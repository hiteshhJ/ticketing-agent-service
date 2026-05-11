package com.sainsburys.agent.service;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import com.azure.core.util.BinaryData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainsburys.agent.model.ChatResponse;
import com.sainsburys.agent.model.Message;
import com.sainsburys.agent.tools.ToolDefinitions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sainsburys.agent.common.AppConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final OpenAIClient openAIClient;
    private final ToolExecutor toolExecutor;
    private final ObjectMapper objectMapper;

    @Value("${azure.openai.deployment-name}")
    private String deploymentName;

    private static final String SYSTEM_PROMPT = """
            You are a helpful AI assistant for the Ticketing system at Sainsbury's.
            
            You help users query:
            - **Promotions** (meal deals, discounts, special offers) from the ecsPromotions collection
            - **Price history** (current and historical prices) from the ecsPrices collection
            - **Offer type details** (offer type descriptions and mechanics) from the offerTypeDetail collection
            
            ## Guidelines:
            - Be concise and helpful
            - When showing data, format it clearly with relevant details
            - If asked about a product, show both promotions AND current price when relevant
            - For date ranges, use ISO format (YYYY-MM-DD)
            - When you encounter an offer type code (e.g., 411, 500), use the get_offer_type_detail or get_all_offer_types tool to look up its meaning and description
            - Price levels indicates either store (40) or Zone (30)
            - Price Codes indicates either store ids (price level 40) or zone ids (price level 30)
            - Always use the provided tools to query real data - never make up information
            - If no results are found, suggest alternative queries
            - When explaining promotions, include offer type descriptions from the offerTypeDetail collection for clarity
            - When the user asks about promotion types (e.g., show me active meal deal promtoions), here "meal deal" can be promo type description or promo mechanic description or reward mechnic type.
             These details are found in offer type table. Find the offer type first then search for promotions with that offer type
            - When user specifically asks for promotions or complex promotions then only query from ecsPromotions otherwise always query from ecsPrices. Offer type is used by ecs promotions
            
            ## Current date: %s
            
            When users ask general questions, help them understand what data is available and how to query it.
            """.formatted(LocalDate.now());

    public ChatResponse runAgent(String userMessage, List<Message> conversationHistory) {
        List<ChatRequestMessage> messages = buildMessages(userMessage, conversationHistory);

        int maxIterations = 10;
        int iterations = 0;
        boolean continueLoop = true;

        while (continueLoop && iterations < maxIterations) {
            iterations++;

            ChatCompletionsOptions options = new ChatCompletionsOptions(messages);
            options.setTools(buildTools());
            options.setTemperature(0.7);
            options.setMaxTokens(1500);

            ChatCompletions chatCompletions = openAIClient.getChatCompletions(deploymentName, options);
            ChatChoice choice = chatCompletions.getChoices().getFirst();
            ChatResponseMessage assistantMessage = choice.getMessage();

//           Add assistant message
            ChatRequestAssistantMessage chatRequestAssistantMessage = new ChatRequestAssistantMessage(assistantMessage.getContent());
            chatRequestAssistantMessage.setToolCalls(assistantMessage.getToolCalls());
            messages.add(chatRequestAssistantMessage);

            // Check for tool calls
            List<ChatCompletionsToolCall> toolCalls = assistantMessage.getToolCalls();
            if (toolCalls != null && !toolCalls.isEmpty()) {
                // Execute all tool calls
                for (ChatCompletionsToolCall toolCall : toolCalls) {
                    if (toolCall instanceof ChatCompletionsFunctionToolCall functionToolCall) {
                        String functionName = functionToolCall.getFunction().getName();
                        String arguments = functionToolCall.getFunction().getArguments();

                        Map<String, Object> result = toolExecutor.executeTool(functionName, arguments);
                        String resultJson = serializeResult(result);

                        // Add tool result message
                        messages.add(new ChatRequestToolMessage(resultJson, functionToolCall.getId()));
                    }
                }
            } else {
                // No more tool calls, we have the final answer
                continueLoop = false;
            }

            // Safety check
            if (canTerminateIterations(choice)) {
                continueLoop = false;
            }
        }

        // Extract final response
        String finalResponse = extractFinalResponse(messages);
        List<Message> updatedHistory = convertToMessageHistory(messages);

        return ChatResponse.builder()
                .response(finalResponse)
                .conversationHistory(updatedHistory)
                .build();
    }

    private static boolean canTerminateIterations(ChatChoice choice) {
        return choice.getFinishReason() == CompletionsFinishReason.STOPPED ||
                choice.getFinishReason() == CompletionsFinishReason.TOKEN_LIMIT_REACHED;
    }

    private static List<ChatRequestMessage> buildMessages(String userMessage, List<Message> history) {
        List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatRequestSystemMessage(SYSTEM_PROMPT));

        // Add conversation history
        for (Message msg : history) {
            if (MESSAGE_ROLE_USER.equals(msg.getRole())) {
                messages.add(new ChatRequestUserMessage(msg.getContent()));
            } else if (MESSAGE_ROLE_ASSISTANT.equals(msg.getRole())) {
                messages.add(new ChatRequestAssistantMessage(msg.getContent()));
            }
        }

        // Add current user message
        messages.add(new ChatRequestUserMessage(userMessage));

        return messages;
    }

    private List<ChatCompletionsToolDefinition> buildTools() {
        List<ChatCompletionsToolDefinition> tools = new ArrayList<>();

        for (Map<String, Object> toolDef : ToolDefinitions.getAllToolDefinitions()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> function = (Map<String, Object>) toolDef.get(TOOL_DEF_FUNCTION);

            FunctionDefinition functionDefinition = new FunctionDefinition(function.get(TOOL_DEF_NAME).toString());
            functionDefinition.setDescription(function.get(TOOL_DEF_DESCRIPTION).toString());
            functionDefinition.setParameters(BinaryData.fromObject(function.get(TOOL_DEF_PARAMETERS)));

            tools.add(new ChatCompletionsFunctionToolDefinition(functionDefinition));
        }

        return tools;
    }

    private String serializeResult(Map<String, Object> result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("Failed to serialize tool result", e);
            return "{\"error\":\"Failed to serialize result\"}";
        }
    }

    private String extractFinalResponse(List<ChatRequestMessage> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatRequestMessage msg = messages.get(i);
            if (msg instanceof ChatRequestAssistantMessage assistantMsg) {
                return assistantMsg.getContent();
            }
        }
        return "I couldn't complete that request.";
    }

    private List<Message> convertToMessageHistory(List<ChatRequestMessage> messages) {
        List<Message> history = new ArrayList<>();

        for (ChatRequestMessage msg : messages) {
            if (msg instanceof ChatRequestSystemMessage) {
                continue; // Skip system prompt
            }

            Message message = new Message();
            if (msg instanceof ChatRequestUserMessage userMsg) {
                message.setRole("user");
                message.setContent(String.valueOf(userMsg.getContent()));
            } else if (msg instanceof ChatRequestAssistantMessage assistantMsg) {
                message.setRole("assistant");
                message.setContent(assistantMsg.getContent());
            }

            if (message.getRole() != null) {
                history.add(message);
            }
        }

        return history;
    }
}

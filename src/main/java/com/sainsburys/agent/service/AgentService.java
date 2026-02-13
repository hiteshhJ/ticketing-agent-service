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
            You are a helpful AI assistant for the Ticketing & Pricing system at Sainsbury's.
            
            You help users query:
            - **Promotions** (meal deals, discounts, special offers) from the ecsPromotions collection
            - **Price history** (current and historical prices) from the ecsPrices collection
            
            ## Guidelines:
            - Be concise and helpful
            - When showing data, format it clearly with relevant details
            - If asked about a product, show both promotions AND current price when relevant
            - For date ranges, use ISO format (YYYY-MM-DD)
            - Offer types: 411 = Mix & Match deals, 500 = Regular pricing
            - Price levels typically indicate store tier (e.g., 30)
            - Always use the provided tools to query real data - never make up information
            - If no results are found, suggest alternative queries
            
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

//            // Add assistant message
//            messages.add(assistantMessage);

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
            if (choice.getFinishReason() == CompletionsFinishReason.STOPPED ||
                    choice.getFinishReason() == CompletionsFinishReason.TOKEN_LIMIT_REACHED) {
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

    private List<ChatRequestMessage> buildMessages(String userMessage, List<Message> history) {
        List<ChatRequestMessage> messages = new ArrayList<>();
        messages.add(new ChatRequestSystemMessage(SYSTEM_PROMPT));

        // Add conversation history
        for (Message msg : history) {
            if ("user".equals(msg.getRole())) {
                messages.add(new ChatRequestUserMessage(msg.getContent()));
            } else if ("assistant".equals(msg.getRole())) {
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
            Map<String, Object> function = (Map<String, Object>) toolDef.get("function");
            String name = (String) function.get("name");
            String description = (String) function.get("description");

            FunctionDefinition functionDefinition = new FunctionDefinition(name);
            functionDefinition.setDescription(description);
            functionDefinition.setParameters(BinaryData.fromObject(function.get("parameters")));

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

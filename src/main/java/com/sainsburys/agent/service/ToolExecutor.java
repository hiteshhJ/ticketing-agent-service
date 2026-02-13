package com.sainsburys.agent.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainsburys.agent.tools.PriceTools;
import com.sainsburys.agent.tools.PromotionTools;
import com.sainsburys.agent.tools.ToolDefinitions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToolExecutor {

    private final PromotionTools promotionTools;
    private final PriceTools priceTools;
    private final ObjectMapper objectMapper;

    public Map<String, Object> executeTool(String functionName, String argumentsJson) {
        try {
            Map<String, Object> params = objectMapper.readValue(
                    argumentsJson,
                    new TypeReference<>() {
                    }
            );

            log.info("🔧 Executing tool: {} with params: {}", functionName, params);

            Map<String, Object> result = switch (functionName) {
                case ToolDefinitions.QUERY_PROMOTIONS -> promotionTools.queryPromotions(params);
                case ToolDefinitions.GET_PROMOTION_BY_ID -> promotionTools.getPromotionById(params);
                case ToolDefinitions.COUNT_PROMOTIONS -> promotionTools.countPromotions(params);
                case ToolDefinitions.QUERY_PRICES -> priceTools.queryPrices(params);
                case ToolDefinitions.GET_PRICE_BY_ID -> priceTools.getPriceById(params);
                case ToolDefinitions.GET_CURRENT_PRICE -> priceTools.getCurrentPrice(params);
                default -> Map.of("error", "Unknown tool: " + functionName);
            };

            log.info("✅ Tool result: {}", result);
            return result;

        } catch (Exception e) {
            log.error("❌ Tool execution error for {}: {}", functionName, e.getMessage(), e);
            return Map.of("error", "Tool execution failed: " + e.getMessage());
        }
    }
}

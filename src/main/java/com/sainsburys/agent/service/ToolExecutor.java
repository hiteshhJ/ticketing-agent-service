package com.sainsburys.agent.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainsburys.agent.tools.OfferTypeTools;
import com.sainsburys.agent.tools.PriceTools;
import com.sainsburys.agent.tools.PromotionTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.sainsburys.agent.common.AppConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToolExecutor {

    private final PromotionTools promotionTools;
    private final PriceTools priceTools;
    private final OfferTypeTools offerTypeTools;
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
                case QUERY_PROMOTIONS -> promotionTools.queryPromotions(params);
                case GET_PROMOTION_BY_ID -> promotionTools.getPromotionById(params);
                case COUNT_PROMOTIONS -> promotionTools.countPromotions(params);
                case QUERY_PRICES -> priceTools.queryPrices(params);
                case GET_PRICE_BY_ID -> priceTools.getPriceById(params);
                case GET_CURRENT_PRICE -> priceTools.getCurrentPrice(params);
                case GET_OFFER_TYPE_DETAIL -> offerTypeTools.getOfferTypeDetail(params);
                case QUERY_OFFER_TYPES -> offerTypeTools.queryOfferTypes(params);
                case GET_ALL_OFFER_TYPES -> offerTypeTools.getAllOfferTypes();
                default -> Map.of(ERROR_MESSAGE, "Unknown tool: " + functionName);
            };

            log.info("✅ Tool result: {}", result);
            return result;

        } catch (Exception e) {
            log.error("❌ Tool execution error for {}: {}", functionName, e.getMessage(), e);
            return Map.of(ERROR_MESSAGE, "Tool execution failed: " + e.getMessage());
        }
    }
}

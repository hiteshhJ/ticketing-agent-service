package com.sainsburys.agent.tools;

import java.util.List;
import java.util.Map;

import static com.sainsburys.agent.common.AppConstant.*;

public class ToolDefinitions {

    private ToolDefinitions() {
        // Private constructor to prevent instantiation
    }

    public static List<Map<String, Object>> getAllToolDefinitions() {
        return List.of(
                createQueryPromotionsTool(),
                createGetPromotionByIdTool(),
                createCountPromotionsTool(),
                createQueryPricesTool(),
                createGetPriceByIdTool(),
                createGetCurrentPriceTool(),
                createGetOfferTypeDetailTool(),
                createQueryOfferTypesTool(),
                createGetAllOfferTypesTool()
        );
    }

    private static Map<String, Object> createQueryPromotionsTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", QUERY_PROMOTIONS,
                        "description", "Query and search for promotions in the ecsPromotions collection. Use this to find promotions by product code, offer type, price level, date range, or promotion name.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "productCode", Map.of("type", "string", "description", "Filter by product code (SKU)"),
                                        "offerType", Map.of("type", "integer", "description", "Filter by offer type (e.g., 411 for mix-match deals, 500 for regular)"),
                                        "priceLevel", Map.of("type", "integer", "description", "Filter by price level"),
                                        "promotionName", Map.of("type", "string", "description", "Search by promotion name (case-insensitive partial match)"),
                                        "limit", Map.of("type", "integer", "description", "Maximum number of results to return (1-100, default 10)")
                                )
                        )
                )
        );
    }

    private static Map<String, Object> createGetPromotionByIdTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", GET_PROMOTION_BY_ID,
                        "description", "Get detailed information about a specific promotion by its ID",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "id", Map.of("type", "string", "description", "The promotion ID (_id field)")
                                ),
                                "required", List.of("id")
                        )
                )
        );
    }

    private static Map<String, Object> createCountPromotionsTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", COUNT_PROMOTIONS,
                        "description", "Count the number of promotions matching specific criteria",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "productCode", Map.of("type", "string", "description", "Filter by product code"),
                                        "offerType", Map.of("type", "integer", "description", "Filter by offer type"),
                                        "published", Map.of("type", "boolean", "description", "Filter by published status")
                                )
                        )
                )
        );
    }

    private static Map<String, Object> createQueryPricesTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", QUERY_PRICES,
                        "description", "Query price history for products. Use this to search for historical and current prices by product code, price level, or date range.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "productCode", Map.of("type", "string", "description", "Filter by product code (SKU)"),
                                        "priceLevel", Map.of("type", "integer", "description", "Filter by price level"),
                                        "priceCode", Map.of("type", "string", "description", "Filter by price code"),
                                        "offerType", Map.of("type", "integer", "description", "Filter by offer type"),
                                        "limit", Map.of("type", "integer", "description", "Maximum number of results to return (1-100, default 10)")
                                )
                        )
                )
        );
    }

    private static Map<String, Object> createGetPriceByIdTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", GET_PRICE_BY_ID,
                        "description", "Get detailed price information by its ID",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "id", Map.of("type", "string", "description", "The price record ID (_id field)")
                                ),
                                "required", List.of("id")
                        )
                )
        );
    }

    private static Map<String, Object> createGetCurrentPriceTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", GET_CURRENT_PRICE,
                        "description", "Get the current active price for a specific product",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "productCode", Map.of("type", "string", "description", "The product code (SKU)"),
                                        "priceLevel", Map.of("type", "integer", "description", "Optional: filter by specific price level")
                                ),
                                "required", List.of("productCode")
                        )
                )
        );
    }

    private static Map<String, Object> createGetOfferTypeDetailTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", GET_OFFER_TYPE_DETAIL,
                        "description", "Get detailed information about a specific offer type including its description, mechanics, and reward type. Use this to understand what an offer type code means.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "offerType", Map.of("type", "string", "description", "The offer type code (e.g., '411', '500')")
                                ),
                                "required", List.of("offerType")
                        )
                )
        );
    }

    private static Map<String, Object> createQueryOfferTypesTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", QUERY_OFFER_TYPES,
                        "description", "Search and filter offer types by promotion type description or reward mechanic type",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "promoTypeDesc", Map.of("type", "string", "description", "Search by promotion type description (case-insensitive partial match)"),
                                        "rewardMechanicType", Map.of("type", "string", "description", "Filter by reward mechanic type"),
                                        "limit", Map.of("type", "integer", "description", "Maximum number of results to return (1-100, default 50)")
                                )
                        )
                )
        );
    }

    private static Map<String, Object> createGetAllOfferTypesTool() {
        return Map.of(
                "type", "function",
                "function", Map.of(
                        "name", GET_ALL_OFFER_TYPES,
                        "description", "Get a complete list of all available offer types with their descriptions and mechanics. Use this when you need to show or reference all offer types.",
                        "parameters", Map.of(
                                "type", "object",
                                "properties", Map.of()
                        )
                )
        );
    }
}

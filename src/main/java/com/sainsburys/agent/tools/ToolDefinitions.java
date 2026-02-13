package com.sainsburys.agent.tools;

import java.util.List;
import java.util.Map;

public class ToolDefinitions {

    private ToolDefinitions() {
        // Private constructor to prevent instantiation
    }

    public static final String QUERY_PROMOTIONS = "query_promotions";
    public static final String GET_PROMOTION_BY_ID = "get_promotion_by_id";
    public static final String COUNT_PROMOTIONS = "count_promotions";
    public static final String QUERY_PRICES = "query_prices";
    public static final String GET_PRICE_BY_ID = "get_price_by_id";
    public static final String GET_CURRENT_PRICE = "get_current_price";

    public static List<Map<String, Object>> getAllToolDefinitions() {
        return List.of(
                createQueryPromotionsTool(),
                createGetPromotionByIdTool(),
                createCountPromotionsTool(),
                createQueryPricesTool(),
                createGetPriceByIdTool(),
                createGetCurrentPriceTool()
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
}

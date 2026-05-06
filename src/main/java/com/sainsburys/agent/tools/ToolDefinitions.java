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
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, QUERY_PROMOTIONS,
                        TOOL_DEF_DESCRIPTION, "Query and search for promotions in the ecsPromotions collection. Use this to find promotions by product code, offer type, price level, date range, or promotion name.",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of(
                                        PARAM_PRICE_LEVEL, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Filter by price level (store or zone)"),
                                        PARAM_PRICE_CODES, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by price code (Store ID or Zone ID)"),
                                        PARAM_PRICE_START_DATE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by price start date (ISO format, e.g., '2024-01-01T00:00:00Z')"),
                                        PARAM_PRICE_END_DATE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by price end date (ISO format, e.g., '2024-01-31T23:59:59Z')"),
                                        PARAM_OFFER_TYPE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Filter by offer type"),
                                        PARAM_IS_ADVERTISING_REQUIRED, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_BOOLEAN, TOOL_DEF_DESCRIPTION, "Filter by whether advertising is required"),
                                        PARAM_PROMOTION_NAME, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Search by promotion name (case-insensitive partial match)"),
                                        PARAM_PRODUCT_CODE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by product code"),
                                        PARAM_IS_PUBLISHED, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_BOOLEAN, TOOL_DEF_DESCRIPTION, "Filter by published status (if published is true that means promotion is published to ECS)"),
                                        PARAM_LIMIT, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Maximum number of results to return (1-100, default 10)")
                                )
                        )
                )
        );
    }

    private static Map<String, Object> createGetPromotionByIdTool() {
        return Map.of(
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, GET_PROMOTION_BY_ID,
                        TOOL_DEF_DESCRIPTION, "Get detailed information about a specific promotion by its ID",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of(
                                        PARAM_ID, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "The promotion ID (_id field)")
                                ),
                                TOOL_DEF_REQUIRED, List.of(PARAM_ID)
                        )
                )
        );
    }

    private static Map<String, Object> createCountPromotionsTool() {
        return Map.of(
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, COUNT_PROMOTIONS,
                        TOOL_DEF_DESCRIPTION, "Count the number of promotions matching specific criteria",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of(
                                        PARAM_PRICE_LEVEL, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Filter by price level (store or zone)"),
                                        PARAM_PRICE_CODES, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by price code (Store ID or Zone ID)"),
                                        PARAM_PRICE_START_DATE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by price start date (ISO format, e.g., '2024-01-01T00:00:00Z')"),
                                        PARAM_PRICE_END_DATE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by price end date (ISO format, e.g., '2024-01-31T23:59:59Z')"),
                                        PARAM_OFFER_TYPE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Filter by offer type"),
                                        PARAM_IS_ADVERTISING_REQUIRED, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_BOOLEAN, TOOL_DEF_DESCRIPTION, "Filter by whether advertising is required"),
                                        PARAM_PROMOTION_NAME, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Search by promotion name (case-insensitive partial match)"),
                                        PARAM_PRODUCT_CODE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by product code"),
                                        PARAM_IS_PUBLISHED, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_BOOLEAN, TOOL_DEF_DESCRIPTION, "Filter by published status (if published is true that means promotion is published to ECS)")
                                )
                        )
                )
        );
    }

    private static Map<String, Object> createQueryPricesTool() {
        return Map.of(
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, QUERY_PRICES,
                        TOOL_DEF_DESCRIPTION, "Query price history for products. Use this to search for historical and current prices by product code, price level, or date range.",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of(
                                        "productCode", Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by product code (SKU)"),
                                        "priceLevel", Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Filter by price level"),
                                        "priceCode", Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Filter by price code"),
                                        "offerType", Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Filter by offer type"),
                                        "limit", Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Maximum number of results to return (1-100, default 10)")
                                )
                        )
                )
        );
    }

    private static Map<String, Object> createGetPriceByIdTool() {
        return Map.of(
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, GET_PRICE_BY_ID,
                        TOOL_DEF_DESCRIPTION, "Get detailed price information by its ID",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of(
                                        "id", Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "The price record ID (_id field)")
                                ),
                                "required", List.of("id")
                        )
                )
        );
    }

    private static Map<String, Object> createGetCurrentPriceTool() {
        return Map.of(
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, GET_CURRENT_PRICE,
                        TOOL_DEF_DESCRIPTION, "Get the current active price for a specific product",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of(
                                        "productCode", Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "The product code (SKU)"),
                                        "priceLevel", Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Optional: filter by specific price level")
                                ),
                                "required", List.of("productCode")
                        )
                )
        );
    }

    private static Map<String, Object> createGetOfferTypeDetailTool() {
        return Map.of(
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, GET_OFFER_TYPE_DETAIL,
                        TOOL_DEF_DESCRIPTION, "Get detailed information about a specific offer type including its description, mechanics, and reward type. Use this to understand what an offer type code means.",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of(
                                        PARAM_OFFER_TYPE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "The offer type code (e.g., '411', '500')")
                                ),
                                TOOL_DEF_REQUIRED, List.of(PARAM_OFFER_TYPE)
                        )
                )
        );
    }

    private static Map<String, Object> createQueryOfferTypesTool() {
        return Map.of(
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, QUERY_OFFER_TYPES,
                        TOOL_DEF_DESCRIPTION, "Search and filter offer types by promotion type description or reward mechanic type or promo mechanic description or threshold",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of(
                                        PARAM_PROMO_TYPE_DESC, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Search by promotion type description (case-insensitive partial match)"),
                                        PARAM_REWARD_MECHANIC_TYPE, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Search by reward mechanic type"),
                                        PARAM_PROMO_MECHANIC_DESC, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_STRING, TOOL_DEF_DESCRIPTION, "Search by promotion mechanic description"),
                                        PARAM_THRESHOLD, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Search by threshold"),
                                        PARAM_LIMIT, Map.of(TOOL_DEF_TYPE, TOOL_DEF_TYPE_INTEGER, TOOL_DEF_DESCRIPTION, "Maximum number of results to return (1-100, default 10)")
                                )
                        )
                )
        );
    }

    private static Map<String, Object> createGetAllOfferTypesTool() {
        return Map.of(
                TOOL_DEF_TYPE, TOOL_DEF_FUNCTION,
                TOOL_DEF_FUNCTION, Map.of(
                        TOOL_DEF_NAME, GET_ALL_OFFER_TYPES,
                        TOOL_DEF_DESCRIPTION, "Get a complete list of all available offer types with their descriptions and mechanics. Use this when you need to show or reference all offer types.",
                        TOOL_DEF_PARAMETERS, Map.of(
                                TOOL_DEF_TYPE, TOOL_DEF_OBJECT,
                                TOOL_DEF_PROPERTIES, Map.of()
                        )
                )
        );
    }
}

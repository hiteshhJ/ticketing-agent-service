package com.sainsburys.agent.common;

public class AppConstant {

    public static final String QUERY_PROMOTIONS = "query_promotions";
    public static final String GET_PROMOTION_BY_ID = "get_promotion_by_id";
    public static final String COUNT_PROMOTIONS = "count_promotions";
    public static final String QUERY_PRICES = "query_prices";
    public static final String GET_PRICE_BY_ID = "get_price_by_id";
    public static final String GET_CURRENT_PRICE = "get_current_price";
    public static final String GET_OFFER_TYPE_DETAIL = "get_coffer_type_detail";
    public static final String QUERY_OFFER_TYPES = "query_offer_types";
    public static final String GET_ALL_OFFER_TYPES = "get_all_offer_types";

    public static final String MESSAGE_ROLE_USER = "user";
    public static final String MESSAGE_ROLE_ASSISTANT = "assistant";
    public static final String ERROR_MESSAGE = "error";
    public static final String KEY_COUNT = "count";
    public static final String KEY_OFFER_TYPES = "offerTypes";

    public static final String PARAM_ID = "id";
    public static final String PARAM_PRODUCT_CODE = "productCode";
    public static final String PARAM_PRICE_LEVEL = "priceLevel";
    public static final String PARAM_PRICE_CODE = "priceCode";
    public static final String PARAM_PRICE_CODES = "priceCodes";
    public static final String PARAM_OFFER_TYPE = "offerType";
    public static final String PARAM_PROMOTION_NAME = "promotionName";
    public static final String PARAM_PUBLISHED = "published";
    public static final String PARAM_PRICE_START_DATE = "priceStartDate";
    public static final String PARAM_PRICE_END_DATE = "priceEndDate";
    public static final String PARAM_IS_ADVERTISING_REQUIRED = "advertisingRequired";
    public static final String PARAM_IS_PUBLISHED = "published";
    public static final String PARAM_PROMO_TYPE_DESC = "promoTypeDesc";
    public static final String PARAM_PROMO_MECHANIC_DESC = "promoMechanicDesc";
    public static final String PARAM_REWARD_MECHANIC_TYPE = "rewardMechanicType";
    public static final String PARAM_THRESHOLD = "threshold";

    public static final String PARAM_LIMIT = "limit";
    public static final int DEFAULT_LIMIT = 10;
    public static final int MAXIMUM_LIMIT = 100;

    public static final String PROP_PRICE_START_DATE = "priceStartDate.$date";

    public static final String TOOL_DEF_TYPE = "type";
    public static final String TOOL_DEF_FUNCTION = "function";
    public static final String TOOL_DEF_NAME = "name";
    public static final String TOOL_DEF_DESCRIPTION = "description";
    public static final String TOOL_DEF_PARAMETERS = "parameters";
    public static final String TOOL_DEF_PROPERTIES = "properties";
    public static final String TOOL_DEF_OBJECT = "object";
    public static final String TOOL_DEF_REQUIRED = "required";
    public static final String TOOL_DEF_TYPE_STRING = "string";
    public static final String TOOL_DEF_TYPE_INTEGER = "integer";
    public static final String TOOL_DEF_TYPE_BOOLEAN = "boolean";

    private AppConstant() {
        /* This utility class should not be instantiated */
    }
}

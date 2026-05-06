package com.sainsburys.agent.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainsburys.agent.model.EcsPromotion;
import com.sainsburys.agent.repository.PromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.sainsburys.agent.common.AppConstant.*;

@Slf4j
@Component
public class PromotionTools {

    private final PromotionRepository promotionRepository;
    private final MongoTemplate promotionMongoTemplate;
    private final ObjectMapper objectMapper;

    public PromotionTools(PromotionRepository promotionRepository,
                          @Qualifier("promotionMongoTemplate") MongoTemplate promotionMongoTemplate,
                          ObjectMapper objectMapper) {
        this.promotionRepository = promotionRepository;
        this.promotionMongoTemplate = promotionMongoTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> queryPromotions(Map<String, Object> params) {
        try {
            Query query = new Query();

            if (params.containsKey(PARAM_PRODUCT_CODE)) {
                query.addCriteria(Criteria.where("priceDetails.offerDetailList.productCode")
                        .is(params.get(PARAM_PRODUCT_CODE)));
            }

            if (params.containsKey(PARAM_OFFER_TYPE)) {
                query.addCriteria(Criteria.where(PARAM_OFFER_TYPE).is(params.get(PARAM_OFFER_TYPE)));
            }

            if (params.containsKey(PARAM_PRICE_LEVEL)) {
                query.addCriteria(Criteria.where(PARAM_PRICE_LEVEL).is(params.get(PARAM_PRICE_LEVEL)));
            }

            if (params.containsKey(PARAM_PROMOTION_NAME)) {
                query.addCriteria(Criteria.where(PARAM_PROMOTION_NAME)
                        .regex((String) params.get(PARAM_PROMOTION_NAME), "i"));
            }

            int limit = params.containsKey("limit") ?
                    ((Number) params.get("limit")).intValue() : 10;
            query.limit(Math.min(limit, 100));

            List<EcsPromotion> results = promotionMongoTemplate.find(query, EcsPromotion.class);

            List<Map<String, Object>> simplified = results.stream()
                    .map(this::simplifyPromotion)
                    .toList();

            return Map.of(
                    "count", results.size(),
                    "promotions", simplified
            );
        } catch (Exception e) {
            log.error("Error querying promotions", e);
            return Map.of(ERROR_MESSAGE, "Failed to query promotions: " + e.getMessage());
        }
    }

    public Map<String, Object> getPromotionById(Map<String, Object> params) {
        try {
            String id = (String) params.get("id");
            if (id == null) {
                return Map.of(ERROR_MESSAGE, "ID is required");
            }

            Optional<EcsPromotion> promotion = promotionRepository.findById(id);
            if (promotion.isEmpty()) {
                return Map.of(ERROR_MESSAGE, "Promotion not found");
            }

            return objectMapper.convertValue(promotion.get(), Map.class);
        } catch (Exception e) {
            log.error("Error getting promotion by ID", e);
            return Map.of(ERROR_MESSAGE, "Failed to get promotion: " + e.getMessage());
        }
    }

    public Map<String, Object> countPromotions(Map<String, Object> params) {
        try {
            Query query = new Query();

            if (params.containsKey(PARAM_PRODUCT_CODE)) {
                query.addCriteria(Criteria.where("priceDetails.offerDetailList.productCode")
                        .is(params.get(PARAM_PRODUCT_CODE)));
            }

            if (params.containsKey(PARAM_OFFER_TYPE)) {
                query.addCriteria(Criteria.where(PARAM_OFFER_TYPE).is(params.get(PARAM_OFFER_TYPE)));
            }

            if (params.containsKey(PARAM_PUBLISHED)) {
                query.addCriteria(Criteria.where(PARAM_PUBLISHED).is(params.get(PARAM_PUBLISHED)));
            }

            long count = promotionMongoTemplate.count(query, EcsPromotion.class);

            return Map.of("count", count);
        } catch (Exception e) {
            log.error("Error counting promotions", e);
            return Map.of(ERROR_MESSAGE, "Failed to count promotions: " + e.getMessage());
        }
    }

    private Map<String, Object> simplifyPromotion(EcsPromotion p) {
        List<Map<String, Object>> products = p.getPriceDetails() != null ?
                p.getPriceDetails().stream()
                .filter(d -> "PRODUCTS".equals(d.getOfferDetailName()))
                .flatMap(d -> d.getOfferDetailList() != null ?
                        d.getOfferDetailList().stream() : List.<EcsPromotion.ProductDetail>of().stream())
                .map(product -> Map.<String, Object>of(
                        PARAM_PRODUCT_CODE, product.getProductCode(),
                        "productTypeCode", product.getProductTypeCode(),
                        "productGroupNumber", product.getProductGroupNumber()
                ))
                .toList()
                : List.of();

        return Map.of(
                "id", p.getId(),
                PARAM_PROMOTION_NAME, p.getPromotionName() != null ? p.getPromotionName() : "",
                PARAM_OFFER_TYPE, p.getOfferType() != null ? p.getOfferType() : 0,
                PARAM_PRICE_LEVEL, p.getPriceLevel() != null ? p.getPriceLevel() : 0,
                "startDate", p.getPriceStartDate() != null ?
                        p.getPriceStartDate().toString() : "",
                "endDate", p.getPriceEndDate() != null ?
                        p.getPriceEndDate().toString() : "",
                "canTrigger", p.getCanTrigger() != null && p.getCanTrigger(),
                PARAM_PUBLISHED, p.getPublished() != null && p.getPublished(),
                "products", products
        );
    }
}

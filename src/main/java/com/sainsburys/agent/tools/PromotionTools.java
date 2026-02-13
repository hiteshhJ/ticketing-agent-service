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

            if (params.containsKey("productCode")) {
                query.addCriteria(Criteria.where("priceDetails.offerDetailList.productCode")
                        .is(params.get("productCode")));
            }

            if (params.containsKey("offerType")) {
                query.addCriteria(Criteria.where("offerType").is(params.get("offerType")));
            }

            if (params.containsKey("priceLevel")) {
                query.addCriteria(Criteria.where("priceLevel").is(params.get("priceLevel")));
            }

            if (params.containsKey("promotionName")) {
                query.addCriteria(Criteria.where("promotionName")
                        .regex((String) params.get("promotionName"), "i"));
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
            return Map.of("error", "Failed to query promotions: " + e.getMessage());
        }
    }

    public Map<String, Object> getPromotionById(Map<String, Object> params) {
        try {
            String id = (String) params.get("id");
            if (id == null) {
                return Map.of("error", "ID is required");
            }

            Optional<EcsPromotion> promotion = promotionRepository.findById(id);
            if (promotion.isEmpty()) {
                return Map.of("error", "Promotion not found");
            }

            return objectMapper.convertValue(promotion.get(), Map.class);
        } catch (Exception e) {
            log.error("Error getting promotion by ID", e);
            return Map.of("error", "Failed to get promotion: " + e.getMessage());
        }
    }

    public Map<String, Object> countPromotions(Map<String, Object> params) {
        try {
            Query query = new Query();

            if (params.containsKey("productCode")) {
                query.addCriteria(Criteria.where("priceDetails.offerDetailList.productCode")
                        .is(params.get("productCode")));
            }

            if (params.containsKey("offerType")) {
                query.addCriteria(Criteria.where("offerType").is(params.get("offerType")));
            }

            if (params.containsKey("published")) {
                query.addCriteria(Criteria.where("published").is(params.get("published")));
            }

            long count = promotionMongoTemplate.count(query, EcsPromotion.class);

            return Map.of("count", count);
        } catch (Exception e) {
            log.error("Error counting promotions", e);
            return Map.of("error", "Failed to count promotions: " + e.getMessage());
        }
    }

    private Map<String, Object> simplifyPromotion(EcsPromotion p) {
        List<Map<String, Object>> products = p.getPriceDetails() != null ?
                p.getPriceDetails().stream()
                        .filter(d -> "PRODUCTS".equals(d.getOfferDetailName()))
                        .flatMap(d -> d.getOfferDetailList() != null ?
                                d.getOfferDetailList().stream() : List.<EcsPromotion.ProductDetail>of().stream())
                        .map(product -> Map.<String, Object>of(
                                "productCode", product.getProductCode(),
                                "productTypeCode", product.getProductTypeCode(),
                                "productGroupNumber", product.getProductGroupNumber()
                        ))
                        .toList()
                : List.of();

        return Map.of(
                "id", p.getId(),
                "promotionName", p.getPromotionName() != null ? p.getPromotionName() : "",
                "offerType", p.getOfferType() != null ? p.getOfferType() : 0,
                "priceLevel", p.getPriceLevel() != null ? p.getPriceLevel() : 0,
                "startDate", p.getPriceStartDate() != null && p.getPriceStartDate().getDate() != null ?
                        p.getPriceStartDate().getDate().toString() : "",
                "endDate", p.getPriceEndDate() != null && p.getPriceEndDate().getDate() != null ?
                        p.getPriceEndDate().getDate().toString() : "",
                "canTrigger", p.getCanTrigger() != null && p.getCanTrigger(),
                "published", p.getPublished() != null && p.getPublished(),
                "products", products
        );
    }
}

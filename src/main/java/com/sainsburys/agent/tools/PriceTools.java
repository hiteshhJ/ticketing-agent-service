package com.sainsburys.agent.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainsburys.agent.model.EcsPrice;
import com.sainsburys.agent.repository.PriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.sainsburys.agent.common.AppConstant.*;

@Slf4j
@Component
public class PriceTools {

    private final PriceRepository priceRepository;
    private final MongoTemplate priceHistoryMongoTemplate;
    private final ObjectMapper objectMapper;

    public PriceTools(PriceRepository priceRepository, @Qualifier("priceHistoryMongoTemplate") MongoTemplate priceHistoryMongoTemplate, ObjectMapper objectMapper) {
        this.priceRepository = priceRepository;
        this.priceHistoryMongoTemplate = priceHistoryMongoTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> queryPrices(Map<String, Object> params) {
        try {
            Query query = new Query();

            PromotionTools.addQueryCriteria(params, query);

            int limit = params.containsKey(PARAM_LIMIT) ?
                    ((Number) params.get(PARAM_LIMIT)).intValue() : DEFAULT_LIMIT;
            query.limit(Math.min(limit, MAXIMUM_LIMIT));

            query.with(Sort.by(Sort.Direction.DESC, PROP_PRICE_START_DATE));

            List<EcsPrice> results = priceHistoryMongoTemplate.find(query, EcsPrice.class);

            List<Map<String, Object>> simplified = results.stream()
                    .map(this::simplifyPrice).toList();

            return Map.of(
                    "count", results.size(),
                    "prices", simplified
            );
        } catch (Exception e) {
            log.error("Error querying prices", e);
            return Map.of(ERROR_MESSAGE, "Failed to query prices: " + e.getMessage());
        }
    }

    public Map<String, Object> getPriceById(Map<String, Object> params) {
        try {
            String id = (String) params.get("id");
            if (id == null) {
                return Map.of(ERROR_MESSAGE, "ID is required");
            }

            Optional<EcsPrice> price = priceRepository.findById(id);
            if (price.isEmpty()) {
                return Map.of(ERROR_MESSAGE, "Price not found");
            }

            return objectMapper.convertValue(price.get(), Map.class);
        } catch (Exception e) {
            log.error("Error getting price by ID", e);
            return Map.of("error", "Failed to get price: " + e.getMessage());
        }
    }

    public Map<String, Object> getCurrentPrice(Map<String, Object> params) {
        try {
            String productCode = (String) params.get("productCode");
            if (productCode == null) {
                return Map.of("error", "Product code is required");
            }

            Instant now = Instant.now();
            Query query = new Query();
            query.addCriteria(Criteria.where("productCode").is(productCode));
            query.addCriteria(Criteria.where(PROP_PRICE_START_DATE).lte(now));
            query.addCriteria(Criteria.where("priceEndDate.$date").gte(now));

            if (params.containsKey("priceLevel")) {
                query.addCriteria(Criteria.where("priceLevel").is(params.get("priceLevel")));
            }

            query.with(Sort.by(Sort.Direction.DESC, PROP_PRICE_START_DATE));
            query.limit(1);

            EcsPrice currentPrice = priceHistoryMongoTemplate.findOne(query, EcsPrice.class);

            if (currentPrice == null) {
                return Map.of("error", "No current price found for this product");
            }

            String price = extractPriceDetail(currentPrice, "PRICE");
            String previousPrice = extractPriceDetail(currentPrice, "PREVIOUS_PRICE");

            return Map.of(
                    "productCode", currentPrice.getProductCode(),
                    "currentPrice", price != null ? price : "",
                    "previousPrice", previousPrice != null ? previousPrice : "",
                    "priceLevel", currentPrice.getPriceLevel() != null ? currentPrice.getPriceLevel() : 0,
                    "priceCode", currentPrice.getPriceCode() != null ? currentPrice.getPriceCode() : "",
                    "promotionName", currentPrice.getPromotionName() != null ? currentPrice.getPromotionName() : "",
                    "validFrom", currentPrice.getPriceStartDate() != null ?
                            currentPrice.getPriceStartDate().toString() : "",
                    "validUntil", currentPrice.getPriceEndDate() != null ?
                            currentPrice.getPriceEndDate().toString() : ""
            );
        } catch (Exception e) {
            log.error("Error getting current price", e);
            return Map.of("error", "Failed to get current price: " + e.getMessage());
        }
    }

    private Map<String, Object> simplifyPrice(EcsPrice p) {
        String price = extractPriceDetail(p, "PRICE");
        String previousPrice = extractPriceDetail(p, "PREVIOUS_PRICE");

        Map<String, Object> simplifiedPrice = new java.util.HashMap<>(Map.of("id", p.getId(), "productCode", p.getProductCode() != null ? p.getProductCode() : "",
                "productTypeCode", p.getProductTypeCode() != null ? p.getProductTypeCode() : "",
                "priceLevel", p.getPriceLevel() != null ? p.getPriceLevel() : 0,
                "priceCode", p.getPriceCode() != null ? p.getPriceCode() : "",
                "price", price != null ? price : "",
                "previousPrice", previousPrice != null ? previousPrice : "",
                "startDate", p.getPriceStartDate() != null ?
                        p.getPriceStartDate().toString() : "",
                "endDate", p.getPriceEndDate() != null ?
                        p.getPriceEndDate().toString() : "",
                "offerType", p.getOfferType() != null ? p.getOfferType() : 0
        ));

        simplifiedPrice.put("promotionCode", p.getPromotionCode() != null ? p.getPromotionCode() : "");
        simplifiedPrice.put("promotionName", p.getPromotionName() != null ? p.getPromotionName() : "");
        simplifiedPrice.put("canTrigger", p.getCanTrigger() != null && p.getCanTrigger());
        simplifiedPrice.put("published", p.getPublished() != null && p.getPublished());

        return simplifiedPrice;
    }

    private String extractPriceDetail(EcsPrice price, String detailName) {
        if (price.getPriceDetails() == null) {
            return null;
        }
        return price.getPriceDetails().stream()
                .filter(d -> detailName.equals(d.getOfferDetailName()))
                .map(EcsPrice.PriceDetail::getOfferDetailValue)
                .findFirst()
                .orElse(null);
    }
}

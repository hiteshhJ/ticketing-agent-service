package com.sainsburys.agent.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainsburys.agent.model.OfferTypeDetail;
import com.sainsburys.agent.repository.OfferTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.sainsburys.agent.common.AppConstant.*;

@Slf4j
@Component
public class OfferTypeTools {

    private final OfferTypeRepository offerTypeRepository;
    private final MongoTemplate promotionMongoTemplate;
    private final ObjectMapper objectMapper;

    public OfferTypeTools(OfferTypeRepository offerTypeRepository,
                          @Qualifier("promotionMongoTemplate") MongoTemplate promotionMongoTemplate,
                          ObjectMapper objectMapper) {
        this.offerTypeRepository = offerTypeRepository;
        this.promotionMongoTemplate = promotionMongoTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Get offer type details by offer type code
     */
    public Map<String, Object> getOfferTypeDetail(Map<String, Object> params) {
        try {
            String offerType = (String) params.get("offerType");
            if (offerType == null) {
                return Map.of(ERROR_MESSAGE, "offerType is required");
            }

            Optional<OfferTypeDetail> offerTypeDetail = offerTypeRepository.findByOfferType(offerType);
            if (offerTypeDetail.isEmpty()) {
                return Map.of(ERROR_MESSAGE, "Offer type " + offerType + " not found");
            }

            return objectMapper.convertValue(offerTypeDetail.get(), Map.class);
        } catch (Exception e) {
            log.error("Error getting offer type detail", e);
            return Map.of(ERROR_MESSAGE, "Failed to get offer type detail: " + e.getMessage());
        }
    }

    /**
     * Query all offer types or filter by specific criteria
     */
    public Map<String, Object> queryOfferTypes(Map<String, Object> params) {
        try {
            Query query = new Query();

            if (params.containsKey(PROMO_TYPE_DESC)) {
                query.addCriteria(Criteria.where(PROMO_TYPE_DESC)
                        .regex((String) params.get(PROMO_TYPE_DESC), "i"));
            }

            if (params.containsKey(REWARD_MECHANIC_TYPE)) {
                query.addCriteria(Criteria.where(REWARD_MECHANIC_TYPE)
                        .is(params.get(REWARD_MECHANIC_TYPE)));
            }

            int limit = params.containsKey("limit") ?
                    ((Number) params.get("limit")).intValue() : 50;
            query.limit(Math.min(limit, 100));

            List<OfferTypeDetail> results = promotionMongoTemplate.find(query, OfferTypeDetail.class, "offerTypeDetail");

            List<Map<String, Object>> simplified = results.stream()
                    .map(this::simplifyOfferType)
                    .toList();

            return Map.of(
                    "count", results.size(),
                    "offerTypes", simplified
            );
        } catch (Exception e) {
            log.error("Error querying offer types", e);
            return Map.of("error", "Failed to query offer types: " + e.getMessage());
        }
    }

    /**
     * Get all offer types for reference
     */
    public Map<String, Object> getAllOfferTypes() {
        try {
            List<OfferTypeDetail> allOfferTypes = offerTypeRepository.findAll();

            List<Map<String, Object>> simplified = allOfferTypes.stream()
                    .map(this::simplifyOfferType)
                    .toList();

            return Map.of(
                    "count", allOfferTypes.size(),
                    "offerTypes", simplified
            );
        } catch (Exception e) {
            log.error("Error getting all offer types", e);
            return Map.of("error", "Failed to get all offer types: " + e.getMessage());
        }
    }

    private Map<String, Object> simplifyOfferType(OfferTypeDetail offerType) {
        Map<String, Object> simple = new HashMap<>();
        simple.put("offerType", offerType.getOfferType());
        simple.put("promoTypeDesc", offerType.getPromoTypeDesc());
        simple.put("promoMechanicDesc", offerType.getPromoMechanicDesc());
        simple.put("rewardMechanicType", offerType.getRewardMechanicType());
        simple.put("threshold", offerType.getThreshold());
        return simple;
    }
}


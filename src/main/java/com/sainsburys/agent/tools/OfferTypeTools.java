package com.sainsburys.agent.tools;

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

import static com.sainsburys.agent.common.AppConstant.*;

@Slf4j
@Component
public class OfferTypeTools {

    private final OfferTypeRepository offerTypeRepository;
    private final MongoTemplate promotionMongoTemplate;

    public OfferTypeTools(OfferTypeRepository offerTypeRepository,
                          @Qualifier("promotionMongoTemplate") MongoTemplate promotionMongoTemplate) {
        this.offerTypeRepository = offerTypeRepository;
        this.promotionMongoTemplate = promotionMongoTemplate;
    }

    /**
     * Get offer type details by offer type code
     */
    public Map<String, Object> getOfferTypeDetail(Map<String, Object> params) {
        try {
            String offerType = (String) params.get(PARAM_OFFER_TYPE);
            if (offerType == null) {
                return Map.of(ERROR_MESSAGE, "offerType is required");
            }

            List<OfferTypeDetail> offerTypeDetail = offerTypeRepository.findByOfferType(offerType);
            if (offerTypeDetail.isEmpty()) {
                return Map.of(ERROR_MESSAGE, "Offer type " + offerType + " not found");
            }

            List<Map<String, Object>> simplified = offerTypeDetail.stream()
                    .map(this::simplifyOfferType)
                    .toList();

            return Map.of(
                    KEY_COUNT, offerTypeDetail.size(),
                    KEY_OFFER_TYPES, simplified
            );
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

            if (params.containsKey(PARAM_PROMO_TYPE_DESC)) {
                query.addCriteria(Criteria.where(PARAM_PROMO_TYPE_DESC)
                        .regex((String) params.get(PARAM_PROMO_TYPE_DESC), "i"));
            }

            if (params.containsKey(PARAM_PROMO_MECHANIC_DESC)) {
                query.addCriteria(Criteria.where(PARAM_PROMO_MECHANIC_DESC)
                        .regex((String) params.get(PARAM_PROMO_MECHANIC_DESC), "i"));
            }

            if (params.containsKey(PARAM_REWARD_MECHANIC_TYPE)) {
                query.addCriteria(Criteria.where(PARAM_REWARD_MECHANIC_TYPE)
                        .is(params.get(PARAM_REWARD_MECHANIC_TYPE)));
            }

            if (params.containsKey(PARAM_THRESHOLD)) {
                query.addCriteria(Criteria.where(PARAM_THRESHOLD)
                        .is(params.get(PARAM_THRESHOLD)));
            }

            int limit = params.containsKey(PARAM_LIMIT) ?
                    ((Number) params.get(PARAM_LIMIT)).intValue() : DEFAULT_LIMIT;
            query.limit(Math.min(limit, MAXIMUM_LIMIT));

            List<OfferTypeDetail> results = promotionMongoTemplate.find(query, OfferTypeDetail.class, "offerTypeDetail");

            List<Map<String, Object>> simplified = results.stream()
                    .map(this::simplifyOfferType)
                    .toList();

            return Map.of(
                    KEY_COUNT, results.size(),
                    KEY_OFFER_TYPES, simplified
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
                    KEY_COUNT, allOfferTypes.size(),
                    KEY_OFFER_TYPES, simplified
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


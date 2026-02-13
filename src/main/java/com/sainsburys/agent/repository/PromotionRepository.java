package com.sainsburys.agent.repository;

import com.sainsburys.agent.model.EcsPromotion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends MongoRepository<EcsPromotion, String> {

    @Query("{ 'priceDetails.offerDetailList.productCode': ?0 }")
    List<EcsPromotion> findByProductCode(String productCode);

    List<EcsPromotion> findByOfferType(Integer offerType);

    List<EcsPromotion> findByPriceLevel(Integer priceLevel);

    List<EcsPromotion> findByPublished(Boolean published);

    @Query("{ 'promotionName': { $regex: ?0, $options: 'i' } }")
    List<EcsPromotion> findByPromotionNameContaining(String name);
}

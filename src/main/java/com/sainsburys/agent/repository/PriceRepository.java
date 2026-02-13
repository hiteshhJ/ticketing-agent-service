package com.sainsburys.agent.repository;

import com.sainsburys.agent.model.EcsPrice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends MongoRepository<EcsPrice, String> {

    List<EcsPrice> findByProductCode(String productCode);

    List<EcsPrice> findByProductCodeAndPriceLevel(String productCode, Integer priceLevel);

    List<EcsPrice> findByPriceLevel(Integer priceLevel);

    List<EcsPrice> findByPriceCode(String priceCode);

    List<EcsPrice> findByOfferType(Integer offerType);
}

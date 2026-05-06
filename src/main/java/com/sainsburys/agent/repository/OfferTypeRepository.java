package com.sainsburys.agent.repository;

import com.sainsburys.agent.model.OfferTypeDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfferTypeRepository extends MongoRepository<OfferTypeDetail, String> {
    
    Optional<OfferTypeDetail> findByOfferType(String offerType);
}


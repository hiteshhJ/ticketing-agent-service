package com.sainsburys.agent.repository;

import com.sainsburys.agent.model.OfferTypeDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferTypeRepository extends MongoRepository<OfferTypeDetail, String> {

    List<OfferTypeDetail> findByOfferType(String offerType);
}


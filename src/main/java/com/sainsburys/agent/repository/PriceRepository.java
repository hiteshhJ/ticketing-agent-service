package com.sainsburys.agent.repository;

import com.sainsburys.agent.model.EcsPrice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PriceRepository extends MongoRepository<EcsPrice, String> {

}

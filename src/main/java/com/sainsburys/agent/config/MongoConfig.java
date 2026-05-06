package com.sainsburys.agent.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.promotion-uri}")
    private String promotionUri;

    @Value("${spring.data.mongodb.price-history-uri}")
    private String priceHistoryUri;

    @Bean(name = "promotionMongoClient")
    public MongoClient promotionMongoClient() {
        return MongoClients.create(promotionUri);
    }

    @Bean(name = "priceHistoryMongoClient")
    public MongoClient priceHistoryMongoClient() {
        return MongoClients.create(priceHistoryUri);
    }

    @Bean(name = "promotionMongoTemplate")
    public MongoTemplate promotionMongoTemplate() {
        return new MongoTemplate(promotionMongoClient(), "promotionDB");
    }

    @Bean(name = "priceHistoryMongoTemplate")
    public MongoTemplate priceHistoryMongoTemplate() {
        return new MongoTemplate(priceHistoryMongoClient(), "priceHistoryDB");
    }
}

@Configuration
@EnableMongoRepositories(
        basePackages = "com.sainsburys.agent.repository",
        mongoTemplateRef = "promotionMongoTemplate",
        includeFilters = {
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.sainsburys.agent.repository.PromotionRepository.class,
                                com.sainsburys.agent.repository.OfferTypeRepository.class
                        }
                )
        }
)
class PromotionMongoConfig {
}

@Configuration
@EnableMongoRepositories(
        basePackages = "com.sainsburys.agent.repository",
        mongoTemplateRef = "priceHistoryMongoTemplate",
        includeFilters = {
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
                        classes = com.sainsburys.agent.repository.PriceRepository.class
                )
        }
)
class PriceHistoryMongoConfig {
}


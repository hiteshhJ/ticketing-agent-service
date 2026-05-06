package com.sainsburys.agent.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "offerTypeDetail")
public class OfferTypeDetail {
    @Id
    private String id;
    private String offerType;
    private Integer threshold;
    private String promoTypeDesc;
    private String promoMechanicDesc;
    private String rewardMechanicType;
}


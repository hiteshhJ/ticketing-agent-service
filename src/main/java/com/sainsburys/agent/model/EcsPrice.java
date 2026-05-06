package com.sainsburys.agent.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "ecsPrices")
public class EcsPrice {
    @Id
    private String id;

    private String headerType;
    private String userName;
    private String token;
    private Integer eventUID;
    private String priceUID;
    private String productCode;
    private String productTypeCode;
    private Integer priceLevel;
    private String priceCode;

    @Field("priceStartDate")
    private Date priceStartDate;

    @Field("priceEndDate")
    private Date priceEndDate;

    private Integer offerType;
    private Boolean canTrigger;
    private String promotionCode;
    private String promotionName;
    private Boolean checkProduct;
    private Boolean advertisingRequired;
    private List<PriceDetail> priceDetails;
    private Boolean published;

    @Field("createdDateTime")
    private Date createdDateTime;

    @Field("lastUpdatedDateTime")
    private Date lastUpdatedDateTime;

    private Long version;

    @JsonProperty("_class")
    private String classType;

    @Data
    public static class PriceDetail {
        private String offerDetailName;
        private String offerDetailValue;
    }
}

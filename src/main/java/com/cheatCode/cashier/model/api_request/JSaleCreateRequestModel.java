package com.cheatCode.cashier.model.api_request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class JSaleCreateRequestModel {
    
    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("email")
    private String email;
}

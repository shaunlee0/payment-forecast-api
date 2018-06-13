package com.cft.paymentforecast.domain.business;

import lombok.Data;

@Data
public class Merchant {

    private String name;
    private String pubKey;
    private Integer id;

    public Merchant(String name, String pubKey, Integer id) {
        this.name = name;
        this.pubKey = pubKey;
        this.id = id;
    }
}

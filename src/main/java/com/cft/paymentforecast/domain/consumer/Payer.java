package com.cft.paymentforecast.domain.consumer;

import lombok.Data;

@Data
public class Payer {

    private Integer id;
    private String pubKey;
    private Integer debitPermissionId;

    public Payer(Integer id, String pubKey, Integer debitPermissionId) {
        this.id = id;
        this.pubKey = pubKey;
        this.debitPermissionId = debitPermissionId;
    }
}

package com.cft.paymentforecast.domain.consumer;

import lombok.Data;

@Data
public class Payer {

    private Integer id;
    private String pubKey;
    private Integer debitPermissionId;

}

package com.cft.paymentforecast.domain.payment;

import lombok.Data;

import java.util.Currency;
import java.util.Date;

@Data
public class Payment {

    private Date receivedOn;
    private Date dueOn;
    private Long dueOnEpoc;
    private Currency currency;
    private Double amount;

}

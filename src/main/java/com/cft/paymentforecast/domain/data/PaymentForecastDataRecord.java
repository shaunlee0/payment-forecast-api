package com.cft.paymentforecast.domain.data;

import com.cft.paymentforecast.domain.business.Merchant;
import com.cft.paymentforecast.domain.consumer.Payer;
import com.cft.paymentforecast.domain.payment.Payment;
import lombok.Data;

@Data
class PaymentForecastDataRecord {

    private Merchant merchant;
    private Payer payer;
    private Payment payment;
    private String sha256;

    public PaymentForecastDataRecord(Merchant merchant, Payer payer, Payment payment, String sha256) {
        this.merchant = merchant;
        this.payer = payer;
        this.payment = payment;
        this.sha256 = sha256;
    }
}

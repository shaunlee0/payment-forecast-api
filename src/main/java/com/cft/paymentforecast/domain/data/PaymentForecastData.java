package com.cft.paymentforecast.domain.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentForecastData {
    private List<PaymentForecastDataRecord> records;

    public PaymentForecastData() {
        this.records = new ArrayList<>();
    }
}

package com.cft.paymentforecast.domain.forecast;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentForecast {
    List<PaymentForecastDay> days;

    public PaymentForecast() {
        days = new ArrayList<>();
    }
}

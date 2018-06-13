package com.cft.paymentforecast.forecast;

import com.cft.paymentforecast.domain.business.Merchant;
import com.cft.paymentforecast.domain.data.PaymentForecastDataRecord;
import com.cft.paymentforecast.domain.forecast.PaymentForecast;
import com.cft.paymentforecast.domain.forecast.PaymentForecastDay;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PaymentForecastProcessor {

    public PaymentForecast calcuclateOwedAmountsByMerchant(PaymentForecast paymentForecast) {
        for (PaymentForecastDay day : paymentForecast.getDays()) {
            HashMap<Merchant, Double> merchantOwedPerDay = new HashMap<>();
            for (PaymentForecastDataRecord record : day.getRecords()) {
                Merchant merchantOwedForRecord = record.getMerchant();

                if (merchantOwedPerDay.containsKey(merchantOwedForRecord)) {
                    Double existingValue = merchantOwedPerDay.get(merchantOwedForRecord);
                    merchantOwedPerDay.put(
                            merchantOwedForRecord, existingValue + record.getPayment().getAmount());
                } else {
                    merchantOwedPerDay.put(merchantOwedForRecord, record.getPayment().getAmount());
                }
            }
            day.setMerchantOwedForDay(merchantOwedPerDay);
        }

        return paymentForecast;
    }
}

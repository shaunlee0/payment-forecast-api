package com.cft.paymentforecast.forecast;

import com.cft.paymentforecast.domain.business.Merchant;
import com.cft.paymentforecast.domain.data.PaymentForecastDataRecord;
import com.cft.paymentforecast.domain.forecast.PaymentForecast;
import com.cft.paymentforecast.domain.forecast.PaymentForecastDay;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
public class PaymentForecastProcessor {
    DecimalFormat df = new DecimalFormat("##.##");

    public PaymentForecast calcuclateOwedAmountsByMerchant(PaymentForecast paymentForecast) {

        Set<String> allMerchants = new HashSet<>();

        for (PaymentForecastDay day : paymentForecast.getDays()) {
            day.setMerchantOwedForDay(calculateMerchantOwedPerDay(day, allMerchants));
        }

        for (PaymentForecastDay day : paymentForecast.getDays()) {
            day.setMerchantOwedForDay(aggregateDailySummaryForAllMerchants(day.getMerchantOwedForDay(), allMerchants));
        }

        return paymentForecast;
    }

    private HashMap<String, String> aggregateDailySummaryForAllMerchants(HashMap<String, String> merchantOwedPerDay, Set<String> allMerchants) {
        HashMap<String, String> aggregatedSummariesForAllMerchants = new HashMap<>();

        for (String merchant : allMerchants) {
            String value = merchantOwedPerDay.get(merchant);
            if (value == null) {
                aggregatedSummariesForAllMerchants.put(merchant, "0");
            } else {
                aggregatedSummariesForAllMerchants.put(merchant, value);
            }
        }

        return aggregatedSummariesForAllMerchants;
    }

    private HashMap<String, String> calculateMerchantOwedPerDay(PaymentForecastDay day, Set<String> allMerchants) {
        HashMap<String, String> merchantOwedPerDay = new HashMap<>();
        day.getRecords().forEach(record -> {
            String merchantOwedForRecord = record.getMerchant().getName();

            if (!merchantOwedForRecord.equals("")) {
                allMerchants.add(merchantOwedForRecord);
                if (merchantOwedPerDay.containsKey(merchantOwedForRecord)) {
                    Double existingValue = Double.valueOf(merchantOwedPerDay.get(merchantOwedForRecord));
                    merchantOwedPerDay.put(
                            merchantOwedForRecord, df.format(existingValue + record.getPayment().getAmount()));
                } else {
                    merchantOwedPerDay.put(merchantOwedForRecord, df.format(record.getPayment().getAmount()));
                }
            }

        });

        return merchantOwedPerDay;

    }
}

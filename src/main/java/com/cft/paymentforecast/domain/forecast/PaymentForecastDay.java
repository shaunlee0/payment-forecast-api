package com.cft.paymentforecast.domain.forecast;

import com.cft.paymentforecast.domain.business.Merchant;
import com.cft.paymentforecast.domain.data.PaymentForecastDataRecord;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
public class PaymentForecastDay {
    private Date day;
    private List<PaymentForecastDataRecord> records;
    private String dayLabel;
    private HashMap<Merchant,Double> merchantOwedForDay;

}

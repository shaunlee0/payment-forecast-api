package com.cft.paymentforecast.controllers;

import com.cft.paymentforecast.domain.data.PaymentForecastData;
import com.cft.paymentforecast.domain.data.PaymentForecastDataRecord;
import com.cft.paymentforecast.domain.forecast.PaymentForecast;
import com.cft.paymentforecast.domain.forecast.PaymentForecastDay;
import com.cft.paymentforecast.forecast.PaymentForecastBuilder;
import com.cft.paymentforecast.forecast.PaymentForecastProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2(topic = "General")
@Controller
@RequestMapping("payment-forecast")
public class PaymentForecastController {

    private final PaymentForecastBuilder paymentForecastBuilder;
    private final PaymentForecastProcessor paymentForecastProcessor;

    @Autowired
    public PaymentForecastController(PaymentForecastBuilder paymentForecastBuilder, PaymentForecastProcessor paymentForecastProcessor) {
        this.paymentForecastBuilder = paymentForecastBuilder;
        this.paymentForecastProcessor = paymentForecastProcessor;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public PaymentForecast getPaymentForecast() {
        log.info("Making payment forecast request");
        PaymentForecastData paymentForecastData = paymentForecastBuilder.getPaymentForecast();
        PaymentForecast paymentForecast = paymentForecastBuilder.generateForecastFromData(paymentForecastData);
        paymentForecast = paymentForecastProcessor.calcuclateOwedAmountsByMerchant(paymentForecast);
        paymentForecast.getDays().parallelStream().forEach(paymentForecastDay -> paymentForecastDay.setRecords(null));
        log.info("Payment forecast request success");
        return paymentForecast;
    }

}

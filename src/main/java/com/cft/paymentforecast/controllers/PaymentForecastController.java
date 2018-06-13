package com.cft.paymentforecast.controllers;

import com.cft.paymentforecast.services.PaymentForecastService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j2(topic = "General")
@Controller
@RequestMapping("payment-forecast")
public class PaymentForecastController {

    private final PaymentForecastService paymentForecastService;

    @Autowired
    public PaymentForecastController(PaymentForecastService paymentForecastService) {
        this.paymentForecastService = paymentForecastService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void getPaymentForecast() {
        paymentForecastService.getPaymentForecast();
    }

}

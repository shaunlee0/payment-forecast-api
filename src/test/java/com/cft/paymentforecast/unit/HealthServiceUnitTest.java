package com.cft.paymentforecast.unit;

import com.cft.paymentforecast.dto.APIResponse;
import com.cft.paymentforecast.services.HealthService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Currency;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HealthServiceUnitTest {

    @InjectMocks
    private HealthService healthService;

    private static final String SUCCESS_MESSAGE = "success";

    @Test
    public void shouldGetHealthResponse() {

        APIResponse healthResponse = healthService.getHealthResponse();

        MatcherAssert.assertThat(healthResponse,
                Matchers.hasProperty("message",is(SUCCESS_MESSAGE))
        );

        Currency currency =  Currency.getInstance(Locale.UK);
        Currency currencyGBP =  Currency.getInstance("GBP");

        System.out.println(currency.getSymbol());
    }
}

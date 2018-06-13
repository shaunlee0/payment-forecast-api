package com.cft.paymentforecast.unit;

import com.cft.paymentforecast.domain.data.PaymentForecastData;
import com.cft.paymentforecast.domain.data.PaymentForecastDataRecord;
import com.cft.paymentforecast.dto.APIResponse;
import com.cft.paymentforecast.services.DataExtractionService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DataExtractionServiceTest {

    private static final String FORECAST_DATA_CSV_PATH = "/csv/payment-forecast-data.csv";

    @InjectMocks
    private DataExtractionService dataExtractionService;

    private static final String SUCCESS_MESSAGE = "success";

    @Test
    public void shouldExtractPaymentForecastData() {

        PaymentForecastData paymentForecastData = dataExtractionService.extractData(FORECAST_DATA_CSV_PATH);

        MatcherAssert.assertThat(paymentForecastData,
                Matchers.hasProperty("records"));

        for (PaymentForecastDataRecord paymentForecastDataRecord : paymentForecastData.getRecords()) {
            MatcherAssert.assertThat(paymentForecastDataRecord,
                    Matchers.allOf(
                            Matchers.hasProperty("merchant"),
                            Matchers.hasProperty("payment"),
                            Matchers.hasProperty("payer"),
                            Matchers.hasProperty("sha256")
                    ));
        }

    }
}

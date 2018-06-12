package com.cft.paymentforecast.integration;

import com.cft.paymentforecast.PaymentForecastApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = PaymentForecastApplication.class)
@AutoConfigureMockMvc
public class HealthControllerIntegrationTest {

    @SpringBootApplication
    static class ExampleConfig {
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldBeAbleToMakeHealthRequest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(notNullValue())))
                .andExpect(jsonPath("$.message", is("success"))).andReturn();

    }
}

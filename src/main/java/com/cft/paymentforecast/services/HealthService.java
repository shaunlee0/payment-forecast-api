package com.cft.paymentforecast.services;

import com.cft.paymentforecast.dto.APIResponse;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private static final String SUCCESS_MESSAGE = "success";

    public APIResponse getHealthResponse() {
        return new APIResponse(SUCCESS_MESSAGE);
    }
}

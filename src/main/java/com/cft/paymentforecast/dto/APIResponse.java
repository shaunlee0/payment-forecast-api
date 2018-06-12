package com.cft.paymentforecast.dto;

import lombok.Data;

@Data
public class APIResponse {
    private String message;

    public APIResponse(String message) {
        this.message = message;
    }
}

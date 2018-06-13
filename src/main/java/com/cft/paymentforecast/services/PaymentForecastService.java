package com.cft.paymentforecast.services;

import com.cft.paymentforecast.domain.business.Merchant;
import com.cft.paymentforecast.domain.consumer.Payer;
import com.cft.paymentforecast.domain.data.PaymentForecastData;
import com.cft.paymentforecast.domain.data.PaymentForecastDataRecord;
import com.cft.paymentforecast.domain.payment.Payment;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2(topic = "ParsingErrors")
public class PaymentForecastService {

    private static final String FORECAST_DATA_CSV_PATH = "/csv/payment-forecast-data.csv";

    private final DataExtractionService dataExtractionService;

    @Autowired
    public PaymentForecastService(DataExtractionService dataExtractionService) {
        this.dataExtractionService = dataExtractionService;
    }

    public void getPaymentForecast() {
        PaymentForecastData paymentForecastData = dataExtractionService.extractData(FORECAST_DATA_CSV_PATH);
        validateSha256Hashes(paymentForecastData);
    }

    private void validateSha256Hashes(PaymentForecastData paymentForecastData) {
        int count = 1;
        for (PaymentForecastDataRecord paymentForecastDataRecord : paymentForecastData.getRecords()) {
            boolean validSha256Hash = validateSha256Hash(paymentForecastDataRecord, count);
            if (!validSha256Hash) {
                log.error("Invalid sha256 Hash found on row {} with record: {}", count, paymentForecastDataRecord);
            }
            count++;
        }
    }

    private boolean validateSha256Hash(PaymentForecastDataRecord paymentForecastDataRecord, int count) {
        boolean validSha256Hash = false;
        try {
            Payment payment = paymentForecastDataRecord.getPayment();
            Merchant merchant = paymentForecastDataRecord.getMerchant();
            Payer payer = paymentForecastDataRecord.getPayer();

            String hashStringForDataRecord = merchant.getPubKey() + payer.getPubKey()
                    + payer.getDebitPermissionId() + payment.getDueOnEpoc() + payment.getAmount();

            HashFunction hf = Hashing.sha256();
            HashCode hc = hf.newHasher()
                    .putString(hashStringForDataRecord, Charsets.UTF_8)
                    .hash();

            validSha256Hash = hc.toString().equals(paymentForecastDataRecord.getSha256());
        } catch (Exception e) {
            log.error("Invalid sha256 Hash found on row {} with record: {}", count, paymentForecastDataRecord);
        }

        return validSha256Hash;
    }
}

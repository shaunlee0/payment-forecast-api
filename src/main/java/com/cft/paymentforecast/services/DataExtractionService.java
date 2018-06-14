package com.cft.paymentforecast.services;

import com.cft.paymentforecast.domain.business.Merchant;
import com.cft.paymentforecast.domain.consumer.Payer;
import com.cft.paymentforecast.domain.data.PaymentForecastData;
import com.cft.paymentforecast.domain.data.PaymentForecastDataRecord;
import com.cft.paymentforecast.domain.payment.Payment;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

@Service
@Log4j2(topic = "ParsingErrors")
public class DataExtractionService {

    private static final String UTC_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public PaymentForecastData extractData(String pathToCsv) {

        InputStream is = getClass().getResourceAsStream(pathToCsv);
        Reader in;
        PaymentForecastData paymentForecastData = new PaymentForecastData();
        in = new InputStreamReader(is);
        Iterable<CSVRecord> records = null;
        try {
            records = CSVFormat.DEFAULT.withHeader().parse(in);
        } catch (IOException e) {
            log.error("Failed to load CSV file with path {}", pathToCsv);
        }
        int row = 2;
        assert records != null;

        for (CSVRecord record : records) {
            PaymentForecastDataRecord paymentForecastDataRecord = getPaymentRecordForCsvRecord(record, row);
            paymentForecastData.getRecords().add(paymentForecastDataRecord);
            row++;
        }

        return paymentForecastData;
    }

    private PaymentForecastDataRecord getPaymentRecordForCsvRecord(CSVRecord record, int row) {
        PaymentForecastDataRecord paymentForecastDataRecord = null;
        try {
            String receivedUTCString = record.get("ReceivedUTC");
            Integer merchantId = Integer.valueOf(record.get("MerchantId"));
            String merchantName = record.get("MerchantName");
            String merchantPubKey = record.get("MerchantPubKey");
            Integer payerId = Integer.valueOf(record.get("PayerId"));
            String payerPubKey = record.get("PayerPubKey");
            Integer debitPermissionId = Integer.valueOf(record.get("DebitPermissionId"));
            String dueUTCString = record.get("DueUTC");
            Long dueEpoc = Long.valueOf(record.get("DueEpoc"));
            Currency currency = Currency.getInstance(record.get("Currency"));
            Double amount = Double.valueOf(record.get("Amount"));
            String sha256 = record.get("SHA256(MerchantPubKeyPayerPubKeyDebitPermissionIdDueEpocAmount)");
            Date receivedOn = new SimpleDateFormat(UTC_FORMAT_STRING).parse(receivedUTCString);
            Date dueOn = new SimpleDateFormat(UTC_FORMAT_STRING).parse(dueUTCString);

            Merchant merchant = new Merchant(merchantName, merchantPubKey, merchantId);
            Payer payer = new Payer(payerId, payerPubKey, debitPermissionId);
            Payment payment = new Payment(receivedOn, dueOn, dueEpoc, currency, amount);

            paymentForecastDataRecord = new PaymentForecastDataRecord(merchant, payer, payment, sha256);
        } catch (Exception e) {
            log.error("Failed to extract values on row {} with CSV Record: {}", row, record.toString());
        }

        return paymentForecastDataRecord;
    }
}

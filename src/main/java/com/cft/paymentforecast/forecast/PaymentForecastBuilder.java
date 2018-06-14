package com.cft.paymentforecast.forecast;

import com.cft.paymentforecast.domain.business.Merchant;
import com.cft.paymentforecast.domain.consumer.Payer;
import com.cft.paymentforecast.domain.data.PaymentForecastData;
import com.cft.paymentforecast.domain.data.PaymentForecastDataRecord;
import com.cft.paymentforecast.domain.forecast.PaymentForecast;
import com.cft.paymentforecast.domain.forecast.PaymentForecastDay;
import com.cft.paymentforecast.domain.payment.Payment;
import com.cft.paymentforecast.services.DataExtractionService;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import static java.lang.String.valueOf;

@Component
@Log4j2(topic = "ParsingErrors")
public class PaymentForecastBuilder {

    private static final String FORECAST_DATA_CSV_PATH = "/csv/payment-forecast-data.csv";

    private final DataExtractionService dataExtractionService;

    @Autowired
    public PaymentForecastBuilder(DataExtractionService dataExtractionService) {
        this.dataExtractionService = dataExtractionService;
    }

    public PaymentForecastData getPaymentForecast() {
        PaymentForecastData paymentForecastData = dataExtractionService.extractData(FORECAST_DATA_CSV_PATH);
        validateSha256Hashes(paymentForecastData);
        return paymentForecastData;
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

    public PaymentForecast generateForecastFromData(PaymentForecastData paymentForecastData) {

        ArrayListMultimap<String, PaymentForecastDataRecord> datesByDay = ArrayListMultimap.create();

        for (PaymentForecastDataRecord paymentForecastDataRecord : paymentForecastData.getRecords()) {
            if (paymentForecastDataRecord != null) {
                Date dueOn = paymentForecastDataRecord.getPayment().getDueOn();
                String key = getLabelForDayFromDate(dueOn);
                datesByDay.put(key, paymentForecastDataRecord);
            }
        }


        return convertMultiMapToPaymentForecast(datesByDay);
    }

    private PaymentForecast convertMultiMapToPaymentForecast(ArrayListMultimap<String, PaymentForecastDataRecord> recordsByDay) {
        PaymentForecast paymentForecast = new PaymentForecast();
        for (String dateKey : recordsByDay.keySet()) {
            PaymentForecastDay paymentForecastDay = new PaymentForecastDay();
            try {
                Date date = new SimpleDateFormat("d/M/yyyy").parse(dateKey.split(" - ")[1]);
                paymentForecastDay.setDay(date);
                paymentForecastDay.setDayLabel(dateKey);
                paymentForecastDay.setRecords(recordsByDay.get(dateKey));
            } catch (ParseException e) {
                log.error("Failed to parse date {}", dateKey);
            }
            paymentForecast.getDays().add(paymentForecastDay);
        }

        Comparator<PaymentForecastDay> comparator = Comparator.comparing(PaymentForecastDay::getDay);
        paymentForecast.getDays().sort(comparator);

        return paymentForecast;
    }

    private String getLabelForDayFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        int dayOfTheWeekCode = cal.get(Calendar.DAY_OF_WEEK);

        if (hourOfDay > 16) {
            cal.add(Calendar.DATE, 1);
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            day = cal.get(Calendar.DAY_OF_MONTH);
            dayOfTheWeekCode = cal.get(Calendar.DAY_OF_WEEK);
        }


        String dayOfTheWeek = getDayForDayInWeekValue(dayOfTheWeekCode);
        return String.format("%s - %s/%s/%s", dayOfTheWeek, valueOf(day), valueOf(month), valueOf(year));
    }

    private String getDayForDayInWeekValue(int dayInWeek) {
        switch (dayInWeek) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;

        }

    }
}

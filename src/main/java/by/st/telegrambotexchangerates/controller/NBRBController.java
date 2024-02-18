package by.st.telegrambotexchangerates.controller;

import by.st.telegrambotexchangerates.model.response.RateResponse;
import by.st.telegrambotexchangerates.service.ExchangeRatesServiceNBRB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NBRBController {
    private final ExchangeRatesServiceNBRB exchangeRatesServiceNBRB;
/*    public List<String> getRateByCurName(String curName) {
        return exchangeRatesServiceNBRB.optionalCurrencyRateNBRBCurrentDay(curName)
                .map(rate -> Arrays.asList(
                        "Национальный банк РБ - " + rate.getCurAbbreviation() + " на " + rate.getFormattedDate(),
                        "Курс " + rate.getCurOfficialRate(),
                        "Выбранная валюта: " + rate.getCurName() + ", Выбранный банк: НБРБ"
                ))
                .orElseThrow(() -> new IllegalArgumentException("Валюта " + curName + " не найдена"));
    }*/

    public RateResponse getRateByCurName(String curAbbreviation, String bankName){
        return exchangeRatesServiceNBRB.optionalCurrencyRateNBRBCurrentDay(curAbbreviation)
                .map(rate -> {
                    RateResponse response = new RateResponse();
                    response.setBankName(bankName);
                    response.setCurAbbreviation(rate.getCurAbbreviation());
                    response.setRate(String.valueOf(rate.getCurOfficialRate()));
                    response.setDate(rate.getFormattedDate());
                    response.setCurrencyName(rate.getCurName());
                    return response;
                })
                .orElseThrow(()-> new IllegalArgumentException("Валюта " + curAbbreviation + " не найдена"));
    }
}

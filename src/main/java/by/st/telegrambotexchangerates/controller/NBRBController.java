package by.st.telegrambotexchangerates.controller;

import by.st.telegrambotexchangerates.service.ExchangeRatesServiceNBRB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NBRBController {
    private final ExchangeRatesServiceNBRB exchangeRatesServiceNBRB;
    public List<String> getRateByCurName(String curName) {
        return exchangeRatesServiceNBRB.optionalCurrencyRateNBRBCurrentDay(curName)
                .map(rate -> Arrays.asList(
                        "Национальный банк РБ - " + rate.getCurAbbreviation() + " на " + rate.getFormattedDate(),
                        "Курс " + rate.getCurOfficialRate(),
                        "Выбранная валюта: " + rate.getCurName() + ", Выбранный банк: НБРБ"
                ))
                .orElseThrow(() -> new IllegalArgumentException("Валюта " + curName + " не найдена"));
    }

}

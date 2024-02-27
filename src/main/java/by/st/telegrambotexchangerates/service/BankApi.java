package by.st.telegrambotexchangerates.service;

import by.st.telegrambotexchangerates.model.CurrencyRate;
import by.st.telegrambotexchangerates.model.CurrencyRateBelarusBank;

import java.util.Optional;

public interface BankApi {
    Optional<CurrencyRate> getCurrencyRate(String curName);

    CurrencyRateBelarusBank getAllRates();

}

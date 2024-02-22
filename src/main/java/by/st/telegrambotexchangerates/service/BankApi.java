package by.st.telegrambotexchangerates.service;

import by.st.telegrambotexchangerates.model.CurrencyRate;

import java.util.Optional;

public interface BankApi {
    Optional<CurrencyRate> getCurrencyRate(String curName);
}

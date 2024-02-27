package by.st.telegrambotexchangerates.controller;

import by.st.telegrambotexchangerates.constants.BotConstants;
import by.st.telegrambotexchangerates.model.CurrencyRateBelarusBank;
import by.st.telegrambotexchangerates.model.response.RateResponse;
import by.st.telegrambotexchangerates.service.AlfaBankApi;
import by.st.telegrambotexchangerates.service.BankApi;
import by.st.telegrambotexchangerates.service.BelarusBankApi;
import by.st.telegrambotexchangerates.service.NationalBankApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class NBRBController {
    public RateResponse getRateByCurName(String curAbbreviation, String bankName, BankApi bankApi) {
        if (bankApi instanceof AlfaBankApi || bankApi instanceof NationalBankApi) {
            return bankApi.getCurrencyRate(curAbbreviation)
                    .map(rate -> {
                        RateResponse response = new RateResponse();
                        response.setBankName(bankName);
                        response.setCurAbbreviation(rate.getCurAbbreviation());
                        response.setRateSale(rate.getCurOfficialRateSale());
                        response.setRateBuy(response.getRateBuy());
                        response.setDate(rate.getFormattedDate());
                        response.setCurrencyName(rate.getCurName());
                        return response;
                    })
                    .orElseThrow(() -> new IllegalArgumentException("Валюта " + curAbbreviation + " не найдена"));
        } else if (bankApi instanceof BelarusBankApi) {
            CurrencyRateBelarusBank currencyRateBelarusBank = bankApi.getAllRates();
            return switch (curAbbreviation) {
                case BotConstants.USD -> RateResponse.builder()
                        .bankName(bankName)
                        .date(currencyRateBelarusBank.getFormattedDate())
                        .curAbbreviation(curAbbreviation)
                        .rateSale(currencyRateBelarusBank.getUsdSale())
                        .rateBuy(currencyRateBelarusBank.getUsdBuy())
                        .build();
                case BotConstants.EUR -> RateResponse.builder()
                        .bankName(bankName)
                        .date(currencyRateBelarusBank.getFormattedDate())
                        .curAbbreviation(curAbbreviation)
                        .rateSale(currencyRateBelarusBank.getEurSale())
                        .rateBuy(currencyRateBelarusBank.getEurBuy())
                        .build();
                case BotConstants.CNY -> RateResponse.builder()
                        .bankName(bankName)
                        .date(currencyRateBelarusBank.getFormattedDate())
                        .curAbbreviation(curAbbreviation)
                        .rateSale(currencyRateBelarusBank.getCnySale())
                        .rateBuy(currencyRateBelarusBank.getChyBuy())
                        .build();
                case BotConstants.RUB -> RateResponse.builder()
                        .bankName(bankName)
                        .date(currencyRateBelarusBank.getFormattedDate())
                        .curAbbreviation(curAbbreviation)
                        .rateSale(currencyRateBelarusBank.getRubSale())
                        .rateBuy(currencyRateBelarusBank.getRubBuy())
                        .build();
                default -> throw new IllegalArgumentException("Валюта " + curAbbreviation + " не найдена");
            };
        } else {
            throw new IllegalArgumentException("Неизвестный API банка: " + bankApi.getClass().getSimpleName());
        }
    }
}




package by.st.telegrambotexchangerates.controller;

import by.st.telegrambotexchangerates.constants.BotConstants;
import by.st.telegrambotexchangerates.model.CurrencyRateBelarusBank;
import by.st.telegrambotexchangerates.model.OpenExchangeRate;
import by.st.telegrambotexchangerates.model.response.OpenExchangeResponse;
import by.st.telegrambotexchangerates.model.response.RateResponse;
import by.st.telegrambotexchangerates.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class RestController {
    public RateResponse getRateByCurName(String curAbbreviation, String bankName, BankApi bankApi) {
        if (bankApi instanceof AlfaBankApi || bankApi instanceof NationalBankApi) {
            return bankApi.getCurrencyRate(curAbbreviation)
                    .map(rate -> {
                        RateResponse response = new RateResponse();
                        response.setBankName(bankName);
                        response.setCurAbbreviation(rate.getCurAbbreviation());
                        response.setRateSale(rate.getCurOfficialRateSale());
                        response.setRateBuy(response.getRateBuy());
                        response.setQuantityUnits(rate.getQuantityUnits());
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
                        .currencyName(BotConstants.U_S_DOLLAR)
                        .quantityUnits(1)
                        .rateSale(currencyRateBelarusBank.getUsdSale())
                        .rateBuy(currencyRateBelarusBank.getUsdBuy())
                        .build();
                case BotConstants.EUR -> RateResponse.builder()
                        .bankName(bankName)
                        .date(currencyRateBelarusBank.getFormattedDate())
                        .curAbbreviation(curAbbreviation)
                        .currencyName(BotConstants.EURO)
                        .quantityUnits(1)
                        .rateSale(currencyRateBelarusBank.getEurSale())
                        .rateBuy(currencyRateBelarusBank.getEurBuy())
                        .build();
                case BotConstants.CNY -> RateResponse.builder()
                        .bankName(bankName)
                        .date(currencyRateBelarusBank.getFormattedDate())
                        .curAbbreviation(curAbbreviation)
                        .currencyName(BotConstants.CHINESE_YUAN)
                        .quantityUnits(10)
                        .rateSale(currencyRateBelarusBank.getCnySale())
                        .rateBuy(currencyRateBelarusBank.getChyBuy())
                        .build();
                case BotConstants.RUB -> RateResponse.builder()
                        .bankName(bankName)
                        .date(currencyRateBelarusBank.getFormattedDate())
                        .curAbbreviation(curAbbreviation)
                        .currencyName(BotConstants.RUSSIAN_RUBLES)
                        .quantityUnits(100)
                        .rateSale(currencyRateBelarusBank.getRubSale())
                        .rateBuy(currencyRateBelarusBank.getRubBuy())
                        .build();
                default -> throw new IllegalArgumentException("Валюта " + curAbbreviation + " не найдена");
            };
        } else {
            throw new IllegalArgumentException("Неизвестный API банка: " + bankApi.getClass().getSimpleName());
        }
    }
    public OpenExchangeResponse getRatesFromOpenExchBy(String date, String bankName, BankApi bankApi){
            OpenExchangeRate openExchangeRate = bankApi.getRatesByDate(date);
            return OpenExchangeResponse.builder()
                    .bankName(bankName)
                    .curAbbreviation(BotConstants.U_S_DOLLAR)
                    .date(date)
                    .rateSaleEUR(openExchangeRate.getRate().getCostEUR())
                    .rateSaleCNY(openExchangeRate.getRate().getCostCNY())
                    .rateSaleRUB(openExchangeRate.getRate().getCostRUB())
                    .rateSaleBYN(openExchangeRate.getRate().getCostBYN())
                    .build();
    }
}




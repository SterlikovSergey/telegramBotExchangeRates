package by.st.telegrambotexchangerates.controller;

import by.st.telegrambotexchangerates.model.response.RateResponse;
import by.st.telegrambotexchangerates.service.BankApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class NBRBController {

    public RateResponse getRateByCurName(String curAbbreviation, String bankName, BankApi bankApi){
        return bankApi.getCurrencyRate(curAbbreviation)
                .map(rate -> {
                    RateResponse response = new RateResponse();
                    response.setBankName(bankName);
                    response.setCurAbbreviation(rate.getCurAbbreviation());
                    response.setRate(rate.getCurOfficialRate());
                    response.setDate(rate.getFormattedDate());
                    response.setCurrencyName(rate.getCurName());
                    return response;
                })
                .orElseThrow(()-> new IllegalArgumentException("Валюта " + curAbbreviation + " не найдена"));
    }
}

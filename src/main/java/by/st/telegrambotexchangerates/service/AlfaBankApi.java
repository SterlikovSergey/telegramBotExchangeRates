package by.st.telegrambotexchangerates.service;

import by.st.telegrambotexchangerates.configuration.BankApiProperties;
import by.st.telegrambotexchangerates.model.CurrencyRate;
import by.st.telegrambotexchangerates.model.CurrencyRateAlfaBank;
import by.st.telegrambotexchangerates.model.CurrencyRateNBRB;
import by.st.telegrambotexchangerates.model.RateAlfaBank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Data
@Slf4j
public class AlfaBankApi implements BankApi{
    private final RestTemplate restTemplate;
    private final BankApiProperties bankApiProperties;
    @Override
    public Optional<CurrencyRate> getCurrencyRate(String curName) {
        ResponseEntity<CurrencyRateAlfaBank> response = restTemplate.exchange(
                bankApiProperties.getUrlAlfaBank(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CurrencyRateAlfaBank>() {
                }
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            CurrencyRateAlfaBank currencyRateAlfaBank = response.getBody();
            if (currencyRateAlfaBank != null) {
                for (RateAlfaBank rate : currencyRateAlfaBank.getRates()) {
                    if (curName.equals(rate.getCurAbbreviation())) {
                        return Optional.of(rate);
                    }
                }
            }
            return Optional.empty();
        } else {
            throw new RuntimeException("Failed to get exchange rates from the API");
        }
    }
}

package by.st.telegrambotexchangerates.service;

import by.st.telegrambotexchangerates.configuration.BankApiProperties;
import by.st.telegrambotexchangerates.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class OpenExchangeApi implements BankApi{

    private final RestTemplate restTemplate;
    private final BankApiProperties bankApiProperties;
    @Override
    public OpenExchangeRate getRatesByDate(String date) {
        String url = UriComponentsBuilder.fromUriString(bankApiProperties.getUrlOpenExchangeRates())
                .buildAndExpand("date",date)
                .toUriString();
        ResponseEntity<OpenExchangeRate> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<OpenExchangeRate>() {
                }
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get exchange rates from the API");
        }
    }
    @Override
    public Optional<CurrencyRate> getCurrencyRate(String curName) {
        return Optional.empty();
    }

    @Override
    public CurrencyRateBelarusBank getAllRates() {
        return null;
    }
}

package by.st.telegrambotexchangerates.service;

import by.st.telegrambotexchangerates.configuration.BankApiProperties;
import by.st.telegrambotexchangerates.model.CurrencyRate;
import by.st.telegrambotexchangerates.model.CurrencyRateBelarusBank;
import by.st.telegrambotexchangerates.model.CurrencyRateNBRB;
import lombok.RequiredArgsConstructor;
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
@Slf4j
public class BelarusBankApi implements BankApi {
    private final RestTemplate restTemplate;
    private final BankApiProperties bankApiProperties;

    @Override
    public Optional<CurrencyRate> getCurrencyRate(String curName) {
        ResponseEntity<List<CurrencyRateBelarusBank>> response = restTemplate.exchange(
                bankApiProperties.getUrlBelarusBank(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CurrencyRateBelarusBank>>() {
                }
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            /*return response.getBody();*/
            return null;
        } else {
            throw new RuntimeException("Failed to get exchange rates from the API");
        }
    }

    public CurrencyRateBelarusBank getAllRates() {
        ResponseEntity<List<CurrencyRateBelarusBank>> response = restTemplate.exchange(
                bankApiProperties.getUrlBelarusBank(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CurrencyRateBelarusBank>>() {
                }
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            return Objects.requireNonNull(response.getBody()).get(0);
        } else {
            throw new RuntimeException("Failed to get exchange rates from the API");
        }
    }
}



package by.st.telegrambotexchangerates.service;

import by.st.telegrambotexchangerates.configuration.BankApiProperties;
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
public class ExchangeRatesServiceNBRB {
    private final RestTemplate restTemplate;
    private final BankApiProperties bankApiProperties;

    public List<CurrencyRateNBRB> getExchangeRates() {
        ResponseEntity<List<CurrencyRateNBRB>> response = restTemplate.exchange(
                bankApiProperties.getUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CurrencyRateNBRB>>() {
                }
        );
        System.out.println(response);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to get exchange rates from the API");
        }
    }

    public Optional<CurrencyRateNBRB> optionalCurrencyRateNBRBCurrentDay(String curName) {
        ResponseEntity<List<CurrencyRateNBRB>> response = restTemplate.exchange(
                bankApiProperties.getUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CurrencyRateNBRB>>() {
                }
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            return Objects.requireNonNull(response
                            .getBody())
                    .stream()
                    .filter(currencyRateNBRB -> currencyRateNBRB
                            .getCurName()
                            .equals(curName))
                    .findFirst();
        } else {
            throw new RuntimeException("Failed to get exchange rates from the API");
        }
    }
}


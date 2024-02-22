package by.st.telegrambotexchangerates.service;

import by.st.telegrambotexchangerates.configuration.BankApiProperties;
import by.st.telegrambotexchangerates.model.CurrencyRate;
import by.st.telegrambotexchangerates.model.CurrencyRateNBRB;
import lombok.RequiredArgsConstructor;
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
public class NationalBankApi implements BankApi {
    private final RestTemplate restTemplate;
    private final BankApiProperties bankApiProperties;

    @Override
    public Optional<CurrencyRate> getCurrencyRate(String curName) {
        ResponseEntity<List<CurrencyRateNBRB>> response = restTemplate.exchange(
                bankApiProperties.getUrlNationalBank(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CurrencyRateNBRB>>() {
                }
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            return Objects.requireNonNull(response.getBody())
                    .stream()
                    .filter(currencyRateNBRB -> currencyRateNBRB.getCurAbbreviation().equals(curName))
                    .map(currencyRateNBRB -> (CurrencyRate) currencyRateNBRB)
                    .findFirst();
        } else {
            throw new RuntimeException("Failed to get exchange rates from the API");
        }
    }
}

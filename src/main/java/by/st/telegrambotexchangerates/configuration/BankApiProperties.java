package by.st.telegrambotexchangerates.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class BankApiProperties {
    @Value("${bank.api.url.nationalBank}")
    private String urlNationalBank;
    @Value("${bank.api.url.alfaBank}")
    private String urlAlfaBank;
    @Value("${bank.api.url.belarusBank}")
    private String urlBelarusBank;
    @Value("${bank.api.url.openExchangeRates}")
    private String urlOpenExchangeRates;

}

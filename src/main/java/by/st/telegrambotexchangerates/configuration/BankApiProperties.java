package by.st.telegrambotexchangerates.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class BankApiProperties {
    @Value("${bank.api.url}")
    private String url;
}

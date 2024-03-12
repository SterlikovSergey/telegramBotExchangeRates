package by.st.telegrambotexchangerates.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenExchangeRate {
    @JsonProperty("base")
    private String curAbbreviation;
    @JsonProperty("rates")
    private RateOpenExchange rate;

}

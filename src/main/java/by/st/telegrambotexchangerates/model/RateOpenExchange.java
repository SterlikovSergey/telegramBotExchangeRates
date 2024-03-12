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
public class RateOpenExchange {
    @JsonProperty("BYN")
    private Double costBYN;
    @JsonProperty("CNY")
    private Double costCNY;
    @JsonProperty("EUR")
    private Double costEUR;
    @JsonProperty("RUB")
    private Double costRUB;
}

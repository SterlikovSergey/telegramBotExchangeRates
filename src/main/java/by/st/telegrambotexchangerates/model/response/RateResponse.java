package by.st.telegrambotexchangerates.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateResponse {
    private String bankName;
    private String currencyName;
    private String curAbbreviation;
    private int quantityUnits;
    private Double rate;
    private String date;
}

package by.st.telegrambotexchangerates.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenExchangeResponse {
    private String bankName;
    private String date;
    private String currencyName;
    private String curAbbreviation;
    private Double rateSaleBYN;
    private Double rateSaleRUB;
    private Double rateSaleEUR;
    private Double rateSaleCNY;
}

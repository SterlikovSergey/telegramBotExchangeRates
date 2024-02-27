package by.st.telegrambotexchangerates.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CurrencyRateBelarusBank implements CurrencyRate{
    private final Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

    @JsonProperty("USD_in")
    private double usdBuy;

    @JsonProperty("USD_out")
    private double usdSale;

    @JsonProperty("RUB_in")
    private double rubBuy;

    @JsonProperty("RUB_out")
    private double rubSale;

    @JsonProperty("EUR_in")
    private double eurBuy;

    @JsonProperty("EUR_out")
    private double eurSale;

    @JsonProperty("CNY_in")
    private double chyBuy;

    @JsonProperty("CNY_out")
    private double cnySale;

    @Override
    public String getCurAbbreviation() {
        return null;
    }

    @Override
    public double getCurOfficialRateBuy() {
        return 0;
    }

    @Override
    public double getCurOfficialRateSale() {
        return 0;
    }

    @Override
    public int getQuantityUnits() {
        return 0;
    }

    @Override
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    @Override
    public String getCurName() {
        return null;
    }
}

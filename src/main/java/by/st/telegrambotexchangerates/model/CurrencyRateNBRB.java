package by.st.telegrambotexchangerates.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRateNBRB implements CurrencyRate{
    @JsonProperty("Cur_ID")
    private int curId;

    @JsonProperty("Date")
    private Date date;

    @JsonProperty("Cur_Abbreviation")
    private String curAbbreviation;

    @JsonProperty("Cur_Scale")
    private int quantityUnits;

    @JsonProperty("Cur_Name")
    private String curName;

    @JsonProperty("Cur_OfficialRate")
    private double curOfficialRate;

    @Override
    public String getCurAbbreviation() {
        return curAbbreviation;
    }

    @Override
    public double getCurOfficialRate() {
        return curOfficialRate;
    }

    @Override
    public int getQuantityUnits() {
        return quantityUnits;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    @Override
    public String getCurName() {
        return curName;
    }

}

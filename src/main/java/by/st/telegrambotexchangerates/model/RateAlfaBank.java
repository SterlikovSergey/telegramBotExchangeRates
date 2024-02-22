package by.st.telegrambotexchangerates.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateAlfaBank implements CurrencyRate{
    @JsonProperty("rate")
    private double rate;

    @JsonProperty("iso")
    private String iso;

    @JsonProperty("code")
    private int code;

    @JsonProperty("quantity")
    private int quantityUnits;

    @JsonProperty("date")
    private String date;

    @JsonProperty("name")
    private String name;

    @Override
    public String getCurAbbreviation() {
        return iso;
    }

    @Override
    public double getCurOfficialRate() {
        return rate;
    }

    @Override
    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = new SimpleDateFormat("dd.MM.yyyy").parse(this.date);
            return formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getCurName() {
        return name;
    }
}

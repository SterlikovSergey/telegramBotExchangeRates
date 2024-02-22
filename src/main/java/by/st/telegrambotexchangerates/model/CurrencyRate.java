package by.st.telegrambotexchangerates.model;

public interface CurrencyRate {
    String getCurAbbreviation();
    double getCurOfficialRate();
    int getQuantityUnits();
    String getFormattedDate();
    String getCurName();
}

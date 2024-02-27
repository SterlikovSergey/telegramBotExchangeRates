package by.st.telegrambotexchangerates.model;

public interface CurrencyRate {
    String getCurAbbreviation();
    double getCurOfficialRateSale();
    double getCurOfficialRateBuy();
    int getQuantityUnits();
    String getFormattedDate();
    String getCurName();
}

package by.st.telegrambotexchangerates.provider;

import by.st.telegrambotexchangerates.constants.BotConstants;
import by.st.telegrambotexchangerates.service.AlfaBankApi;
import by.st.telegrambotexchangerates.service.BankApi;
import by.st.telegrambotexchangerates.service.BelarusBankApi;
import by.st.telegrambotexchangerates.service.NationalBankApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankApiProvider {
    private final ApplicationContext context;

    public BankApi getBankApi(String bankName){
        if(bankName.equals(BotConstants.ALFA_BANK)){
            return context.getBean(AlfaBankApi.class);
        } else if (bankName.equals(BotConstants.NATIONAL_BANK)) {
            return context.getBean(NationalBankApi.class);
        } else if (bankName.equals(BotConstants.BELARUS_BANK)) {
            return context.getBean(BelarusBankApi.class);
        } else {
            throw new IllegalArgumentException("Неизвестный банк: " + bankName);
        }
    }
}

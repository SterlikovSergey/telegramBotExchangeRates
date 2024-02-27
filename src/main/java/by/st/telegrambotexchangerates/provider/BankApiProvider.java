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

    public BankApi getBankApi(String bankName) {
        return switch (bankName) {
            case BotConstants.ALFA_BANK -> context.getBean(AlfaBankApi.class);
            case BotConstants.NATIONAL_BANK -> context.getBean(NationalBankApi.class);
            case BotConstants.BELARUS_BANK -> context.getBean(BelarusBankApi.class);
            default -> throw new IllegalArgumentException("Неизвестный банк: " + bankName);
        };
    }
}

package by.st.telegrambotexchangerates.provider;

import by.st.telegrambotexchangerates.constants.BotConstants;
import by.st.telegrambotexchangerates.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.ws.rs.ext.Provider;

@Service
@RequiredArgsConstructor
public class BankApiProvider {
    private final ApplicationContext context;

    public BankApi getBankApi(String bankName) {
        return switch (bankName) {
            case BotConstants.ALFA_BANK -> context.getBean(AlfaBankApi.class);
            case BotConstants.NATIONAL_BANK -> context.getBean(NationalBankApi.class);
            case BotConstants.BELARUS_BANK -> context.getBean(BelarusBankApi.class);
            case BotConstants.OPEN_EXCHANGE_RATES -> context.getBean(OpenExchangeApi.class);
            default -> throw new IllegalArgumentException("Неизвестный банк: " + bankName);
        };
    }
}

package by.st.telegrambotexchangerates.service;

import by.st.telegrambotexchangerates.constants.BotConstants;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
public class KeyboardService {
    public ReplyKeyboardMarkup getBanksKeyboard() {
        List<String> banks = Arrays.asList(BotConstants.ALFA_BANK, BotConstants.BELARUS_BANK,
                BotConstants.NATIONAL_BANK);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        Iterator<String> bankIterator = banks.iterator();
        if (bankIterator.hasNext()) {
            KeyboardRow firstRow = new KeyboardRow();
            firstRow.add(bankIterator.next());
            keyboardRows.add(firstRow);
        }
        if (bankIterator.hasNext()) {
            KeyboardRow secondRow = new KeyboardRow();
            while (bankIterator.hasNext()) {
                secondRow.add(bankIterator.next());
            }
            keyboardRows.add(secondRow);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboardRows);
        return markup;
    }

    public ReplyKeyboardMarkup getCurrencyKeyboard() {
        KeyboardRow currencyRow = new KeyboardRow();
        currencyRow.add(BotConstants.USD);
        currencyRow.add(BotConstants.RUB);
        currencyRow.add(BotConstants.EUR);
        currencyRow.add(BotConstants.CNY);
        KeyboardRow anotherBankRow = new KeyboardRow();
        anotherBankRow.add(BotConstants.ANOTHER_BANK);
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(currencyRow);
        keyboard.add(anotherBankRow);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public ReplyKeyboardMarkup getOptionsKeyboard() {
        KeyboardRow currencyRow = new KeyboardRow();
        currencyRow.add(BotConstants.CURRENT_EXCHANGE_RATE);
        currencyRow.add(BotConstants.SELECTED_EXCHANGE_RATE);
        KeyboardRow statistics = new KeyboardRow();
        statistics.add(BotConstants.SELECTED_STATISTICS);
        KeyboardRow anotherRow = new KeyboardRow();
        anotherRow.add(BotConstants.ANOTHER_BANK);
        anotherRow.add(BotConstants.ANOTHER_CURRENCY);
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(currencyRow);
        keyboard.add(statistics);
        keyboard.add(anotherRow);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

}

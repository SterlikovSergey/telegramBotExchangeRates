package by.st.telegrambotexchangerates.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class KeyboardService {
    public ReplyKeyboardMarkup getBanksKeyboard(List<String> banks) {
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
        currencyRow.add("USD");
        currencyRow.add("RUB");
        currencyRow.add("EUR");
        currencyRow.add("CNY");
        KeyboardRow anotherBankRow = new KeyboardRow();
        anotherBankRow.add("Выбрать другой банк");
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(currencyRow);
        keyboard.add(anotherBankRow);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public ReplyKeyboardMarkup getOptionsKeyboard() {
        KeyboardRow currencyRow = new KeyboardRow();
        currencyRow.add("Курс на текущий день");
        currencyRow.add("Курс на выбранный день /doesn't work, under development");
        KeyboardRow statistics = new KeyboardRow();
        statistics.add("Собрать статистику/doesn't work, under development");
        KeyboardRow anotherRow = new KeyboardRow();
        anotherRow.add("Выбрать другой банк");
        anotherRow.add("Выбрать другую валюту");
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(currencyRow);
        keyboard.add(statistics);
        keyboard.add(anotherRow);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

}

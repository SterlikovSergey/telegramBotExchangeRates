package by.st.telegrambotexchangerates.component;

import by.st.telegrambotexchangerates.configuration.BotConfig;
import by.st.telegrambotexchangerates.controller.NBRBController;
import by.st.telegrambotexchangerates.model.Guest;
import by.st.telegrambotexchangerates.model.enums.StateChat;
import by.st.telegrambotexchangerates.model.response.RateResponse;
import by.st.telegrambotexchangerates.service.BankApiService;
import by.st.telegrambotexchangerates.service.KeyboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.st.telegrambotexchangerates.model.enums.StateChat.SELECT_BANK;
import static by.st.telegrambotexchangerates.model.enums.StateChat.SELECT_CURRENCY;


@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private static final String NATIONAL_BANK = "Нацбанк РБ";
    private static final String ALFA_BANK = "Альфа-Банк";
    private static final String BELARUS_BANK = "Беларусбанк";
    private static final String START = "/start";
    private static final String USD = "USD";
    private static final String RUB = "RUB";
    private static final String EUR = "EUR";
    private static final String CNY = "CNY";
    private static final String ANOTHER_BANK = "Выбрать другой банк";
    private static final String ANOTHER_CURRENCY = "Выбрать другую валюту";
    private static final String CURRENT_EXCHANGE_RATE = "Курс на текущий день";
    private static final String SELECTED_EXCHANGE_RATE = "Курс на выбранный день";
    private static final String SELECTED_STATISTICS = "Собрать статистику";




    private final KeyboardService keyboardService;

    private final MessageService messageService;

    private final BotConfig botConfig;

    private final BankApiService bankApiService;

    private final NBRBController nbrbController;

        private final Map<Long, StateChat> chatStates = new HashMap<>();
        private final Map<Long, String> lastSelectedBank = new HashMap<>();
        public final Map<Long,String> lastSelectedCurrency = new HashMap<>();

    private static Guest getGuest(Update update) {
        return Guest.builder()
                .id(update.getMessage().getChat().getId())
                .userName(update.getMessage().getChat().getUserName())
                .firstName(update.getMessage().getChat().getFirstName())
                .lastName(update.getMessage().getChat().getLastName())
                .build();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        long chatId;
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            Guest user = getGuest(update);
            StateChat stateChat = chatStates.getOrDefault(chatId, StateChat.SELECT_BANK);
            switch (stateChat) {
                case SELECT_BANK:
                    if (messageText.equals(START)) {
                        chatStates.put(chatId, StateChat.SELECT_BANK);
                        messageService.startCommandReceived(chatId,user,getBotUsername());
                        showBankOptions(chatId);
                        log.info("Пользователь: " + user + ", запустил бот!" );
                    } else if(messageText.equals(ALFA_BANK) || messageText.equals(BELARUS_BANK) || messageText.equals(NATIONAL_BANK)){
                        /*handleBankSelection(chatId, messageText);*/
                        chatStates.put(chatId, SELECT_CURRENCY);
                        lastSelectedBank.put(chatId,messageText);
                        showCurrencyOptions(chatId,messageText);
                        log.info("Пользователь: " + user + ", выбирает валюту!" );
                    } else {
                        messageService.sendHelpMessage(chatId);
                    }
                    break;

                case SELECT_CURRENCY:
                    switch (messageText) {
                        case USD, RUB, EUR, CNY -> {
                            /*handleCurrencySelection(chatId, messageText);*/
                            chatStates.put(chatId, StateChat.SELECT_OPTION);
                            lastSelectedCurrency.put(chatId,messageText);
                            showOptionOptions(chatId, lastSelectedBank.get(chatId), messageText);
                            log.info("Пользователь: " + user + ", выбирает опции валют!");
                            /*showOptionOptions(chatId);*/
                        }
                        case ANOTHER_BANK -> {
                            showBankOptions(chatId);
                            chatStates.put(chatId, SELECT_BANK);
                            log.info("Пользователь: " + user + ", меняет выбранный банк!");
                        }

                        default -> messageService.sendHelpMessage(chatId);
                    }
                    break;

                case SELECT_OPTION:
                    /*handleOptionSelection(chatId, messageText);*/
                    if(messageText.equals(CURRENT_EXCHANGE_RATE) || messageText.equals(SELECTED_EXCHANGE_RATE) ||
                messageText.equals(SELECTED_STATISTICS)){
                        messageService.sendMessage(chatId,messageText);
                        messageService.sendExchangeRateCurrentDayMessage(chatId,
                                nbrbController.getRateByCurName(lastSelectedCurrency.get(chatId),
                                        lastSelectedBank.get(chatId)));
                        log.info("Пользователь: " + user + ", получил ответ!");

                    } else if (messageText.equals(ANOTHER_BANK)) {
                        chatStates.put(chatId, SELECT_BANK);
                        showBankOptions(chatId);
                    } else {
                        chatStates.put(chatId,StateChat.SELECT_CURRENCY);
                        showCurrencyOptions(chatId,lastSelectedBank.get(chatId));
                    }
                    break;
            }
        }
    }


    private void showBankOptions(long chatId) {
        List<String> banks = bankApiService.getAllBanks();
        ReplyKeyboardMarkup markup = keyboardService.getBanksKeyboard(banks);
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Чтобы воспользоваться функциями бота сперва выбери банк из меню снизу.")
                .replyMarkup(markup)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Не удалось отправить сообщение: " + e.getMessage());
        }
    }

    private void showCurrencyOptions(long chatId, String bankName) {
        ReplyKeyboardMarkup markup = keyboardService.getCurrencyKeyboard();
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Ты выбрал " + bankName + ". Теперь выбери валюту из списка ниже.")
                .replyMarkup(markup)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Не удалось отправить сообщение: " + e.getMessage());
        }
    }

    private void showOptionOptions(long chatId, String bankName, String currency) {
        ReplyKeyboardMarkup markup = keyboardService.getOptionsKeyboard();
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выбранная валюта " + currency + ". Выбранный банк: " + bankName)
                .replyMarkup(markup)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Не удалось отправить сообщение: " + e.getMessage());
        }
    }

}

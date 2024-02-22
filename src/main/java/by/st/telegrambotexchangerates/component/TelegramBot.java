package by.st.telegrambotexchangerates.component;

import by.st.telegrambotexchangerates.configuration.BotConfig;
import by.st.telegrambotexchangerates.constants.BotConstants;
import by.st.telegrambotexchangerates.controller.NBRBController;
import by.st.telegrambotexchangerates.model.Guest;
import by.st.telegrambotexchangerates.model.enums.StateChat;
import by.st.telegrambotexchangerates.model.response.RateResponse;
import by.st.telegrambotexchangerates.provider.BankApiProvider;
import by.st.telegrambotexchangerates.service.BankApi;
import by.st.telegrambotexchangerates.service.KeyboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

import static by.st.telegrambotexchangerates.model.enums.StateChat.SELECT_BANK;
import static by.st.telegrambotexchangerates.model.enums.StateChat.SELECT_CURRENCY;


@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final KeyboardService keyboardService;
    private final MessageSender messageSender;
    private final BotConfig botConfig;
    private final NBRBController nbrbController;
    private final BankApiProvider bankApiProvider;
    private final Map<Long, StateChat> chatStates = new HashMap<>();
    private final Map<Long, String> lastSelectedBank = new HashMap<>();
    public final Map<Long, String> lastSelectedCurrency = new HashMap<>();

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
            Guest guest = getGuest(update);
            StateChat stateChat = chatStates.getOrDefault(chatId, StateChat.SELECT_BANK);
            switch (stateChat) {
                case SELECT_BANK:
                    if (messageText.equals(BotConstants.START)) {
                        chatStates.put(chatId, StateChat.SELECT_BANK);
                        messageSender.startCommandReceived(chatId, guest, getBotUsername());
                        showBankOptions(chatId);
                        log.info("Пользователь: " + guest + ", запустил бот!");
                    } else if (messageText.equals(BotConstants.ALFA_BANK) ||
                            messageText.equals(BotConstants.BELARUS_BANK) ||
                            messageText.equals(BotConstants.NATIONAL_BANK)) {
                        chatStates.put(chatId, SELECT_CURRENCY);
                        lastSelectedBank.put(chatId, messageText);
                        showCurrencyOptions(chatId, messageText);
                        log.info("Пользователь: " + guest + ", выбирает валюту!");
                    } else {
                        messageSender.sendHelpMessage(chatId);
                    }
                    break;
                case SELECT_CURRENCY:
                    switch (messageText) {
                        case BotConstants.USD, BotConstants.RUB, BotConstants.EUR, BotConstants.CNY -> {
                            chatStates.put(chatId, StateChat.SELECT_OPTION);
                            lastSelectedCurrency.put(chatId, messageText);
                            showOptionOptions(chatId, lastSelectedBank.get(chatId), messageText);
                            log.info("Пользователь: " + guest + ", выбирает опции валют!");
                        }
                        case BotConstants.ANOTHER_BANK -> {
                            showBankOptions(chatId);
                            chatStates.put(chatId, SELECT_BANK);
                            log.info("Пользователь: " + guest + ", меняет выбранный банк!");
                        }
                        default -> messageSender.sendHelpMessage(chatId);
                    }
                    break;
                case SELECT_OPTION:
                    if (messageText.equals(BotConstants.CURRENT_EXCHANGE_RATE)
                            || messageText.equals(BotConstants.SELECTED_EXCHANGE_RATE) ||
                            messageText.equals(BotConstants.SELECTED_STATISTICS)) {
                        messageSender.sendMessage(chatId, messageText);
                        BankApi bankApi = bankApiProvider.getBankApi(lastSelectedBank.get(chatId));
                        RateResponse response = nbrbController.getRateByCurName(
                                lastSelectedCurrency.get(chatId),
                                lastSelectedBank.get(chatId),
                                bankApi);
                        log.info("Получен ответ api банка в виде: " + response.toString());
                        messageSender.sendExchangeRateCurrentDayMessage(chatId, response);
                        messageSender.sendThanksToUserMessage(chatId, guest);
                        log.info("Пользователь: " + guest + ", получил ответ!");
                    } else if (messageText.equals(BotConstants.ANOTHER_BANK)) {
                        chatStates.put(chatId, SELECT_BANK);
                        showBankOptions(chatId);
                    } else {
                        chatStates.put(chatId, StateChat.SELECT_CURRENCY);
                        showCurrencyOptions(chatId, lastSelectedBank.get(chatId));
                    }
                    break;
            }
        }
    }

    private void showBankOptions(long chatId) {
        ReplyKeyboardMarkup markup = keyboardService.getBanksKeyboard();
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

package by.st.telegrambotexchangerates.component;

import by.st.telegrambotexchangerates.configuration.BotConfig;
import by.st.telegrambotexchangerates.constants.BotConstants;
import by.st.telegrambotexchangerates.controller.RestController;
import by.st.telegrambotexchangerates.model.Guest;
import by.st.telegrambotexchangerates.model.enums.StateChat;
import by.st.telegrambotexchangerates.model.enums.StateIncomingValues;
import by.st.telegrambotexchangerates.model.enums.StateResponse;
import by.st.telegrambotexchangerates.model.response.OpenExchangeResponse;
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

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static by.st.telegrambotexchangerates.model.enums.StateChat.SELECT_BANK;
import static by.st.telegrambotexchangerates.model.enums.StateChat.SELECT_CURRENCY;


@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    public final Map<Long, String> lastSelectedCurrency = new HashMap<>();
    private final KeyboardService keyboardService;
    private final MessageSender messageSender;
    private final BotConfig botConfig;
    private final RestController restController;
    private final BankApiProvider bankApiProvider;
    private final Map<Long, StateChat> chatStates = new HashMap<>();
    private final Map<Long, StateResponse> stateResponse = new HashMap<>();
    private final Map<Long, StateIncomingValues> stateIncomingValues = new HashMap<>();
    private final Map<Long, String> lastSelectedBank = new HashMap<>();
    private final Map<Long, String> dates = new HashMap<>();
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

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
                    switch (messageText){
                        case BotConstants.START -> {
                            chatStates.put(chatId, StateChat.SELECT_BANK);
                            messageSender.startCommandReceived(chatId, guest, getBotUsername());
                            showBankOptions(chatId);
                            log.info("Пользователь: " + guest + ", запустил бот!");
                        }
                        case BotConstants.ALFA_BANK,BotConstants.BELARUS_BANK,BotConstants.NATIONAL_BANK,
                                BotConstants.OPEN_EXCHANGE_RATES -> {
                            chatStates.put(chatId, SELECT_CURRENCY);
                            lastSelectedBank.put(chatId, messageText);
                            showCurrencyOptions(chatId, messageText);
                            log.info("Пользователь: " + guest + ", выбирает валюту!");
                        }
                        default -> messageSender.sendHelpMessage(chatId);
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
                    switch (messageText) {
                        case BotConstants.CURRENT_EXCHANGE_RATE -> {
                            stateResponse.put(chatId, StateResponse.WAITING_FOR_RESPONSE);
                            if (executorService == null || executorService.isShutdown()) {
                                executorService = Executors.newScheduledThreadPool(10);
                            }
                            long startTime = System.currentTimeMillis();
                            handleResponse(startTime, chatId);
                            messageSender.sendMessage(chatId, messageText);
                            BankApi bankApi = bankApiProvider.getBankApi(lastSelectedBank.get(chatId));
                            try {
                                RateResponse response = restController.getRateByCurName(
                                        lastSelectedCurrency.get(chatId),
                                        lastSelectedBank.get(chatId),
                                        bankApi);
                                if (response != null) {
                                    stateResponse.put(chatId, StateResponse.RESPONSE_RECEIVED);
                                    log.info("Получен ответ api банка в виде: " + response.toString());
                                    messageSender.sendExchangeRateCurrentDayMessage(chatId, response);
                                    messageSender.sendThanksToUserMessage(chatId, guest);
                                    log.info("Пользователь: " + guest + ", получил ответ!");
                                    stateResponse.put(chatId, StateResponse.RESET);
                                }
                            } catch (Exception e) {
                                stateResponse.put(chatId, StateResponse.WAITING_FOR_RESPONSE);
                                showBankOptions(chatId);
                                chatStates.put(chatId, SELECT_BANK);
                                executorService.shutdown();
                            }
                        }
                        case BotConstants.SELECTED_EXCHANGE_RATE -> {

                            if (executorService == null || executorService.isShutdown()) {
                                executorService = Executors.newScheduledThreadPool(10);
                            }
                            long startTime = System.currentTimeMillis();
                            messageSender.sendMessage(chatId, messageText);
                            if (stateIncomingValues.get(chatId) != StateIncomingValues.DATE_INPUT) {
                                stateIncomingValues.put(chatId, StateIncomingValues.WAITING_FOR_DATE);
                                messageSender.sendMessage(chatId, "Введите дату в формате гггг-мм-дд");
                            }
                            if (stateIncomingValues.get(chatId) == StateIncomingValues.WAITING_FOR_DATE) {
                                dates.put(chatId, update.getMessage().getText());
                                handleResponse(startTime, chatId);


                                try {
                                    OpenExchangeResponse response = restController.getRatesFromOpenExchBy(
                                            dates.get(chatId),
                                            lastSelectedBank.get(chatId),
                                            bankApiProvider.getBankApi(lastSelectedBank.get(chatId)));
                                    if (response != null) {
                                        stateResponse.put(chatId, StateResponse.RESPONSE_RECEIVED);
                                        log.info("Получен ответ api банка в виде: " + response.toString());
                                        messageSender.sendExchangeRateSelectedDayMessage(chatId, response);
                                        messageSender.sendThanksToUserMessage(chatId, guest);
                                        log.info("Пользователь: " + guest + ", получил ответ!");
                                        stateResponse.put(chatId, StateResponse.RESET);
                                    }
                                } catch (Exception e) {
                                    stateResponse.put(chatId, StateResponse.WAITING_FOR_RESPONSE);
                                    showBankOptions(chatId);
                                    chatStates.put(chatId, SELECT_BANK);
                                    executorService.shutdown();
                                }
                            }
                        }
                        case BotConstants.ANOTHER_BANK -> {
                            showBankOptions(chatId);
                            chatStates.put(chatId, SELECT_BANK);
                            log.info("Пользователь: " + guest + ", меняет выбранный банк!");
                        }
                        case BotConstants.ANOTHER_CURRENCY -> {
                            chatStates.put(chatId, StateChat.SELECT_CURRENCY);
                            showCurrencyOptions(chatId, lastSelectedBank.get(chatId));
                        }
                        default -> messageSender.sendHelpMessage(chatId);
                    }
                    break;
            }
        }
    }

    private void handleResponse(Long startTime, Long chatId) {
        executorService.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - startTime >= 31_000 &&
                    stateResponse.get(chatId) == StateResponse.WAITING_FOR_RESPONSE) {
                messageSender.sendMessage(chatId, "Сервис " + lastSelectedBank.get(chatId) +
                        " временно не доступен. Выберите другой банк!");
                executorService.shutdown();
                chatStates.put(chatId, SELECT_BANK);
            } else if (stateResponse.get(chatId) == StateResponse.RESPONSE_RECEIVED ||
                    stateResponse.get(chatId) == StateResponse.RESET) {
                executorService.shutdown();
            } else {
                messageSender.sendMessage(chatId, "Пожалуйста, подождите, ваш запрос обрабатывается...");
            }
        }, 10, 10, TimeUnit.SECONDS);
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

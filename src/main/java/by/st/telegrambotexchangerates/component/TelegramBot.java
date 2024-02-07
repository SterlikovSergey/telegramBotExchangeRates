package by.st.telegrambotexchangerates.component;

import by.st.telegrambotexchangerates.configuration.BotConfig;
import by.st.telegrambotexchangerates.service.BankApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private final BankApiService bankApiService;
    private RestTemplate restTemplate;

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
        String currency = "";

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                showBankOptions(chatId);
            } else {
                sendHelpMessage(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            chatId = update.getMessage().getChatId();
            if (callbackData.startsWith("bank:")) {
                String selectedBank = callbackData.substring(5);
                handleBankSelection(chatId, selectedBank);
            }
        }

    }

    private void showBankOptions(long chatId) {
        List<String> banks = bankApiService.getAllBanks();
        KeyboardRow row = new KeyboardRow();
        for (String bank : banks) {
            row.add(bank);
        }
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(Collections.singletonList(row));
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Выберите банк:")
                .replyMarkup(markup)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Не удалось отправить сообщение: " + e.getMessage());
        }
    }


    public String getCurrentRate(String currencyCode) {
        var answer = "";

        String jsonString = restTemplate.getForObject("https://api.nbrb.by/exrates/rates/USD?parammode=2", String.class);
        if ((jsonString == null) || (jsonString.isEmpty())) {
            return "На данных момент сервис не доступен";
        } else {
            try {
                JsonNode rootNode = new ObjectMapper().readTree(jsonString);
                JsonNode valuteNode = rootNode.get("Cur_OfficialRate");
                var value = valuteNode.get(currencyCode).get("Value").doubleValue();
                answer = "Официальный курс " + currencyCode + "  на сегодня: " + value + " USD";
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return answer;
        }
    }

    private void handleBankSelection(long chatId, String selectedBank) {
        String apiUrl = "https://api.nbrb.by/exrates/rates/" + selectedBank;
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        /*String rate = bankParser.parseRateFromResponse(response.getBody());*/
        /*sendMessage(chatId, "Текущий курс валюты в " + selectedBank + " составляет: " + rate);*/
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = "Привет  " + firstName + ", рады видеть вас.";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Не удалось отправить сообщение: " + e.getMessage());
        }
    }

    private void sendHelpMessage(long chatId) {
        String helpMessage = "Извините, я не понимаю эту команду. Вы можете использовать команду /start, чтобы начать.";
        sendMessage(chatId, helpMessage);
    }
}

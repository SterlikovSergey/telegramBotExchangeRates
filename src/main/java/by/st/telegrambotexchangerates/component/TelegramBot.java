package by.st.telegrambotexchangerates.component;

import by.st.telegrambotexchangerates.configuration.BotConfig;
import by.st.telegrambotexchangerates.controller.NBRBController;
import by.st.telegrambotexchangerates.service.BankApiService;
import by.st.telegrambotexchangerates.service.ExchangeRatesServiceNBRB;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    private final BankApiService bankApiService;

    private final RestTemplate restTemplate;

    private final ExchangeRatesServiceNBRB exchangeRatesServiceNBRB;

    private final NBRBController nbrbController;


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
            String userName = update.getMessage().getChat().getFirstName();
            if (messageText.equals("/start")) {
                startCommandReceived(chatId, userName);
                showBankOptions(chatId);
                log.info("Пользователь " + userName + ", выбирает банк");
            } else if (messageText.equals("Нацбанк РБ")){
                List<String> rates = nbrbController.getRateByCurName("Доллар США");
                sendMessage(chatId,getCurrentRate("usd"));
                sendMessage(chatId,exchangeRatesServiceNBRB.optionalCurrencyRateNBRBCurrentDay("Доллар США")
                        .toString());
                for(String rate: rates){
                    sendMessage(chatId,rate);
                }
            } else {
                sendHelpMessage(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            chatId = update.getMessage().getChatId();
            if (callbackData.equals("Нацбанк РБ")) {
                sendMessage(chatId,getCurrentRate("usd"));
                String selectedBank = callbackData.substring(5);
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
        System.out.println(jsonString);
        if ((jsonString == null) || (jsonString.isEmpty())) {
            return "На данных момент сервис не доступен";
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode rootNode = objectMapper.readTree(jsonString);
                JsonNode rateNode = rootNode.get("Cur_OfficialRate");
                String rateString = rateNode.asText();
                answer = "Официальный курс " + currencyCode + "  на сегодня: " + rateString + " USD";
                answer = jsonString;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return answer;
        }
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

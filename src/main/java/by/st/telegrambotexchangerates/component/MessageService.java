package by.st.telegrambotexchangerates.component;

import by.st.telegrambotexchangerates.model.Guest;
import by.st.telegrambotexchangerates.model.response.RateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageService {

    private TelegramLongPollingBot bot;
    @Autowired
    public void setBot(@Lazy TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void startCommandReceived(long chatId, Guest guest,String botName) {
        String answer = "Привет пользователь @" + guest.getUserName() + " Я, телеграм бот @" + botName +
                "." + " Добро пожаловать " + guest.getFirstName() + "!";
        sendMessage(chatId, answer);
    }

    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Не удалось отправить сообщение: " + e.getMessage());
        }
    }

    public void sendExchangeRateCurrentDayMessage(long chatId,RateResponse response){
        List<String> strings = new ArrayList<>();
        strings.add(response.getBankName() + " - " + response.getCurrencyName() + " на " + response.getDate());
        strings.add("Курс продажи - " + response.getRate());
        strings.add("Выбранная валюта: " + response.getCurAbbreviation() + ". Выбранный банк: " + response.getBankName());
        for (String string: strings){
            sendMessage(chatId,string);
        }

    }

    public void sendHelpMessage(long chatId) {
        String helpMessage = "Извините, я не понимаю эту команду. Вы можете использовать команду /start, чтобы начать.";
        sendMessage(chatId, helpMessage);
    }

}

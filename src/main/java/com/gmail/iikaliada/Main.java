package com.gmail.iikaliada;

import com.gmail.iikaliada.bot.CurrencyBot;
import com.gmail.iikaliada.bot.GameBot;
import com.gmail.iikaliada.bot.NewBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static void main(String[] args) {

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//            telegramBotsApi.registerBot(new CurrencyBot());
            telegramBotsApi.registerBot(new NewBot());
//            telegramBotsApi.registerBot(new GameBot());
            System.out.println("app is running");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

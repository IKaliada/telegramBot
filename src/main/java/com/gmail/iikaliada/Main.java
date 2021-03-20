package com.gmail.iikaliada;

import com.gmail.iikaliada.bot.CurrencyBot;
import com.gmail.iikaliada.bot.GameBot;
import com.gmail.iikaliada.bot.NewBot;
import com.gmail.iikaliada.bot.SecurityBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//            telegramBotsApi.registerBot(new CurrencyBot());
//            telegramBotsApi.registerBot(new NewBot());
//            telegramBotsApi.registerBot(new GameBot());
            telegramBotsApi.registerBot(new SecurityBot());
            logger.info("App is running");
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }
}

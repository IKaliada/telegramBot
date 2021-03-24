package com.gmail.iikaliada.bot;

import com.gmail.iikaliada.constant.CurrencyCommand;
import com.gmail.iikaliada.handler.CurrencyHandler;
import com.gmail.iikaliada.handler.KeyboardHandler;
import com.gmail.iikaliada.util.PropUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static com.gmail.iikaliada.constant.Constant.*;
import static com.gmail.iikaliada.constant.CurrencyCommand.HELP;
import static com.gmail.iikaliada.constant.CurrencyCommand.KEYBOARD;

public class CurrencyBot extends TelegramLongPollingBot {
    private final PropUtil propUtil = PropUtil.getInstance();
    private final Logger logger = LogManager.getLogger(CurrencyBot.class);

    @Override
    public String getBotUsername() {
        return propUtil.getProperties(BOT_NAME);
    }

    @Override
    public String getBotToken() {
        return propUtil.getProperties(BOT_TOKEN);
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        long chatId = update.getMessage().getChatId();
        sendMessage.setChatId(String.valueOf(chatId));
        String inputText = update.getMessage().getText();
        if (inputText.equals(START)) {
            String name = update.getMessage().getFrom().getFirstName() + " " +
                    update.getMessage().getFrom().getLastName();
            sendMessage.setText("Hello, " + name + ". Type " + HELP + " to get help");
        } else if (inputText.equals(KEYBOARD)) {
            KeyboardHandler keyboardHandler = new KeyboardHandler();
            try {
                sendMessage.setText("Choose your currency");
                sendMessage.setReplyMarkup(keyboardHandler.getKeyboard());
            } catch (IOException e) {
                logger.error(e.getMessage());
            } catch (ParserConfigurationException e) {
                logger.error(e.getMessage());
            } catch (SAXException e) {
                logger.error(e.getMessage());
            }
        } else if (inputText.equals(HELP)) {
            sendMessage.setText("Этот бот показывает курсы валют на текущий день. \n" +
                    "Список комманд бота:\n" +
                    CurrencyCommand.getCommand()
            );
        } else {
            CurrencyHandler currencyHandler = new CurrencyHandler();
            try {
                String currency = currencyHandler.getCurrency(inputText);
                sendMessage.setText(currency);
            } catch (IOException e) {
                logger.error(e.getMessage());
            } catch (SAXException e) {
                logger.error(e.getMessage());
            } catch (ParserConfigurationException e) {
                logger.error(e.getMessage());
            } catch (TransformerException e) {
                logger.error(e.getMessage());
            }
        }
        try {
            logger.info("Message '" + sendMessage.getText() + " was sent");
            logger.info("Called: " + inputText);
            Message execute = execute(sendMessage);
            logger.info("Message " + execute.getMessageId() + " delivered");
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }
    }
}

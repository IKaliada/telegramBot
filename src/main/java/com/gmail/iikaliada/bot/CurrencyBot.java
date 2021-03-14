package com.gmail.iikaliada.bot;

import com.gmail.iikaliada.PropUtil;
import com.gmail.iikaliada.constant.Constant;
import com.gmail.iikaliada.constant.CurrencyCommand;
import com.gmail.iikaliada.handler.CurrencyHandler;
import com.gmail.iikaliada.handler.KeyboardHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static com.gmail.iikaliada.constant.Constant.*;
import static com.gmail.iikaliada.constant.Constant.START;
import static com.gmail.iikaliada.constant.CurrencyCommand.*;

public class CurrencyBot extends TelegramLongPollingBot {
    private final PropUtil propUtil = PropUtil.getInstance();

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
            System.out.println(name);
            sendMessage.setText("Hello, " + name + ". Type "+ HELP +" to get help");
        } else if (inputText.equals(KEYBOARD)) {
            KeyboardHandler keyboardHandler = new KeyboardHandler();
            try {
                sendMessage.setText("Choose your currency");
                sendMessage.setReplyMarkup(keyboardHandler.getKeyboard());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
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
                System.out.println(currency);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

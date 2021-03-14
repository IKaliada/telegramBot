package com.gmail.iikaliada.bot;

import com.gmail.iikaliada.PropUtil;
import com.gmail.iikaliada.handler.CleanerHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.gmail.iikaliada.constant.Constant.*;

public class NewBot extends TelegramLongPollingBot {
    private PropUtil propUtil = PropUtil.getInstance();
    private Map<String, Message> messages = new HashMap<>();

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

        if (update.getMessage() != null) {

        }
        String chatId = propUtil.getProperties(CHAT_ID);
        if (chatId == null || "".equals(chatId)) {
            chatId = update.getMessage().getChatId().toString();
        }
        System.out.println("ChatID = " + chatId);

        messages.put(update.getMessage().getMessageId().toString(),
                update.getMessage());
    }

    private void sendMessage(String tmpId, Message getMessage) {
        SendMessage sendMessage = new SendMessage();
        String message = getMessage.getText();
        sendMessage.setChatId(tmpId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocument(String tmpId, Message getMessage) {
        SendDocument sendDocument = new SendDocument();
        InputFile inputFile = new InputFile(getMessage.getDocument().getFileId());
        sendDocument.setChatId(tmpId);
        String caption = getMessage.getCaption();
        if (!(caption == null || "".equals(caption))) {
            sendDocument.setCaption(getMessage.getCaption());
        }
        sendDocument.setDocument(inputFile);
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void clearMessages(Message input) {
        for (Map.Entry<String, Message> message : messages.entrySet()) {
            CleanerHandler cleanerHandler = new CleanerHandler(input);
            DeleteMessage clear = cleanerHandler.clear(message.getKey());
            try {
                execute(clear);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        messages.clear();
    }
}

package com.gmail.iikaliada.bot;

import com.gmail.iikaliada.PropUtil;
import com.gmail.iikaliada.handler.CleanerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.gmail.iikaliada.constant.Constant.*;

public class NewBot extends TelegramLongPollingBot {
    private final PropUtil propUtil = PropUtil.getInstance();
    private Map<String, Message> messages = new HashMap<>();
    private Logger logger = LogManager.getLogger(NewBot.class);

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
        String chatId = propUtil.getProperties(CHAT_ID);
        Message message = update.getMessage();
        if (message != null) {
            if (message.hasText()) {
                if (message.getText().equals(CLEAR)) {
                    clearMessages(message);
                } else {
                    sendMessage(chatId, message);
                }
            } else if (message.hasDocument()) {
                sendDocument(chatId, message);
            }
        }
    }

    private void sendMessage(String receiverId, Message inputMessage) {
        if (inputMessage.getReplyToMessage() != null && inputMessage.getReplyToMessage().hasText()) {
            try {
                replyToBot(inputMessage);
            } catch (TelegramApiException e) {
                logger.error(e.getMessage(), e);
            }
        } else if (Long.valueOf(inputMessage.getFrom().getId().toString()).equals(inputMessage.getChat().getId())) {
            ForwardMessage sendMessage = new ForwardMessage();
            sendMessage.setChatId(receiverId);
            sendMessage.setMessageId(inputMessage.getMessageId());
            sendMessage.setFromChatId(inputMessage.getFrom().getId().toString());
            try {
                Message execute = execute(sendMessage);
                messages.put(execute.getMessageId().toString(), execute);
                logger.info("Message '" + inputMessage.getText()
                        + "' was sent to " + execute.getChat().getTitle());
            } catch (TelegramApiException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            messages.put(inputMessage.getMessageId().toString(), inputMessage);
        }
    }

    private void sendDocument(String receiverId, Message getMessage) {
        SendDocument sendDocument = new SendDocument();
        InputFile inputFile = new InputFile(getMessage.getDocument().getFileId());
        sendDocument.setChatId(receiverId);
        String caption = getMessage.getCaption();
        if (!(caption == null || "".equals(caption))) {
            sendDocument.setCaption(getMessage.getCaption());
        }
        sendDocument.setDocument(inputFile);
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void clearMessages(Message input) {
        messages.put(input.getMessageId().toString(), input);
        logger.info(messages);
        Iterator<String> it = messages.keySet().iterator();
        while (it.hasNext()) {
            String message = it.next();
            logger.info("Trying to clear: MessageId=" + message);
            CleanerHandler cleanerHandler = new CleanerHandler(input);
            DeleteMessage clear = cleanerHandler.clear(message);
            try {
                Boolean execute = execute(clear);
                if (execute) {
                    it.remove();
                    logger.info("Message " + message + " was cleared");
                }
            } catch (TelegramApiException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void replyToBot(Message message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message.getText());
        sendMessage.setChatId(message.getFrom().getId().toString());
        Message execute = execute(sendMessage);
        logger.info("Message '" + execute.getText()
                + "' was sent to " + message.getFrom().getUserName());
        messages.put(message.getMessageId().toString(), message);
        messages.put(message.getReplyToMessage().getMessageId().toString(), message.getReplyToMessage());
    }
}

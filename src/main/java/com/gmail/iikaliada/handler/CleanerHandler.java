package com.gmail.iikaliada.handler;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class CleanerHandler {
    private Message input;

    public CleanerHandler(Message input) {
        this.input = input;
    }

    public DeleteMessage clear(String messageId) {
        System.out.println(input.getChat().getId());
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(input.getChatId().toString());
        deleteMessage.setMessageId(Integer.parseInt(messageId));
        return deleteMessage;
    }
}

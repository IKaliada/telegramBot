package com.gmail.iikaliada.bot;

import com.gmail.iikaliada.PropUtil;
import com.gmail.iikaliada.enums.EmojiConstants;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gmail.iikaliada.constant.Constant.*;
import static com.gmail.iikaliada.enums.EmojiConstants.*;

public class GameBot extends TelegramLongPollingBot {

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith(SLASH)) {
                if (update.getMessage().getText().equals(START)) {
                    initMessage(update);
                }
            } else {
                sendMessageToGroup(update);
            }
        } else if (update.hasCallbackQuery()) {
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String data = update.getCallbackQuery().getData();
            switch (data) {
                case GET_START:
                    getStart(update);
                    break;
                case PLAY_GAME:
                    playGame(update);
                    break;
                case GET_ABOUT_YOU:
                    getAboutYou(update);
                    break;
                case GET_SOME_INFO:
                    getSomeInfo(update);
                    break;
                case GET_MESSAGE:
                    sendMessage(update);
                    break;
                case GET_SENT_MESSAGE:
                    getSentMessage(update);
                    break;
                case PLAY_FOOTBALL:
                    sendGame(FOOTBALL_ANIM, chatId);
                    break;
                case PLAY_BASKET:
                    sendGame(BASKET_ANIM, chatId);
                    break;
                case PLAY_CASINO:
                    sendGame(CASINO_ANIM, chatId);
                    break;
                case PLAY_DARTS:
                    sendGame(DARTS_ANIM, chatId);
                    break;
                case PLAY_DICE:
                    sendGame(DICE_ANIM, chatId);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private void playGame(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        sendMessage.setText("Choose your game");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = Arrays.asList(
                addListButtons(
                        addButton(DICE_ANIM.label, PLAY_DICE),
                        addButton(DARTS_ANIM.label, PLAY_DARTS),
                        addButton(BASKET_ANIM.label, PLAY_BASKET)),
                addListButtons(
                        addButton(FOOTBALL_ANIM.label, PLAY_FOOTBALL),
                        addButton(CASINO_ANIM.label, PLAY_CASINO))
        );
        inlineKeyboardMarkup.setKeyboard(list);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToGroup(Update update) {
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setChatId(propUtil.getProperties(CHAT_ID));
        forwardMessage.setFromChatId(update.getMessage().getFrom().getId().toString());
        forwardMessage.setMessageId(update.getMessage().getMessageId());
        try {
            execute(forwardMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendGame(EmojiConstants game, String chatId) {
        SendDice sendDice = new SendDice();
        sendDice.setChatId(chatId);
        sendDice.setEmoji(game.label);
        try {
            execute(sendDice);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void initMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText("You entered start");
        InlineKeyboardMarkup rowsLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listButton = Arrays.asList(
                addListButtons(
                        addButton("Info", GET_START)),
                addListButtons(
                        addButton("Send message to group", SEND_MESSAGE)));
        rowsLine.setKeyboard(listButton);
        sendMessage.setReplyMarkup(rowsLine);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getStart(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("You want to continue work with us");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyButtons = Arrays.asList(
                addListButtons(addButton("About You", GET_ABOUT_YOU)),
                addListButtons(addButton("Some Info", GET_SOME_INFO)),
                addListButtons(addButton("PLAY GAME", PLAY_GAME))
        );
        inlineKeyboardMarkup.setKeyboard(keyButtons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getAboutYou(Update update) {
        SendMessage sendMessage = new SendMessage();
        String name = update.getCallbackQuery().getFrom().getFirstName();
        String lasstName = update.getCallbackQuery().getFrom().getLastName();
        sendMessage.setText("Name - " + name + " " + lasstName + "\n"
                + "userName - " + update.getCallbackQuery().getFrom().getUserName());
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        sendMessage.setReplyMarkup(getBackButton());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getSomeInfo(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        sendMessage.setText("Today is a good day to do something stupid");
        sendMessage.setReplyMarkup(getBackButton());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        sendMessage.setText("Enter text you want to send to chat");
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = Arrays.asList(addListButtons(
                addButton("SEND", GET_SENT_MESSAGE)
        ));
        inlineKeyboardMarkup.setKeyboard(buttons);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getMessages(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        sendMessage.setText("messages");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getSentMessage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(propUtil.getProperties(CHAT_ID));
        sendMessage.setText(update.getCallbackQuery().getMessage().getText());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        sendMessage.setText("Thank you!");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getBackButton() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> backButton = new ArrayList<>();

        backButton.add(addButton(BACK.label + " Back to main menu", GET_START));
        buttons.add(backButton);
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton addButton(String descButton, String callBackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(descButton);
        inlineKeyboardButton.setCallbackData(callBackData);
        return inlineKeyboardButton;
    }

    private List<InlineKeyboardButton> addListButtons(InlineKeyboardButton... button) {
        return new ArrayList<>(Arrays.asList(button));
    }

}

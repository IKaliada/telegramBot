package com.gmail.iikaliada.bot;

import com.gmail.iikaliada.InfoException;
import com.gmail.iikaliada.PropUtil;
import com.gmail.iikaliada.connection.DatabaseConnection;
import com.gmail.iikaliada.model.User;
import com.gmail.iikaliada.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.gmail.iikaliada.constant.Constant.*;

public class SecurityBot extends TelegramLongPollingBot {
    private final PropUtil propUtil = PropUtil.getInstance();
    private final Logger logger = LogManager.getLogger(NewBot.class);

    private final UserService userService;
    private final DatabaseConnection databaseConnection;

    public SecurityBot() {
        databaseConnection = new DatabaseConnection();
        userService = new UserService(databaseConnection);
    }

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
        Message input = update.getMessage();
        User user = null;
        if (input.getText().equals(START)) {
            user = getUserByUserId(input.getFrom().getId().toString());
            if (user == null) {
                try {
                    logger.info("User not found. Trying to save user");
                    user = addUser(input);
                    logger.info("User saved: " + user);
                } catch (InfoException e) {
                    logger.error(e.getMessage(), e);
                    sendErrorMessage(input.getFrom().getId().toString());
                }
            }
            if (user != null) {
                sendWelcomeMessage(user);
            }
        }
    }

    private void sendWelcomeMessage(User user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getUserId());
        String message = "Для того, чтобы подключиться к системе безопасности, придумайте для себя какое-то кодовое слово и сообщите его мне\n" +
                "Вам нужно будет каждый день в период с 20.00 до 22.00 пприсылать это кодовое слово в бот.\n" +
                "Как только произойдет проверка кодового слова, вам нужно будет очистить историю с ботом\n" +
                "Никаких напоминаний не будет, каждый день в один и тот же период жду от вас кодовое слово\n" +
                "Если у вас не получается в этот период сегодня ответить - напишите в бот '/Опаздываю' и вам предоставят дополнительно 2 часа для ответа\n" +
                "Если вы до 22.00 не пришлете кодовое слово - вас удалят из группы. Для того, чтобы в нее " +
                "вернуться - надо написать в бот '/Вернуться' и пройти проверку";
        sendMessage.setText("Добро пожаловать в проверку безопасности " + user.getName() + " " + user.getLastname() + "\n"
                + message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendErrorMessage(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Something went wrong. Try later");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private User addUser(Message inputMessage) throws InfoException {
        User user = new User();
        String name = inputMessage.getFrom().getFirstName();
        String lastName = inputMessage.getFrom().getLastName();
        user.setName(name != null ? name : "");
        user.setLastname(lastName != null ? lastName : "");
        user.setUsername(inputMessage.getFrom().getUserName());
        user.setUserId(inputMessage.getFrom().getId().toString());
        boolean isUserSaved = userService.addUser(user);
        if (isUserSaved) {
            return user;
        } else {
            throw new InfoException("Something went wrong");
        }
    }

    private User getUserByUserId(String userId) {
        return userService.getUserById(userId);
    }
}

package com.gmail.iikaliada.bot;

import com.gmail.iikaliada.UserSession;
import com.gmail.iikaliada.connection.DatabaseConnection;
import com.gmail.iikaliada.constant.BotState;
import com.gmail.iikaliada.constant.BotStateConst;
import com.gmail.iikaliada.exception.ErrorException;
import com.gmail.iikaliada.exception.InfoException;
import com.gmail.iikaliada.model.User;
import com.gmail.iikaliada.model.UserMessage;
import com.gmail.iikaliada.service.MessageService;
import com.gmail.iikaliada.service.UserService;
import com.gmail.iikaliada.util.PropUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.gmail.iikaliada.constant.BotStateConst.*;
import static com.gmail.iikaliada.constant.Constant.*;

public class SecurityBot extends TelegramLongPollingBot {
    private final PropUtil propUtil = PropUtil.getInstance();
    private final Logger logger = LogManager.getLogger(SecurityBot.class);
    private User user;
    private BotState botState;
    private int counter = 0;

    private final UserService userService;
    private final MessageService messageService;
    private final DatabaseConnection databaseConnection;
    private UserSession userSession;

    public SecurityBot() {
        databaseConnection = new DatabaseConnection();
        userService = new UserService(databaseConnection);
        messageService = new MessageService(databaseConnection);
        userSession = new UserSession();
        botState = new BotState(NEW);
        logger.info("Security bot initialized");
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
        logger.warn("User " + update.getMessage().getFrom().getId());
        Message input = update.getMessage();
        if (input.getText() != null) {
            try {
                user = userService.getUserById(input.getFrom().getId().toString());
                if (user != null) {
                    if (isUserKicked()) {
                        botState = new BotState(KICKED);
                        userSession.updateSession(user.getUserId(), botState);
                    }
                    if (userSession.getStateFromSession(user.getUserId()).equals(NEW)) {
                        botState = new BotState(RUNNING);
                    } else if (userSession.getStateFromSession(user.getUserId()).equals(WAITING)) {
                        botState = new BotState(WAKED_UP);
                    }
                }
                if (input.getText().equals(START)) {
                    if (user == null) {
                        addUser(input);
                    }
                    if (!(userSession.getStateFromSession(user.getUserId()).equals(SAVE_INIT)
                            || userSession.getStateFromSession(user.getUserId()).equals(KICKED))) {
                        if (isPeriodOfCheck()) {
                            botState = new BotState(RUNNING);
                        } else {
                            botState = new BotState(WAITING);
                        }
                    }
                } else if (input.getText().equals(GET_BACK)) {
                    botState = new BotState(BACK);
                }
                if (user != null) {
                    userSession.updateSession(user.getUserId(), botState);
                    handleMessage(input, user);
                }
            } catch (ErrorException e) {
                sendMessageToAdmin(input);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void addUser(Message input) {
        try {
            logger.info("User not found on db. Trying to save user");
            int id = userService.addUser(input);
            if (id > 0) {
                user = userService.getUserById(input.getFrom().getId().toString());
                if (user == null) {
                    throw new ErrorException("User " + input.getFrom().getId() + " not found");
                }
            }
            sendWelcomeMessage(user);
            botState = new BotState(SAVE_INIT);
            userSession.updateSession(user.getUserId(), botState);
            logger.info("User saved: " + user);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            sendMessage(input.getFrom().getId().toString(), "Something went wrong");
            throw new ErrorException(e.getMessage(), e);
        }
    }

    private boolean isUserKicked() {
        int kicked = userService.getKicked(user.getUserId());
        return kicked == 1;
    }

    private boolean isPeriodOfCheck() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTme = simpleDateFormat.format(new Date());
        String startOfTime = propUtil.getProperties("start_time");
        String endOfTime = propUtil.getProperties("end_time");
        long sTime = Time.valueOf(startOfTime).getTime();
        long eTime = Time.valueOf(endOfTime).getTime();
        long cTime = Time.valueOf(currentTme).getTime();
        return cTime >= sTime && cTime <= eTime;
    }

    private void handleMessage(Message input, User user) {
        logger.info("Session " + user.getUserId() + " "
                + userSession.getStateFromSession(user.getUserId()));
        if (counter > 2) {
//                kickUser(update.getMessage().getFrom().getId(), "-1001366669804");
            throw new ErrorException("Превышение попыток ввода пароля");
        }
        BotStateConst state = userSession.getStateFromSession(user.getUserId());
        switch (state) {
            case NEW:
                sendMessage(input.getFrom().getId().toString(),
                        "Введите /start для начала работы с ботом");
                break;
            case WAITING:
                sendMessage(user.getUserId(),
                        "Время проверки не пришло");//Добавить обработку, когда время проверки не пришло
                break;
            case WAKED_UP:
                sendMessage(user.getUserId(),
                        "Введите /start для начала работы с ботом");
                break;
            case RUNNING:
                sendMessage(user.getUserId(), "Введите пароль для проверки:");
                botState = new BotState(ENTERED_MESSAGE);
                break;
            case SAVE_INIT:
                sendMessage(user.getUserId(), "Введите кодовое слово:");
                botState = new BotState(INIT_MESSAGE);
                break;
            case KICKED:
                sendMessage(user.getUserId(), "Введите /back что бы вернуться " +
                        "и пройти проверку");
                break;
            case INIT_MESSAGE:
                saveInitMessage(input.getText(), new Date().getTime());
                botState = new BotState(WAITING);
                break;
            case ENTERED_MESSAGE:
                UserMessage messageByUserId = messageService.getMesssgeByUserId(user.getId());
                handlingEnteredMessage(messageByUserId.getMessage(), input.getText());
                break;
            case BACK:
                if (isUserKicked()) {
                    sendMessage(propUtil.getProperties(ADMIN), "User " + user.getUserId()
                            + " wants to get back");
                    sendMessage(user.getUserId(), "Мы отправили сообщение администратору," +
                            " скоро с вами свяжутся");
                }
                break;
            default:
                logger.warn("Что то пошло не так: \n" + input.getFrom().getId() + " "
                        + input.getDate() + " " + input.getText());
                sendMessage(propUtil.getProperties(ADMIN),
                        "Что то пошло не так: \n" + input.getFrom().getId() + " "
                                + input.getDate() + " " + input.getText());
        }
        userSession.updateSession(user.getUserId(), botState);
    }

    private void saveInitMessage(String message, long date) {
        boolean isSaved = false;
        try {
            isSaved = messageService.saveMessage(user.getId(), message);
            if (isSaved) {
                sendMessage(user.getUserId(), "Мы запомнили ваше кодовое слово. " +
                        "Не забудьте почистить историю");
            } else {
                sendMessage(user.getUserId(), "Что то пошло не так. " +
                        "Мы уже работаем над проблемой");
                sendMessage(propUtil.getProperties(ADMIN),
                        message + "\n" +
                                user.getUserId() + "\n" +
                                new SimpleDateFormat("dd:MM:yyyy HH:mm:ss")
                                        .format(TimeUnit.SECONDS.toMillis(date)));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendMessageToAdmin(Message input) {
        sendMessage(propUtil.getProperties(ADMIN),
                input.getFrom().getUserName() + "\n" +
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                .format(TimeUnit.SECONDS.toMillis(input.getDate())));
    }

    private void handlingEnteredMessage(String savedMessage, String enteredMessage) {
        if (enteredMessage.equals(savedMessage)) {
            sendMessage(user.getUserId(), "Спасибо, " +
                    getFullName(user) + ", это действительно вы!");
            botState = new BotState(WAITING);
//            dropTimer(user.getUserId());
            counter = 0;
        } else {
            try {
                String message = "";
                switch (counter) {
                    case 0:
                        message = "2 попытки";
                        break;
                    case 1:
                        message = "1 попытка";
                        break;
                    default:
                        throw new InfoException("Превышение попыток ввода пароля");
                }
                sendMessage(user.getUserId(), "Пароли не совпадают. У вас еще " +
                        message);
                counter++;
                logger.info("counter = " + counter);
                botState = new BotState(ENTERED_MESSAGE);
            } catch (InfoException e) {
                kickUser(user.getUserId(), propUtil.getProperties(CHAT));
                sendMessage(user.getUserId(),
                        "Вы не правильно указали кодовое слово. Вас исключили из группы." +
                                "Для того, что бы вернуться в группу, введите " +
                                "/back и пройдите проверку");
                logger.error("User was kicked! " + e.getMessage());
            }
        }
    }

    private void kickUser(String userId, String chatId) {
        try {
            KickChatMember kickChatMember = new KickChatMember();
            kickChatMember.setUserId(Integer.parseInt(userId));
            kickChatMember.setChatId(chatId);
            boolean isKicked = false;
            int kickCounter = 0;
            userService.kickUser(userId);
            userSession.updateSession(userId, new BotState(KICKED));
            while (!isKicked) {
                try {
                    isKicked = execute(kickChatMember);
                    if (isKicked) {
                        logger.info("User " + userId + " was kicked");
                    }
                } catch (TelegramApiException e) {
                    if (e.getCause().getMessage().contains("Connection timed out")) {
                        if (kickCounter > 10) {
                            throw new ErrorException(e.getCause().getMessage());
                        }
                        isKicked = false;
                        logger.error(e.getMessage());
                        kickCounter++;
                    }
                }
            }
        } catch (ErrorException e) {
            logger.error(e.getMessage());
            sendMessage(propUtil.getProperties(ADMIN), user.getUserId() +
                    "\n" + e.getCause().getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    private String getFullName(User user) {
        String name = user.getName() != null ? user.getName() : "";
        String lastName = user.getLastname() != null ? user.getLastname() : "";
        return name + " " + lastName;
    }

    private void sendMessage(String userId, String message) {
        boolean isSaved = false;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(message);
        while (!isSaved) {
            try {
                Message savedMessage = execute(sendMessage);
                if (savedMessage != null) {
                    isSaved = true;
                }
            } catch (TelegramApiException e) {
                if (e.getCause().getMessage().contains("Connection timed out")) {
                    continue;
                } else {
                    sendMessage(userId, "Произошла ошибка!");
                    sendMessage(propUtil.getProperties(ADMIN), e.getCause().getMessage());
                    isSaved = true;
                }
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void sendWelcomeMessage(User user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getUserId());
        String message = "Для того, чтобы подключиться к системе безопасности, придумайте для себя какое-то кодовое " +
                "слово и сообщите его в бот. Вам нужно будет каждый день в период с 20.00 до 22.00 присылать это " +
                "кодовое слово в бот. Как только произойдет проверка кодового слова, вам нужно будет очистить историю с ботом\n" +
                "Никаких напоминаний не будет, каждый день в один и тот же период жду от вас кодовое слово\n" +
                "Если у вас не получается в этот период сегодня ответить - напишите в бот '/im_late' и вам предоставят " +
                "дополнительно 2 часа для ответа\n" +
                "Если вы до 22.00 не пришлете кодовое слово - вас удалят из группы. Для того, чтобы в нее " +
                "вернуться - надо написать в бот '/back' и пройти проверку";
        sendMessage.setText("Добро пожаловать в проверку безопасности " + user.getName() + " " + user.getLastname() + "\n"
                + message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
}

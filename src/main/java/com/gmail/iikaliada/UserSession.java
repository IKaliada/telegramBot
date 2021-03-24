package com.gmail.iikaliada;

import com.gmail.iikaliada.constant.BotState;
import com.gmail.iikaliada.constant.BotStateConst;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
    private Map<String, BotState> session = new HashMap<>();
    private Logger logger = LogManager.getLogger(UserSession.class);

    public void updateSession(String userId, BotState botState) {
        if (session.get(userId) == null) {
            session.put(userId, botState);
        } else {
            session.replace(userId, botState);
        }
    }

    public BotStateConst getStateFromSession(String userId) {
        if (session.get(userId) == null) {
            session.put(userId, new BotState(BotStateConst.NEW));
        }
        BotState botState = session.get(userId);
        return botState.getBotStates();
    }
}
package com.cooldrinkscompany.vmcs.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.cooldrinkscompany.vmcs.service.CoinsService.InsertCoin;
import com.google.gson.Gson;

public final class SessionManager {
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());

    private static SessionManager INSTANCE;
    List<Session> sessions;

    private SessionManager() {    
        sessions = new ArrayList<Session>();    
    }
    
    public static SessionManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        
        return INSTANCE;
    }

    public Session createSession(InsertCoin coin) {
        Session session = new Session(coin);
        sessions.add(session);
        LOGGER.info(new Gson().toJson(sessions));
        return session;
    }
}

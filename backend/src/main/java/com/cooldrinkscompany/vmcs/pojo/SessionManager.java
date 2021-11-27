package com.cooldrinkscompany.vmcs.pojo;

import com.cooldrinkscompany.vmcs.service.CoinsService.InsertCoin;

public final class SessionManager {
    private static SessionManager INSTANCE;
    
    private SessionManager() {        
    }
    
    public static SessionManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        
        return INSTANCE;
    }

    public Session createSession(InsertCoin coin) {
        Session session = new Session(coin);
        session.save();
        return session;
    }
}

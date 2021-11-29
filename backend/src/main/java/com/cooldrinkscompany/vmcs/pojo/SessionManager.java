package com.cooldrinkscompany.vmcs.pojo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.cooldrinkscompany.endpoint.MessageBoardEndpoint;
import com.cooldrinkscompany.vmcs.factory.VendingMachineSnapshotFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public final class SessionManager {
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());

    private static SessionManager INSTANCE;
    List<Session> sessions;

    private SessionManager() {
        sessions = new ArrayList<Session>();
    }

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
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

    public Session updateSession(String sessionId, InsertCoin coin) {
        Session session = null;
        // LOGGER.info("[updateSession] Session ID is " + sessionId);
        for (Session s : sessions) {
            // LOGGER.info("[updateSession] Existing session ID is " +
            // s.sessionId.toString());
            // LOGGER.info("[updateSession] Compare result: " +
            // (s.sessionId.toString().equals(sessionId)));
            if (s.sessionId.toString().equals(sessionId)) {
                if (coin != null) {
                    s.addCoin(coin);
                }
                session = s;
                Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                VendingMachineSnapshot snapshot = VendingMachineSnapshotFactory.getInstance().getSnapshot();
                JsonElement jsonElement = gson.toJsonTree(snapshot);
                jsonElement.getAsJsonObject().add("session", gson.toJsonTree(s));
                MessageBoardEndpoint.getInstance().sendMessage(gson.toJson(jsonElement), session.sessionId.toString());
                break;
            }
        }
        LOGGER.info(new Gson().toJson(sessions));
        return session;
    }

    public Session getSession(String sessionId) {
        Session session = null;
        for (Session s : sessions) {
            if (s.sessionId.toString().equals(sessionId)) {
                session = s;
                break;
            }
        }
        return session;
    }

    public Session purchaseSession(Drink drink)
    {
        Session session = new Session(drink);
        sessions.add(session);
        LOGGER.info(new Gson().toJson(sessions));
        return session;
    }
}

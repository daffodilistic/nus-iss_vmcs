package com.cooldrinkscompany.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class MessageBoardEndpoint extends Endpoint {
    private static final Logger LOGGER = Logger.getLogger(MessageBoardEndpoint.class.getName());

    private static MessageBoardEndpoint INSTANCE;
    private final Map<String, List<Session>> sessions = new ConcurrentHashMap<>();

    public static MessageBoardEndpoint getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageBoardEndpoint();
        }

        return INSTANCE;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        String sessionId = session.getPathParameters().get("sessionId");
        if (sessionId != null) {
            LOGGER.info(String.format("Session opened ID: %s key: %s", session.getId(), sessionId));
            if (!sessions.containsKey(sessionId)) {
                sessions.put(sessionId, new ArrayList<>());
            }
            sessions.get(sessionId).add(session);

            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    LOGGER.info(String.format("Message received from session ID: %s, message: %s, key: %s",
                            session.getId(),
                            message,
                            sessionId));
                    sessions.get(sessionId).parallelStream().forEach(session2 -> {
                        if (session == session2) {
                            return;
                        }
                        session2.getAsyncRemote().sendText(message);
                    });
                }
            });
        } else {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Session ID is required"));

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return;
    }

    public void sendMessage(String message, String key) {
        LOGGER.info("Sending message...");
        if (!sessions.containsKey(key)) {
            LOGGER.info(String.format("Key '%s' not registered, can't send message '%s'", key, message));
            return;
        }

        sessions.get(key).parallelStream().forEach(session -> {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        });
    }
        });
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        super.onError(session, thr);
    }
}
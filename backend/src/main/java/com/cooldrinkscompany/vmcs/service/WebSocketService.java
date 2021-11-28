package com.cooldrinkscompany.vmcs.service;

import java.util.logging.Logger;

import javax.websocket.server.ServerEndpointConfig;

import com.cooldrinkscompany.endpoint.MessageBoardEndpoint;

import io.helidon.webserver.*;
import io.helidon.webserver.Routing.Rules;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.tyrus.TyrusSupport;

public class WebSocketService implements Service {
    private static final Logger LOGGER = Logger.getLogger(CoinsService.class.getName());

    @Override
    public void update(Rules rules) {
        ServerEndpointConfig serverConfig = ServerEndpointConfig.Builder
                .create(MessageBoardEndpoint.class, "/{sessionId}")
                .configurator(new CustomServerEndpointConfigurator())
                .build();

        rules
                .register("/",
                        TyrusSupport.builder().register(serverConfig)
                                .build());
    }

    public class CustomServerEndpointConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public <T> T getEndpointInstance(Class<T> endpointClass) {
            // override the default behavior by providing a 'Singleton'
            return (T) MessageBoardEndpoint.getInstance();
        }
    }
}

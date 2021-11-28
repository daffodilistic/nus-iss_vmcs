package com.cooldrinkscompany.vmcs.service;

import java.util.Collections;
//import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import com.cooldrinkscompany.vmcs.controller.ControllerManageDrink;
import com.cooldrinkscompany.vmcs.controller.ControllerSetSystemStatus;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import io.helidon.common.reactive.Multi;
import io.helidon.config.Config;
import io.helidon.webserver.*;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

public class SystemService implements Service {
    private static final Logger LOGGER = Logger.getLogger(SystemService.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());

    private final ProductDAOImpl productDao;
    private final Config config;

    public SystemService(ProductDAOImpl productDao) {
        this.productDao = productDao;
        this.config = Config.create();
    }

    @Override
    public void update(Routing.Rules rules) {

        rules
        .get(PathMatcher.create("/login/*"), this::login)
        .get(PathMatcher.create("/logout"), this::logout)
        .get(PathMatcher.create("/viewDoorStatus"), this::viewDoorStatus)
        .get(PathMatcher.create("/lockDoor"), this::lockDoor);
    }

    private boolean validatePassword(String inputPassword){
        String realPassword = this.config.get("password").asMap().get().get("password");
        if (inputPassword.equals(realPassword)){
            return true;
        }
        return false;
    }

    private void login(ServerRequest request, ServerResponse response){
        String inputPassword = request.path().toString().replace("/login/", "");
        if(validatePassword(inputPassword)){
            String logginResponse = ControllerSetSystemStatus.setLoggedIn(this.productDao);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:",logginResponse).build();
            response.send(returnObject);
        }else{
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:", "Failed to loggin with password: " + inputPassword).build();
            response.send(returnObject);
        }
    }

    private void logout(ServerRequest request, ServerResponse response){
        if (ControllerSetSystemStatus.getStatus(this.productDao, "isUnlocked")){
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:", "Log out Failed. Please lock the door first.").build();
            response.send(returnObject);
        }else{
            String logoutResponse= ControllerSetSystemStatus.setStatus(this.productDao, "isLoggedIn", false);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:", logoutResponse.equals("Success") ? "Logged out" : "Logged out Failed").build();
            response.send(returnObject);
        }
    }

    private void viewDoorStatus(ServerRequest request, ServerResponse response){
        Boolean doorStatus = ControllerSetSystemStatus.getStatus(this.productDao, "isUnlocked");
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:", doorStatus ? "Unlocked" : "Locked").build();
        response.send(returnObject);
    }

    private void lockDoor(ServerRequest request, ServerResponse response){
        String lockDoorResponse = ControllerSetSystemStatus.setStatus(this.productDao, "isUnlocked", false);
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:", lockDoorResponse.equals("Success") ? "Door locked" : "Lock Failed").build();
        response.send(returnObject);
    }


}

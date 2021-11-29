package com.cooldrinkscompany.vmcs.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.google.gson.Gson;

import io.helidon.common.reactive.Multi;
import io.helidon.webserver.*;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

public class DrinksService implements Service {
    private static final Logger LOGGER = Logger.getLogger(DrinksService.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());

    private final ProductDAOImpl productDao;

    public DrinksService(ProductDAOImpl productDao) {
        this.productDao = productDao;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/", this::listDrinks)
                .post("/{drinkId}/buy", this::buyDrink)
                .get(PathMatcher.create("/viewDrinkQty/*"), this::viewDrinkQty)
                .put(PathMatcher.create("/setDrinkQty"), this::setDrinkQty)
                .get(PathMatcher.create("/viewDrinkPrice/*"), this::viewDrinkPrice)
                .get(PathMatcher.create("/setDrinkPrice/*"), this::setDrinkPrice);
    }

    private void buyDrink(ServerRequest request, ServerResponse response) {
        String drinkId = request.path().param("drinkId");
        LOGGER.info("[buyDrink] drinkId: " + drinkId);
        
    }

    private void listDrinks(ServerRequest request, ServerResponse response) {
        List<JsonObject> drinks = this.productDao.getAllDrinks();
        JsonArrayBuilder drinksBuilder = JSON_FACTORY.createArrayBuilder();
        for (JsonObject drink : drinks) {
            drinksBuilder.add(drink);
        }
        JsonArray drinksArray = drinksBuilder.build();
        response.send(drinksArray);
    }

    private void viewDrinkQty(ServerRequest request, ServerResponse response) {
        LOGGER.info("start viewing drinks");
        if(ControllerSetSystemStatus.getStatus(this.productDao,"isUnlocked")){
        String drinkName = request.path().toString().replace("/viewDrinkQty/", "");
        int qty = ControllerManageDrink.queryDrinkQty(this.productDao, drinkName);
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                .add("Drink Qty:", qty)
                .build();
        response.send(returnObject);
    }else{
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("View Drink Qty:", "Door locked. Please unlock door first.").build();
        response.send(returnObject);
        }
    }

    private void setDrinkQty(ServerRequest request, ServerResponse response) {
        LOGGER.info("start setting drinks qty");
        boolean canSetDrinkQuantity = ControllerSetSystemStatus.getStatus(this.productDao,"isUnlocked");
        if (canSetDrinkQuantity) {
            request.content().as(JsonObject.class).thenAccept(json -> {
                String drinkName = json.getString("name");
                String quantity = String.valueOf(json.getInt("quantity", -1));
                LOGGER.info(String.format("[setDrinkQty] Updating drink: %s with quantity: %s", drinkName, quantity));
                String status = ControllerManageDrink.setDrinkQty(this.productDao, drinkName, quantity);
                JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                    .add("success", !status.contains("Failed"))
                    .add("message", status)
                    .build();
                // TODO: Send an update to all clients via WebSocket

                response.send(returnObject);
            }).exceptionally(e -> {
                LOGGER.info("[setDrinkQty] Exception: " + e.getMessage());
                e.printStackTrace();
    
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
    
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("error", "Cannot update drink quantity!");
                data.put("message", e.toString());
    
                // LOGGER.info("[setDrinkQty] Data: " + new Gson().toJson(data));
    
                response.addHeader("Content-Type", "application/json").send(new Gson().toJson(data));
                return null;
            });
        } else {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("error", "Cannot update drink quantity!");
            data.put("message", "Door locked. Please unlock door first.");
            response.addHeader("Content-Type", "application/json").send(new Gson().toJson(data));
        }
    }

    private void viewDrinkPrice(ServerRequest request, ServerResponse response) {
        LOGGER.info("start viewing drinks");
        if(ControllerSetSystemStatus.getStatus(this.productDao, "isLoggedIn") && ControllerSetSystemStatus.getStatus(this.productDao,"isUnlocked")){
        String drinkName = request.path().toString().replace("/viewDrinkPrice/", "");
        double price = ControllerManageDrink.queryDrinkPrice(this.productDao, drinkName);
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                .add("Drink Price (in cents):", price)
                .build();
        response.send(returnObject);
       }else{
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("View Drink Price:", "System is currently locked. Please login and unlock door first.").build();
        response.send(returnObject);
       }
    }

    private void setDrinkPrice(ServerRequest request, ServerResponse response) {
        LOGGER.info("start setting drinks price");
        if(ControllerSetSystemStatus.getStatus(this.productDao, "isLoggedIn") && ControllerSetSystemStatus.getStatus(this.productDao,"isUnlocked")){
        String drinkParams = request.path().toString().replace("/setDrinkPrice/", "");
        String[] drinkTypeAndPrice = drinkParams.split(":");
        if (drinkTypeAndPrice.length != 2) {
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                    .add("Status:", "Invalid Input! Must be DrinkType:Price format")
                    .build();
            response.send(returnObject);
        } else {
            String drinkType = drinkTypeAndPrice[0];
            String drinkPrice = drinkTypeAndPrice[1];
            String status = ControllerManageDrink.setDrinkPrice(this.productDao, drinkType, drinkPrice);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                    .add("Status:", status)
                    .build();
            response.send(returnObject);
        }
        }else{
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Set Drink Price:", "System is currently locked. Please login and unlock door first.").build();
        response.send(returnObject);
       }
    }
}

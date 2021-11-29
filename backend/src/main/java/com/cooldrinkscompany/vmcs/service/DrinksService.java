package com.cooldrinkscompany.vmcs.service;

import java.util.Collections;
import java.util.List;
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
                .get(PathMatcher.create("/setDrinkQty/*"), this::setDrinkQty)
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
        if(ControllerSetSystemStatus.getStatus(this.productDao,"isUnlocked")){
        String drinkParams = request.path().toString().replace("/setDrinkQty/", "");
        String[] drinkTypeAndQty = drinkParams.split(":");
        if (drinkTypeAndQty.length != 2) {
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                    .add("Status:", "Invalid Input! Must be DrinkType:Qty format")
                    .build();
            response.send(returnObject);
        } else {
            String drinkType = drinkTypeAndQty[0];
            String drinkQty = drinkTypeAndQty[1];
            String status = ControllerManageDrink.setDrinkQty(this.productDao, drinkType, drinkQty);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                    .add("Status:", status)
                    .build();
            response.send(returnObject);
        }
        }else{
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Set Drink Qty:", "Door locked. Please unlock door first.").build();
        response.send(returnObject);
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

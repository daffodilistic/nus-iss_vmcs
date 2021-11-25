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
        .get(PathMatcher.create("/viewDrinkQty/*"), this::viewDrinkQty);
    }
/*
    private void listDrinks(ServerRequest request, ServerResponse response) {
        Multi<JsonObject> rows = this.productDao.getDbClient().execute(exec -> exec.namedQuery("select-all"))
                .map(it -> it.as(JsonObject.class));

        JsonArrayBuilder jsonBuilder = JSON_FACTORY.createArrayBuilder();

        rows.forEach(it -> {
            System.out.println(it.toString());
            jsonBuilder.add(it);
        }).whenComplete((input, exception) -> {
            if (exception != null) {
                LOGGER.log(Level.WARNING, "Failed to list drinks", exception);
                response.status(500).send();
                return;
            }

            JsonArray jsonArray = jsonBuilder.build();
            response.send(jsonArray);
        });
    }
*/
    private void listDrinks(ServerRequest request, ServerResponse response) {
        Multi<JsonObject> rows = this.productDao.getDbClient().execute(exec -> exec.createQuery("SELECT * FROM drinks").execute())
                .map(it -> it.as(JsonObject.class));

        rows.collectList().thenAccept(list -> {
            JsonArrayBuilder arrayBuilder = JSON_FACTORY.createArrayBuilder();
            list.forEach(arrayBuilder::add);
            JsonArray array = arrayBuilder.build();
            response.send(Json.createObjectBuilder().add("drinks", array).build());
        });
    }

    private void viewDrinkQty(ServerRequest request, ServerResponse response){
        LOGGER.info("start viewing drinks");
        String drinkName = request.path().toString().replace("/viewDrinkQty/","");
        int qty = ControllerManageDrink.queryDrinkQty(this.productDao, drinkName);
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                .add("Drink Qty:", qty)
                .build();
        response.send(returnObject);
    }
}

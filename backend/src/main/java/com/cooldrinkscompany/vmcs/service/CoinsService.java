package com.cooldrinkscompany.vmcs.service;

import java.util.Collections;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import io.helidon.common.reactive.Multi;
import io.helidon.webserver.*;
import com.cooldrinkscompany.vmcs.controller.ControllerManageCoin;

public class CoinsService implements Service {
    private static final Logger LOGGER = Logger.getLogger(CoinsService.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());

    private final ProductDAOImpl productDao;

    public CoinsService(ProductDAOImpl productDao) {
        this.productDao = productDao;
    }

    @Override
    public void update(Routing.Rules rules) {

        rules
        .get("/", this::listCoins)
        .get(PathMatcher.create("/viewCoinQty/*"), this::viewCoinQty);
    }

    private void listCoins(ServerRequest request, ServerResponse response) {
        Multi<JsonObject> rows = this.productDao.getDbClient().execute(exec -> exec.createQuery("SELECT * FROM coins").execute())
                .map(it -> it.as(JsonObject.class));

        rows.collectList().thenAccept(list -> {
            JsonArrayBuilder arrayBuilder = JSON_FACTORY.createArrayBuilder();
            list.forEach(arrayBuilder::add);
            JsonArray array = arrayBuilder.build();
            response.send(Json.createObjectBuilder().add("coins", array).build());
        });
    }

    private void viewCoinQty(ServerRequest request, ServerResponse response){
        LOGGER.info("start viewing coins");
        String coinName = request.path().toString().replace("/viewCoinQty/","");
        int qty = ControllerManageCoin.queryCoinQty(this.productDao, coinName);
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                .add("Coin Qty:", qty)
                .build();
        response.send(returnObject);
    }
}

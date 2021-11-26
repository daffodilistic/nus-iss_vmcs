package com.cooldrinkscompany.vmcs;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import com.cooldrinkscompany.vmcs.form.FormManageCoins;

public class CoinsService implements Service {
    private static final Logger LOGGER = Logger.getLogger(CoinsService.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());

    private final DbClient dbClient;

    public CoinsService(DbClient dbClient) {
        this.dbClient = dbClient;

        // MySQL init
        dbClient.execute(handle -> handle
                .createInsert("CREATE TABLE public.coins (id SERIAL PRIMARY KEY, name VARCHAR NULL, denomination FLOAT, quantity INT)").execute())
                .thenAccept(System.out::println).exceptionally(throwable -> {
                    LOGGER.log(Level.WARNING, "Failed to create table, maybe it already exists?", throwable);
                    return null;
                });
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::listCoins)
        .get("/queryTolAmt", this::queryTolAmt);
    }

    private void listCoins(ServerRequest request, ServerResponse response) {
        Multi<JsonObject> rows = dbClient.execute(exec -> exec.createQuery("SELECT * FROM coins").execute())
                .map(it -> it.as(JsonObject.class));

        rows.collectList().thenAccept(list -> {
            JsonArrayBuilder arrayBuilder = JSON_FACTORY.createArrayBuilder();
            list.forEach(arrayBuilder::add);
            JsonArray array = arrayBuilder.build();
            response.send(Json.createObjectBuilder().add("coins", array).build());
        });
    }

    private void queryTolAmt(ServerRequest request, ServerResponse response){
        LOGGER.info("start calculating total amount of Coins");
        float amount = FormManageCoins.queryTolAmount();
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                .add("Total Amount", amount)
                .build();
        response.send(returnObject);
    }
}

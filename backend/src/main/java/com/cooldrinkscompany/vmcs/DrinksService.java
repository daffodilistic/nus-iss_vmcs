package com.cooldrinkscompany.vmcs;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import io.helidon.common.reactive.Multi;
import io.helidon.dbclient.DbClient;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

public class DrinksService implements Service {
    private static final Logger LOGGER = Logger.getLogger(DrinksService.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());

    private final DbClient dbClient;

    DrinksService(DbClient dbClient) {
        this.dbClient = dbClient;

        // MySQL init
        dbClient.execute(handle -> handle
                .createInsert("CREATE TABLE public.drinks (id SERIAL PRIMARY KEY, name VARCHAR NULL)").execute())
                .thenAccept(System.out::println).exceptionally(throwable -> {
                    LOGGER.log(Level.WARNING, "Failed to create table, maybe it already exists?", throwable);
                    return null;
                });
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::listDrinks);
    }

    private void listDrinks(ServerRequest request, ServerResponse response) {
        // Multi<JsonObject> rows = dbClient.execute(exec -> exec.createQuery("SELECT *
        // FROM drinks").execute())
        Multi<JsonObject> rows = dbClient.execute(exec -> exec.namedQuery("select-all"))
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
}

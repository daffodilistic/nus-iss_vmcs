package com.cooldrinkscompany.vmcs;

import java.util.logging.Logger;

import javax.json.JsonObject;

import com.cooldrinkscompany.vmcs.pojo.Drink;

import java.util.logging.Level;

import io.helidon.dbclient.DbClient;
import io.helidon.common.reactive.Multi;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import io.helidon.webserver.Routing;


public class VendingMachineService implements Service {
    private static final Logger LOGGER = Logger.getLogger(VendingMachineService.class.getName());

    private final DbClient dbClient;

    VendingMachineService(DbClient dbClient) {
        this.dbClient = dbClient;

        // MySQL init
        dbClient.execute(handle -> handle.createInsert(
            "CREATE TABLE public.drinks (id SERIAL PRIMARY KEY, name VARCHAR NULL)")
            .execute()).thenAccept(System.out::println)
                .exceptionally(throwable -> {
                    LOGGER.log(Level.WARNING, "Failed to create table, maybe it already exists?", throwable);
                    return null;
                });
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::listDrinks);
    }

    private void listDrinks(ServerRequest request, ServerResponse response) {
        // Multi<JsonObject> rows = dbClient.execute(exec -> exec.createQuery("SELECT * FROM drinks").execute())
        Multi<JsonObject> rows = dbClient.execute(exec -> exec.namedQuery("select-all"))
                .map(it -> it.as(JsonObject.class));

        response.send(rows, JsonObject.class);
    }
}

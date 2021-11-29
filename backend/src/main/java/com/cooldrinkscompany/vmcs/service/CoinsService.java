package com.cooldrinkscompany.vmcs.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.cooldrinkscompany.vmcs.controller.ControllerSetSystemStatus;
import com.cooldrinkscompany.vmcs.pojo.InsertCoin;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import com.cooldrinkscompany.vmcs.pojo.Session;
import com.cooldrinkscompany.vmcs.pojo.SessionManager;
import com.cooldrinkscompany.vmcs.controller.ControllerManageCoin;

import com.google.gson.Gson;

import io.helidon.common.reactive.Multi;
import io.helidon.webserver.*;

public class CoinsService implements Service {
    private static final Logger LOGGER = Logger.getLogger(CoinsService.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private final ProductDAOImpl productDao;

    public CoinsService(ProductDAOImpl productDao) {
        this.productDao = productDao;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/", this::listCoins)
                .post("/insert", this::insertCoin)
                .get(PathMatcher.create("/viewCoinQty/*"), this::viewCoinQty)
                .get(PathMatcher.create("/setCoinQty/*"), this::setCoinQty)
                .get(PathMatcher.create("/viewCoinPrice/*"), this::viewCoinPrice)
                .get("/queryTotalAmount", this::queryTotalAmount)
                .get("/collectAllCash", this::collectAllCash);
    }

    private void insertCoin(ServerRequest request, ServerResponse response) {
        LOGGER.info("[insertCoin]");
        request.content().as(JsonObject.class).thenAccept(json -> {
            boolean isExistingSession = request.queryParams().first("sessionId").isPresent();
            // Deserialize incoming JSON into a Java InsertCoin object
            Gson gson = new Gson();
            InsertCoin coin = gson.fromJson(json.toString(), InsertCoin.class);
            // LOGGER.info(gson.toJson(coin));
            if (isExistingSession) {
                // Update coins for existing session
                // LOGGER.info("[insertCoin] existing session");
                String sessionId = request.queryParams().first("sessionId").get();
                Session session = SESSION_MANAGER.updateSession(sessionId, coin);
                response.addHeader("Content-Type", "application/json").send(gson.toJson(session));
            } else {
                // LOGGER.info("[insertCoin] new session");
                // Setup a new session for the request
                Session session = SESSION_MANAGER.createSession(coin);
                response.addHeader("Content-Type", "application/json").send(gson.toJson(session));
            }
        }).exceptionally(e -> {
            LOGGER.info("[insertCoin] Exception: " + e.getMessage());
            e.printStackTrace();

            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("error", "Invalid coin!");
            data.put("reason", e.toString());

            // LOGGER.info("[insertCoin] Data: " + new Gson().toJson(data));

            response.addHeader("Content-Type", "application/json").send(new Gson().toJson(data));
            return null;
        });
    }

    private void listCoins(ServerRequest request, ServerResponse response) {
        Multi<JsonObject> rows = this.productDao.getDbClient()
                .execute(exec -> exec.createQuery("SELECT * FROM coins").execute()).map(it -> it.as(JsonObject.class));

        rows.collectList().thenAccept(list -> {
            JsonArrayBuilder arrayBuilder = JSON_FACTORY.createArrayBuilder();
            list.forEach(arrayBuilder::add);
            JsonArray array = arrayBuilder.build();
            response.send(Json.createObjectBuilder().add("coins", array).build());
        });
    }

    private void viewCoinQty(ServerRequest request, ServerResponse response) {
        LOGGER.info("start viewing coins qty");
        String coinName = request.path().toString().replace("/viewCoinQty/", "");
        int qty = ControllerManageCoin.queryCoinQty(this.productDao, coinName);
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Coin Qty:", qty !=Integer.MAX_VALUE ? String.valueOf(qty) : "ERROR: Coin type does not exist").build();
        response.send(returnObject);
    }

    private void setCoinQty(ServerRequest request, ServerResponse response) {
        LOGGER.info("start setting coins qty");
        String coinParams = request.path().toString().replace("/setCoinQty/", "");
        String[] coinTypeAndQty = coinParams.split(":");
        if (coinTypeAndQty.length != 2) {
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                    .add("Status:", "Invalid Input! Must be CoinType:Qty format").build();
            response.send(returnObject);
        } else {
            String coinType = coinTypeAndQty[0];
            String coinQty = coinTypeAndQty[1];
            String status = ControllerManageCoin.setCoinQty(this.productDao, coinType, coinQty);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:", status).build();
            response.send(returnObject);
        }
    }

    private void viewCoinPrice(ServerRequest request, ServerResponse response) {
        LOGGER.info("start viewing coins");
        String coinName = request.path().toString().replace("/viewCoinPrice/", "");
        double price = ControllerManageCoin.queryCoinPrice(this.productDao, coinName);
        JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Coin Denomination:", price).build();
        response.send(returnObject);
    }

    private void queryTotalAmount(ServerRequest request, ServerResponse response) {
        LOGGER.info("start calculation all coins value");
        boolean canGetTotalAmount = ControllerSetSystemStatus.getStatus(this.productDao, "isLoggedIn")
                && ControllerSetSystemStatus.getStatus(this.productDao, "isUnlocked");
        JsonObjectBuilder builder = JSON_FACTORY.createObjectBuilder().add("success", canGetTotalAmount);
        if (canGetTotalAmount) {
            float totalCash = ControllerManageCoin.queryTotalAmount(this.productDao);
            builder.add("total_cash", totalCash);
            response.send(builder.build());
        } else {
            builder.addNull("total_cash");
            builder.add("message", "System is currently locked. Please login and unlock door first.");
            response.send(builder.build());
        }
    }

    private void collectAllCash(ServerRequest request, ServerResponse response) {
        LOGGER.info("start collecting all cash");
        boolean canCashOut = ControllerSetSystemStatus.getStatus(this.productDao, "isLoggedIn")
                && ControllerSetSystemStatus.getStatus(this.productDao, "isUnlocked");
        JsonObjectBuilder builder = JSON_FACTORY.createObjectBuilder().add("success", canCashOut);
        if (canCashOut) {
            float cashOut = ControllerManageCoin.queryTotalAmount(this.productDao);
            builder.add("cash_out", cashOut);
            response.send(builder.build());
        } else {
            builder.addNull("total_cash");
            builder.add("message", "System is currently locked. Please login and unlock door first.");
            response.send(builder.build());
        }

    }
}

package com.cooldrinkscompany.vmcs.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
                .put("/setCoinQty", this::setCoinQty)
                .get(PathMatcher.create("/viewCoinPrice/*"), this::viewCoinPrice)
                .get("/queryTotalAmount", this::queryTotalAmount)
                .get("/collectAllCash", this::collectAllCash);
    }

    private void insertCoin(ServerRequest request, ServerResponse response) {
        // LOGGER.info("[insertCoin]");
        request.content().as(JsonObject.class).thenAccept(json -> {
            boolean isExistingSession = request.queryParams().first("sessionId").isPresent();
            // Deserialize incoming JSON into a Java InsertCoin object
            Gson gson = new Gson();
            InsertCoin coin = gson.fromJson(json.toString(), InsertCoin.class);
            // LOGGER.info(gson.toJson(coin));
            if (!coin.country.equals("SG")) {
                throw new IllegalArgumentException("Only Singapore coins are supported");
            }

            if (isExistingSession) {
                // Update coins for existing session
                LOGGER.info("[insertCoin] existing session");
                String sessionId = request.queryParams().first("sessionId").get();
                Session session = SESSION_MANAGER.updateSession(sessionId, coin);
                response.addHeader("Content-Type", "application/json").send(gson.toJson(session));
            } else {
                LOGGER.info("[insertCoin] new session");
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
            data.put("message", e.getMessage());

            // LOGGER.info("[insertCoin] Data: " + new Gson().toJson(data));

            response.addHeader("Content-Type", "application/json").send(new Gson().toJson(data));
            return null;
        });
    }

    private void listCoins(ServerRequest request, ServerResponse response) {
        List<JsonObject> coins = this.productDao.getAllCoins();
        JsonArrayBuilder coinsBuilder = JSON_FACTORY.createArrayBuilder();
        for (JsonObject coin : coins) {
            coinsBuilder.add(coin);
        }
        JsonArray coinsArray = coinsBuilder.build();
        response.send(coinsArray);
    }

    private void viewCoinQty(ServerRequest request, ServerResponse response) {
        LOGGER.info("start viewing coins qty");
        if (ControllerSetSystemStatus.getStatus(this.productDao, "isUnlocked")) {
            String coinName = request.path().toString().replace("/viewCoinQty/", "");
            int qty = ControllerManageCoin.queryCoinQty(this.productDao, coinName);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                    .add("Coin Qty:",
                            qty != Integer.MAX_VALUE ? String.valueOf(qty) : "ERROR: Coin type does not exist")
                    .build();
            response.send(returnObject);
        } else {
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                    .add("Set Coin Qty:", "Door locked. Please unlock door first.").build();
            response.send(returnObject);
        }
    }

    private void setCoinQty(ServerRequest request, ServerResponse response) {
        LOGGER.info("start setting coins qty");
        boolean canSetCoinQuantity = ControllerSetSystemStatus.getStatus(this.productDao, "isUnlocked");
        if (canSetCoinQuantity) {
            request.content().as(JsonObject.class).thenAccept(json -> {
                String coinName = json.getString("name");
                String quantity = String.valueOf(json.getInt("quantity", -1));
                String status = ControllerManageCoin.setCoinQty(this.productDao, coinName, quantity);
                JsonObject returnObject = JSON_FACTORY.createObjectBuilder()
                        .add("success", !status.contains("Failed"))
                        .add("message", status)
                        .build();
                SessionManager.getInstance().updateMachineStatus();
                response.send(returnObject);
            }).exceptionally(e -> {
                LOGGER.info("[setCoinQty] Exception: " + e.getMessage());
                e.printStackTrace();

                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("error", "Cannot update coin quantity!");
                data.put("message", e.getMessage());

                // LOGGER.info("[setCoinQty] Data: " + new Gson().toJson(data));

                response.addHeader("Content-Type", "application/json").send(new Gson().toJson(data));
                return null;
            });
        } else {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("error", "Cannot update coin quantity!");
            data.put("message", "Door locked. Please unlock door first.");
            response.addHeader("Content-Type", "application/json").send(new Gson().toJson(data));
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
            float cashOut = Float.parseFloat(ControllerManageCoin.collectAllCash(this.productDao));
            builder.add("cash_out", cashOut);
            SessionManager.getInstance().updateMachineStatus();
            response.send(builder.build());
        } else {
            builder.addNull("cash_out");
            builder.add("message", "System is currently locked. Please login and unlock door first.");
            response.send(builder.build());
        }

    }
}

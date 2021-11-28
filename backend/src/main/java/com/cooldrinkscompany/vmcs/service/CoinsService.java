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

import com.cooldrinkscompany.vmcs.controller.ControllerSetSystemStatus;
import com.cooldrinkscompany.vmcs.pojo.InsertCoin;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import com.cooldrinkscompany.vmcs.pojo.Session;
import com.cooldrinkscompany.vmcs.pojo.SessionManager;
import com.google.gson.Gson;

import io.helidon.common.reactive.Multi;
import io.helidon.config.Config;
import io.helidon.webserver.*;
import com.cooldrinkscompany.vmcs.controller.ControllerManageCoin;

public class CoinsService implements Service {
    private static final Logger LOGGER = Logger.getLogger(CoinsService.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private final ProductDAOImpl productDao;
    private final Config config;

    public CoinsService(ProductDAOImpl productDao) {
        this.productDao = productDao;
        this.config = Config.create();
    }

    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/", this::listCoins)
                .post("/insert", this::insertCoin)
                .get(PathMatcher.create("/viewCoinQty/*"), this::viewCoinQty)
                .get(PathMatcher.create("/setCoinQty/*"), this::setCoinQty)
                .get(PathMatcher.create("/viewCoinPrice/*"), this::viewCoinPrice)
                .get("/queryTotalAmount",this::queryTotalAmount)
                .get("/collectAllCash", this::collectAllCash)
                .get("/loggin/*", this::loggin);
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

    private boolean validatePassword(String inputPassword){
        String realPassword = this.config.get("password").asMap().get().get("password");
        if (inputPassword.equals(realPassword)){
            return true;
        }
        return false;
    }

    private void loggin(ServerRequest request, ServerResponse response){
        String inputPassword = request.path().toString().replace("/loggin/", "");
        if(validatePassword(inputPassword)){
            String logginResponse = ControllerSetSystemStatus.setLoggedIn(this.productDao);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:",logginResponse).build();
            response.send(returnObject);
        }else{
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Status:", "Failed to loggin with password: " + inputPassword).build();
            response.send(returnObject);
        }
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
        if(ControllerSetSystemStatus.getStatus(this.productDao, "isLoggedIn") && ControllerSetSystemStatus.getStatus(this.productDao,"isUnlocked")){
            float totalAMt = ControllerManageCoin.queryTotalAmount(this.productDao);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Total Cash Held (in cents):", totalAMt!=Float.MAX_VALUE ? String.valueOf(totalAMt) :"ERROR").build();
            response.send(returnObject);
        }else{
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Total Cash Held (in cents):", "System is currently locked. Please login and unlock door first.").build();
            response.send(returnObject);
        }
    }

    private void collectAllCash(ServerRequest request, ServerResponse response) {
        LOGGER.info("start collecting all cash");
        if(ControllerSetSystemStatus.getStatus(this.productDao, "isLoggedIn") && ControllerSetSystemStatus.getStatus(this.productDao,"isUnlocked")){
            String totalAMt = ControllerManageCoin.collectAllCash(this.productDao);
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Collected Cash (in cents):", totalAMt).build();
            response.send(returnObject);
        }else{
            JsonObject returnObject = JSON_FACTORY.createObjectBuilder().add("Total Cash Held (in cents):", "System is currently locked. Please login and unlock door first.").build();
            response.send(returnObject);
        }

    }
}

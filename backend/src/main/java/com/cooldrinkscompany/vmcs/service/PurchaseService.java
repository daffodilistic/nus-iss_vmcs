package com.cooldrinkscompany.vmcs.service;

import java.beans.Statement;
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

import com.cooldrinkscompany.vmcs.controller.ControllerSetSystemStatus;
import com.cooldrinkscompany.vmcs.pojo.Coin;
import com.cooldrinkscompany.vmcs.pojo.Drink;
import com.cooldrinkscompany.vmcs.pojo.Product;
import com.cooldrinkscompany.vmcs.pojo.ProductDAO;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;
import com.cooldrinkscompany.vmcs.pojo.Session;
import com.cooldrinkscompany.vmcs.pojo.SessionManager;
import com.google.gson.Gson;

import io.helidon.common.reactive.Multi;
import io.helidon.config.Config;
import io.helidon.webserver.*;
import com.cooldrinkscompany.vmcs.controller.ControllerManageCoin;
import com.cooldrinkscompany.vmcs.controller.ControllerManageDrink;

public class PurchaseService implements Service {
    private static final Logger LOGGER = Logger.getLogger(PurchaseService.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private final Product _product;
    private final Config _config;
    private final DrinksService _drinkService;
    private final ProductDAOImpl _productDAO;

    public PurchaseService(Product product, ProductDAOImpl productDao) {
        this._product = product;
        this._config = Config.create();
        this._drinkService = new DrinksService(productDao);
        this._productDAO = productDao;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::listPurchase).post("/purchase", this::chooseDrink);
                //.get(PathMatcher.create("/viewCoinQty/*"), this::viewCoinQty)
                
    }

    private void chooseDrink(ServerRequest request, ServerResponse response)
    {
        LOGGER.info("[chooseDrink]");
    }


    private void pruchaseDrink(ServerRequest request, ServerResponse response) {
        LOGGER.info("[purchaseDrink]");
        request.content().as(JsonObject.class).thenAccept(json -> {
            boolean isExistingSession_purchase = request.queryParams().first("sessionId").isPresent();
            // Deserialize incoming JSON into a Java InsertCoin object
            Gson gson = new Gson();
            Drink drink = gson.fromJson(json.toString(), Drink.class);
            LOGGER.info(gson.toJson(drink));
            if (isExistingSession_purchase) {
                
            } else {
                // Setup a new session for the request
                Session session = SESSION_MANAGER.purchaseSession(drink);

                Multi<JsonObject> drinks = this._productDAO.getDbClient().execute(exec -> exec.createQuery("SELECT * FROM drinks").execute())
                .map(it -> it.as(JsonObject.class));
                int qty = ControllerManageDrink.queryDrinkQty(this._productDAO, drink.getName());
                if(qty <= 0)
                { 
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("invalid", "drink out of stock");
                }
                else
                {
                    //get inserted coins
                    String insertedCoinsSessionId = request.queryParams().first("sessionId").toString();
                    Session insertedCoinsSession = SESSION_MANAGER.getSession(insertedCoinsSessionId);


                    Gson gInsertedCoins = new Gson();
                    //Coin coin = gson.fromJson(json.toString(), InsertCoin.class);

                    //get inserted coin
                    Multi<JsonObject> jCoins = this._productDAO.getDbClient()
                    .execute(exec -> exec.createQuery("SELECT * FROM coins").execute()).map(it -> it.as(JsonObject.class));

                    //invalid coins check: pending
                    

                    //dispense product
                    //minus drink
                    qty -= 1;
                    //this._productDAO.setDrinksQty("drink", qty);
                    
                    //Multi<JsonObject> udpateDrink = this._productDAO.getDbClient()
                    //        .execute(exec -> exec.createQuery("UPDATE drinks SET quantity = " + qty + " WHERE id=" + drink.getId()).execute()).map(it -> it.as(JsonObject.class));
                    //minus coins


                }
            }
            response.send(drink);
        }).exceptionally(e -> {
            LOGGER.info("[purchaseDrink] Exception: " + e.getMessage());
            // e.printStackTrace();
            
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("error", e.getMessage());
            // data.put("stacktrace", stackTrace.toString());
            
            // LOGGER.info("[insertCoin] Data: " + new Gson().toJson(data));
            
            response.addHeader("Content-Type", "application/json").send(new Gson().toJson(data));
            return null;
        });
    }

    private void listPurchase(ServerRequest request, ServerResponse response) {
        Multi<JsonObject> rows = this._productDAO.getDbClient()
                .execute(exec -> exec.createQuery("SELECT * FROM drinks").execute()).map(it -> it.as(JsonObject.class));

        rows.collectList().thenAccept(list -> {
            JsonArrayBuilder arrayBuilder = JSON_FACTORY.createArrayBuilder();
            list.forEach(arrayBuilder::add);
            JsonArray array = arrayBuilder.build();
            response.send(Json.createObjectBuilder().add("Purchase", array).build());
        });
    }
}
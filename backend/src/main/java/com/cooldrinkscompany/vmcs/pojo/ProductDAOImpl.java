package com.cooldrinkscompany.vmcs.pojo;


import java.util.ArrayList;
import java.util.List;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;


public class ProductDAOImpl implements ProductDAO {

    private static final Logger LOGGER = Logger.getLogger(ProductDAOImpl.class.getName());
    private static final JsonBuilderFactory JSON_FACTORY = Json.createBuilderFactory(Collections.emptyMap());
    public final String createDrinksTable = "CREATE TABLE public.drinks (id SERIAL PRIMARY KEY, name VARCHAR NULL, price FLOAT, quantity INT)";
    public final String createCoinsTable = "CREATE TABLE public.coins (id SERIAL PRIMARY KEY, name VARCHAR NULL, denomination FLOAT, quantity INT)";
    public final String getCoinsQty = "SELECT quantity FROM coins where name = '%s'";
    public final String setCoinsQty = "UPDATE coins SET quantity = %s where name = '%s'";
    public final String getDrinksQty = "SELECT quantity FROM drinks where name = '%s'";
    public final String setDrinksQty = "UPDATE drinks SET quantity = %s where name = '%s'";
    public final String getDrinksPrice = "SELECT price FROM drinks where name = '%s'";
    public final String setDrinksPrice = "UPDATE drinks SET price = %s where name = '%s'";
    public final String getCoinsPrice = "SELECT denomination FROM coins where name = '%s'";
    public final String getAllCoins = "SELECT denomination, quantity FROM coins";
    public final String collectAllCoins = "UPDATE coins SET quantity = 0";
    private final DbClient dbClient;

    public ProductDAOImpl(DbClient dbClient) {
        this.dbClient = dbClient;

         // MySQL init Drinks Table
         dbClient.execute(handle -> handle
         .createInsert(createDrinksTable).execute())
         .thenAccept(System.out::println).exceptionally(throwable -> {
             LOGGER.log(Level.WARNING, "Failed to create drinks table, maybe it already exists?", throwable);
             return null;
         });

        // MySQL init Coins Table
        dbClient.execute(handle -> handle
                .createInsert(createCoinsTable).execute())
                .thenAccept(System.out::println).exceptionally(throwable -> {
            LOGGER.log(Level.WARNING, "Failed to create coins table, maybe it already exists?", throwable);
            return null;
        });
    }

    public DbClient getDbClient() {
        return dbClient;
    }
   
    //Coin Methods
    public String getCoinQuantity(String name){
        try {
            String sql = String.format(this.getCoinsQty,name);
            LOGGER.info("getCoinQty full sql is: " + sql);
            Single<List<JsonObject>> rows = dbClient.execute(exec -> exec.createQuery(sql).execute())
                    .map(it -> it.as(JsonObject.class)).collectList();
            JsonObject result = rows.get().get(0);
            return result.getJsonNumber("quantity").toString();
        }catch (Exception e){
            LOGGER.info(e.toString());
            return "NA";
        }
    }

    public String setCoinQuantity(String name, String quantity){
        try {
            String sql = String.format(this.setCoinsQty, quantity, name);
            LOGGER.info("setCoinQty full sql is: " + sql);
            String sqlResponse = dbClient.execute(exec -> exec.update(sql)).get().toString();
            if (sqlResponse.equals("0")){
                return "SQL Statement update failed. Do you indicate the correct coin name?";
            }else{
                return "Success";
            }
        }catch (Exception e){
            LOGGER.info(e.toString());
            return "Failed. Unexpected error, please check log.";
        }
    }

    public String getCoinPrice(String name){
        try {
            String sql = String.format(this.getCoinsPrice,name);
            LOGGER.info("getCoinPrice full sql is: " + sql);
            Single<List<JsonObject>> rows = dbClient.execute(exec -> exec.createQuery(sql).execute())
                    .map(it -> it.as(JsonObject.class)).collectList();
            JsonObject result = rows.get().get(0);
            return result.getJsonNumber("denomination").toString();
        }catch (Exception e){
            LOGGER.info(e.toString());
            return "NA";
        }
    }

    public List<JsonObject> getAllCoins(){
        try {
            String sql = this.getAllCoins;
            LOGGER.info("getAllCoins full sql is: " + sql);
            Single<List<JsonObject>> rows = dbClient.execute(exec -> exec.createQuery(sql).execute())
                    .map(it -> it.as(JsonObject.class)).collectList();
            List<JsonObject> result = rows.get();
            return result;
        }catch (Exception e){
            LOGGER.info(e.toString());
            return new ArrayList<JsonObject>();
        }
    }

    public String collectAllCoins(){
        try {
            String sql = this.collectAllCoins;
            LOGGER.info("collectAllCoins full sql is: " + sql);
            String sqlResponse = dbClient.execute(exec -> exec.update(sql)).get().toString();
            if (sqlResponse.equals("0")){
                return "SQL Statement update failed. Please see logs.";
            }else{
                return "Success";
            }
        }catch (Exception e){
            LOGGER.info(e.toString());
            return "NA";
        }

    }


    //Drink methods
    public String getDrinkQuantity(String name){
        try {
            String sql = String.format(this.getDrinksQty,name);
            LOGGER.info("getDrinkQty full sql is: " + sql);
            Single<List<JsonObject>> rows = dbClient.execute(exec -> exec.createQuery(sql).execute())
                    .map(it -> it.as(JsonObject.class)).collectList();
            JsonObject result = rows.get().get(0);
            return result.getJsonNumber("quantity").toString();
        }catch (Exception e){
            LOGGER.info(e.toString());
            return "NA";
        }
    }

    public String setDrinkQuantity(String name, String quantity){
        try {
            String sql = String.format(this.setDrinksQty, quantity, name);
            LOGGER.info("setDrinkQty full sql is: " + sql);
            String sqlResponse = dbClient.execute(exec -> exec.update(sql)).get().toString();
            if (sqlResponse.equals("0")){
                return "SQL Statement update failed. Do you indicate the correct Drink name?";
            }else{
                return "Success";
            }
        }catch(NumberFormatException ne){
            LOGGER.info(ne.toString());
            return "Failed. Input qty cannot convert to integer.";
        } catch (Exception e){
            LOGGER.info(e.toString());
            return "Failed. Unexpected error, please check log.";
        }
    }

    public String getDrinkPrice(String name){
        try {
            String sql = String.format(this.getDrinksPrice,name);
            LOGGER.info("getDrinkPrice full sql is: " + sql);
            Single<List<JsonObject>> rows = dbClient.execute(exec -> exec.createQuery(sql).execute())
                    .map(it -> it.as(JsonObject.class)).collectList();
            JsonObject result = rows.get().get(0);
            return result.getJsonNumber("price").toString();
        }catch (Exception e){
            LOGGER.info(e.toString());
            return "NA";
        }
    }

    public String setDrinkPrice(String name, String price){
        try {
            String sql = String.format(this.setDrinksPrice, price, name);
            LOGGER.info("setDrinkPrice full sql is: " + sql);
            String sqlResponse = dbClient.execute(exec -> exec.update(sql)).get().toString();
            if (sqlResponse.equals("0")){
                return "SQL Statement update failed. Do you indicate the correct Drink name?";
            }else{
                return "Success";
            }
        }catch(NumberFormatException ne){
            LOGGER.info(ne.toString());
            return "Failed. Input price cannot convert to integer.";
        } catch (Exception e){
            LOGGER.info(e.toString());
            return "Failed. Unexpected error, please check log.";
        }
    }

}    
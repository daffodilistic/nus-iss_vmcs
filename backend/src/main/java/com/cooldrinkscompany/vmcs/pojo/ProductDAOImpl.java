package com.cooldrinkscompany.vmcs.pojo;


import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObject;

import io.helidon.common.reactive.Single;
import io.helidon.dbclient.DbClient;


public class ProductDAOImpl implements ProductDAO {

    private static final Logger LOGGER = Logger.getLogger(ProductDAOImpl.class.getName());
    public final String createDrinksTable = "CREATE TABLE public.drinks (id SERIAL PRIMARY KEY, name VARCHAR NULL, price FLOAT, quantity INT)";
    public final String createCoinsTable = "CREATE TABLE public.coins (id SERIAL PRIMARY KEY, name VARCHAR NULL, denomination FLOAT, quantity INT)";
    public final String createSystemTable = "CREATE TABLE public.system (id SERIAL PRIMARY KEY, name VARCHAR NULL, status BOOLEAN)";
    public final String getCoinsQty = "SELECT quantity FROM coins where name = '%s'";
    public final String setCoinsQty = "UPDATE coins SET quantity = %s where name = '%s'";
    public final String getDrinksQty = "SELECT quantity FROM drinks where name = '%s'";
    public final String setDrinksQty = "UPDATE drinks SET quantity = %s where name = '%s'";
    public final String getDrinksPrice = "SELECT price FROM drinks where name = '%s'";
    public final String setDrinksPrice = "UPDATE drinks SET price = %s where name = '%s'";
    public final String getCoinsPrice = "SELECT denomination FROM coins where name = '%s'";
    public final String getAllCoins = "SELECT denomination, quantity FROM coins";
    public final String collectAllCoins = "UPDATE coins SET quantity = 0";
    public final String setStatus = "UPDATE system SET status = %s where name = '%s'";
    public final String getStatus = "SELECT status FROM system where name = '%s'";
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

        // MySQL init SystemStatus Table
        dbClient.execute(handle -> handle
                .createInsert(createSystemTable).execute())
                .thenAccept(System.out::println).exceptionally(throwable -> {
            LOGGER.log(Level.WARNING, "Failed to create system table, maybe it already exists?", throwable);
            return null;
        });
        // Seed SystemStatus Table
        dbClient.execute(handle -> handle
                .createInsert(
                "INSERT INTO public.system (id, name, status) VALUES (1,'isLoggedIn',FALSE) ON CONFLICT DO UPDATE;" + 
                "INSERT INTO public.system (id, name, status) VALUES (2,'isUnlocked',FALSE) ON CONFLICT DO UPDATE;")
                .execute())
                .thenAccept(System.out::println).exceptionally(throwable -> {
            LOGGER.log(Level.WARNING, "Failed to seed system table!", throwable);
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
            return new ArrayList<>();
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
    public String setStatus(String name, boolean status){
        try {
            String sql = String.format(this.setStatus, status, name);
            LOGGER.info("setStatus full sql is: " + sql);
            String sqlResponse = dbClient.execute(exec -> exec.update(sql)).get().toString();
            if (sqlResponse.equals("0")){
                return "SQL Statement update failed.";
            }else {
                return "Success";
            }
        }catch (Exception e){
            LOGGER.info(e.toString());
            return "NA";
        }
    }
    public boolean getStatus(String name){
        try {
            String sql = String.format(this.getStatus,name);
            LOGGER.info("getSystemStatus full sql is: " + sql);
            Single<List<JsonObject>> rows = dbClient.execute(exec -> exec.createQuery(sql).execute())
                    .map(it -> it.as(JsonObject.class)).collectList();
            JsonObject result = rows.get().get(0);
            return result.getBoolean("status");
        }catch (Exception e){
            LOGGER.info(e.toString());
            return false;
        }
    }

}    
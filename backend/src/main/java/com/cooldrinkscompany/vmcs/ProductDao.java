package com.cooldrinkscompany.vmcs;

import java.io.*;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

public class ProductDao {

    private JSONObject connection;
    private final String jsonPath = "backend/src/main/resources/database.json";
    private static final Logger LOGGER = Logger.getLogger(ProductDao.class.getName());

    public ProductDao() throws IOException {
        LOGGER.info("Initiating ProductDAO...");
        LOGGER.info("Connecting to database...");
        this.connection = getConnection();
        LOGGER.info("Database connected!");
    }

    public JSONObject getConnection() throws IOException {
        File f = new File(jsonPath);
        if (f.exists()){
            InputStream is = new FileInputStream(jsonPath);
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            JSONObject json = new JSONObject(jsonTxt);
            return json;
        }else{
            LOGGER.info("JSON file path cannot be found at "+f.getAbsolutePath()+"！");
            throw new FileNotFoundException();
        }
    }

    private JSONObject getDrinkObj(int drinkId){
        return this.connection.getJSONObject("drinks").getJSONObject(String.valueOf(drinkId));
    }

    private JSONObject getCoinObj(int coinId){
        return this.connection.getJSONObject("coins").getJSONObject(String.valueOf(coinId));
    }

    public int getDrinkQty(int drinkId){
        return this.getDrinkObj(drinkId).getInt("quantity");
    }

    public float getDrinkPrice(int drinkId){
        return this.getDrinkObj(drinkId).getFloat("price");
    }

    public String getDrinkName(int drinkId){
        return this.getDrinkObj(drinkId).getString("name");
    }

    public int getCoinQty(int CoinId){
        return this.getCoinObj(CoinId).getInt("quantity");
    }

    public float getCoinPrice(int CoinId){
        return this.getCoinObj(CoinId).getFloat("price");
    }

    public String getCoinName(int CoinId){
        return this.getCoinObj(CoinId).getString("name");
    }

    public static void main(String[] args) throws IOException {
        ProductDao dao = new ProductDao();

        System.out.println("Getting Cola:");
        System.out.println("Qty:" + dao.getDrinkQty(1));
        System.out.println("Price:" + dao.getDrinkPrice(1));
        System.out.println("Name:" + dao.getDrinkName(1));

        System.out.println("Getting 50c:");
        System.out.println("Qty:" + dao.getCoinQty(4));
        System.out.println("Price:" + dao.getCoinPrice(4));
        System.out.println("Name:" + dao.getCoinName(4));
    }
}

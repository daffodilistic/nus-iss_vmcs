package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.iterator.CoinIterator;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;

import javax.json.JsonObject;
import java.util.logging.Logger;

public class ControllerManageCoin {

    private static CoinIterator createIterator(ProductDAOImpl dao){
        return new CoinIterator(dao);
    }

    public static float queryTotalAmount(ProductDAOImpl dao){
        Logger LOGGER = Logger.getLogger(ControllerManageCoin.class.getName());
        CoinIterator coinIterator = createIterator(dao);
        float total = 0.0f;
        if (!coinIterator.first().equals(null)){
            JsonObject coin = coinIterator.first();
            float denomination = Float.parseFloat(coin.getJsonNumber("denomination").toString());
            int quantity = Integer.parseInt(coin.getJsonNumber("quantity").toString());
            LOGGER.info("Row is: " + " Denom: " + denomination + " Qty: " + quantity );
            total += denomination*quantity;
            while(!coinIterator.isDone()){
                coin = coinIterator.next();
                denomination = Float.parseFloat(coin.getJsonNumber("denomination").toString());
                quantity = Integer.parseInt(coin.getJsonNumber("quantity").toString());
                LOGGER.info("Row is: " + " Denom: " + denomination + " Qty: " + quantity );
                total += denomination*quantity;
            }
            return total;
        }
        return Float.MAX_VALUE;
    }

    public static String collectAllCash(ProductDAOImpl dao){
        float totalCash = queryTotalAmount(dao);
        if (totalCash == Float.MAX_VALUE){
            return "Collection failed. Total cash cannot be calculated.";
        }else{
            String response = dao.collectAllCoins();
            if (response.equals("Success")){
                return String.valueOf(totalCash);
            }else{
                return response;
            }

        }
    }

    private static String validateQty(String quantity){
        try{
            int qty = Integer.parseInt(quantity);
            if (qty<0 || qty>40){
                return "Failed. Quantity cannot less than 0 or greater than 40";
            }
            return "Pass";
        }catch(NumberFormatException ne){
            return "Failed. Input qty cannot convert to integer.";
        }
    }

    public static int queryCoinQty(ProductDAOImpl dao, String name){
        String qty = dao.getCoinQuantity(name);
        if(qty.equals("NA")){
            return Integer.MAX_VALUE;
        }else{
            return Integer.parseInt(qty);
        }
    }
    public static String setCoinQty(ProductDAOImpl dao, String coinType, String coinQty){
        if (validateQty(coinQty).equals("Pass")){
            String status = dao.setCoinQuantity(coinType, coinQty);
            return status;
        }else{
            return validateQty(coinQty);
        }
    }
 
    public static double queryCoinPrice(ProductDAOImpl dao, String name){
        String price = dao.getCoinPrice(name);
        if(price.equals("NA")){
            return Double.MAX_VALUE;
        }else{
            return Double.parseDouble(price);
        }
    }

}

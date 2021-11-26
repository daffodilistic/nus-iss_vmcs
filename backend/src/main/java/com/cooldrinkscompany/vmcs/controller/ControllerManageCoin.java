package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.iterator.CoinIterator;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;

import java.util.logging.Logger;

public class ControllerManageCoin {

    private CoinIterator createIterator(){
        return null;
    }

    public static float queryTolAmount(){
        return 0.0f;
    }

    private static String validateQty(String quantity){
        try{
            int qty = Integer.parseInt(quantity);
            if (qty<=0 || qty>40){
                return "Quantity cannot less than 0 or greater than 40";
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

package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;

public class ControllerManageDrink {

    
    public static int queryDrinkQty(ProductDAOImpl dao, String name){
        String qty = dao.getDrinkQuantity(name);
        if(qty.equals("NA")){
            return Integer.MAX_VALUE;
        }else{
            return Integer.parseInt(qty);
        }
    }

    public static String setDrinkQty(ProductDAOImpl dao, String drinkType, String drinkQty){
        String status = dao.setDrinkQuantity(drinkType, drinkQty);
        return status;
    }


    public static double queryDrinkPrice(ProductDAOImpl dao, String name){
        String price = dao.getDrinkPrice(name);
        if(price.equals("NA")){
            return Double.MAX_VALUE;
        }else{
            return Double.parseDouble(price);
        }
    }

    public static String setDrinkPrice(ProductDAOImpl dao, String drinkType, String drinkPrice){
        String status = dao.setDrinkPrice(drinkType, drinkPrice);
        return status;
    }
}

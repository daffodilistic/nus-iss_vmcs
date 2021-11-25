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
}

package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;

public class ControllerManageDrink {

    
    public static int queryDrinkQty(ProductDAOImpl dao, String name){
        String qty = dao.getCoinQuantity(name);
        if(qty.equals("NA")){
            return Integer.MAX_VALUE;
        }else{
            return Integer.parseInt(qty);
        }
    }
}

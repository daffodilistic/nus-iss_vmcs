package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.iterator.CoinIterator;
import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;

public class ControllerManageCoin {

    private CoinIterator createIterator(){
        return null;
    }

    public static float queryTolAmount(){
        return 0.0f;
    }

    public static int queryCoinQty(ProductDAOImpl dao, String name){
        String qty = dao.getQuantity(name);
        if(qty.equals("NA")){
            return Integer.MAX_VALUE;
        }else{
            return Integer.parseInt(qty);
        }
    }

}

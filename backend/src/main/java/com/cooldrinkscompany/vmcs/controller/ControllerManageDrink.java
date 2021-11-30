package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;

public class ControllerManageDrink {

    private static String validateQty(String quantity){
        try{
            int qty = Integer.parseInt(quantity);
            if (qty<0 || qty>20){
                return "Failed. Quantity cannot less than 0 or greater than 20";
            }
            return "Pass";
        }catch(NumberFormatException ne){
            return "Failed. Input qty cannot convert to integer.";
        }
    }

    
    public static int queryDrinkQty(ProductDAOImpl dao, String name){
        String qty = dao.getDrinkQuantity(name);
        if(qty.equals("NA")){
            return Integer.MAX_VALUE;
        }else{
            return Integer.parseInt(qty);
        }
    }

    public static String setDrinkQty(ProductDAOImpl dao, String drinkType, String drinkQty){
        if (validateQty(drinkQty).equals("Pass")){
        String status = dao.setDrinkQuantity(drinkType, drinkQty);
        return status;
        }else{
        return validateQty(drinkQty);
        }        
    }


    public static double queryDrinkPrice(ProductDAOImpl dao, String name){
        String price = dao.getDrinkPrice(name);
        if(price.equals("NA")){
            return Double.MAX_VALUE;
        }else{
            return Double.parseDouble(price);
        }
    }

    public static String setDrinkPrice(ProductDAOImpl dao, String drinkName, String drinkPrice){
        String status = dao.setDrinkPrice(drinkName, drinkPrice);
        return status;
    }
}

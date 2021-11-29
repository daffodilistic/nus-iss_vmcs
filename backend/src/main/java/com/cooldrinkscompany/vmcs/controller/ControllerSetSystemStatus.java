package com.cooldrinkscompany.vmcs.controller;

import com.cooldrinkscompany.vmcs.pojo.ProductDAOImpl;

public class ControllerSetSystemStatus {

    public static String setLoggedIn(ProductDAOImpl dao){
        String logginResponse = dao.setStatus("isLoggedIn", true);
        String DoorResponse = dao.setStatus("isUnlocked", true);
        if (logginResponse.equals("Success") && DoorResponse.equals("Success")){
            return "success, Logged-in and Door unlocked";
        }else{
            return logginResponse + " " + DoorResponse;
        }
    }

    public static String setStatus(ProductDAOImpl dao, String name, boolean status){
        return dao.setStatus(name, status);
    }

    public static boolean getStatus(ProductDAOImpl dao, String name){
        return dao.getStatus(name);
    }

}

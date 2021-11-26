package com.cooldrinkscompany.vmcs.form;
import com.cooldrinkscompany.vmcs.controller.ControllerManageCoin;

import java.io.IOException;

public class FormManageCoins {

    public static float queryTolAmount(){
        float amount = ControllerManageCoin.queryTolAmount();
        return amount;
    }

}

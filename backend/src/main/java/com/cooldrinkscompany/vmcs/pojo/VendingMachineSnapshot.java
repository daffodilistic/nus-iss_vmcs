package com.cooldrinkscompany.vmcs.pojo;

import java.util.ArrayList;

public class VendingMachineSnapshot {
    public ArrayList<Drink> drinks;
    public ArrayList<Coin> coins;
    public boolean isDoorLocked;    

    public VendingMachineSnapshot() {
        this.drinks = new ArrayList<Drink>();
        this.coins = new ArrayList<Coin>();
    }
}
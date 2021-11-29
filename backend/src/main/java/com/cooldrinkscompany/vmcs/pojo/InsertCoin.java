package com.cooldrinkscompany.vmcs.pojo;

public class InsertCoin {
    public String name;
    public String country;
    public int value;
    public int quantity;

    public InsertCoin(String name, String country, int value, int quantity) {
        this.name = name;
        this.country = country;
        this.value = value;
        this.quantity = quantity;
    }

    public Coin convertToCoin() {
        return new Coin(-1, this.name, this.quantity, this.value);
    }
}
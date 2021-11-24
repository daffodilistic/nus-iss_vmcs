package com.cooldrinkscompany.vmcs.pojo;
public final class Drink {
    private int id;
    private String name;
    private int quantity;
    private double price = 0.0d;
}

abstract class Product  
{  
    public abstract String productType { get; }  
    public abstract String name { get; set; }  
    public abstract int quantity { get; set; }
    private abstract double price = 0.0d;  
}
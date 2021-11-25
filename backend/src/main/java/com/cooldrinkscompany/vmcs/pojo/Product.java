package com.cooldrinkscompany.vmcs.pojo;

public abstract class Product {
    // Instance variables which are inherited by child classes
    protected int id;
    protected String productType;
    protected String name;
    protected int quantity = 0;
    protected double price = 0.0d;
}
package com.cooldrinkscompany.vmcs.pojo;

public final class Drink {
    private int id;
    private String name;
    private int quantity;
    private double price = 0.0d;

    public Drink() {
        // empty constructor required for JSON-B (JSR 367)
    }

    public Drink(int id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public final int getId() {
        return id;
    }

    // public final void setId(int id) {
    //     this.id = id;
    // }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final double getPrice() {
        return price;
    }

    public final void setPrice(double price) {
        this.price = price;
    }

    public final int getQuantity() {
        return quantity;
    }

    public final void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
package com.cooldrinkscompany.vmcs.pojo;

public final class Coin {
    private int id;
    private String name;
    private int quantity;
    private double denomination = 0.0d;

    public Coin() {
        // empty constructor required for JSON-B (JSR 367)
    }

    public Coin(int id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.denomination = price;
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
        return denomination;
    }

    public final void setPrice(double price) {
        this.denomination = price;
    }

    public final int getQuantity() {
        return quantity;
    }

    public final void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
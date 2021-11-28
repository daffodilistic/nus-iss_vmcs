package com.cooldrinkscompany.vmcs.pojo;

public final class Drink extends Product {
    
    public Drink() {
        // empty constructor required for JSON-B (JSR 367)
    }

    public Drink(int id, String name, int quantity, double price) {
        this.productType = this.getClass().getSimpleName();
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public final int getId() {
        return id;
    }

    // ID should not be changable by program code; let PostgreSQL handle it
    // public final void setId(int id) {
    //     this.id = id;
    // }

    public final String getName() {
        return "Drink" + name;
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
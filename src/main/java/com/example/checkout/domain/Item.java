package com.example.checkout.domain;

public class Item {

    private Long id;
    private double unitPrice;

    public Item() {
    }

    public Item(Long id, double unitPrice) {
        this.id = id;
        this.unitPrice = unitPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}

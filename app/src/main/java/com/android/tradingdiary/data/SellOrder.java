package com.android.tradingdiary.data;

public class SellOrder {
    public String id;
    public String name;
    public Double sellPrice;
    public int sellQty;

    public SellOrder(String id, String name, Double sellPrice, int sellQty) {
        this.id = id;
        this.name = name;
        this.sellPrice = sellPrice;
        this.sellQty = sellQty;
    }

    public SellOrder() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getSellQty() {
        return sellQty;
    }

    public void setSellQty(int sellQty) {
        this.sellQty = sellQty;
    }
}

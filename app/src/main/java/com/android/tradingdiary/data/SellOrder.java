package com.android.tradingdiary.data;

public class SellOrder {
    public String id;
    public String name;
    public Double sellPrice;
    public Double sellQty;

    public SellOrder(String id, String name, Double sellPrice, Double sellQty) {
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

    public Double getSellQty() {
        return sellQty;
    }

    public void setSellQty(Double sellQty) {
        this.sellQty = sellQty;
    }
}

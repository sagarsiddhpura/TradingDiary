package com.android.tradingdiary.data;

import java.util.ArrayList;

public class Order {
    public String id;
    public String name;
    public Double buyPricePerUnit;
    public int buyQty;
    public Double sellPricePerUnit;
    public ArrayList<SellOrder> sellOrders;

    public Order(String id, String name) {
        this.id = id;
        this.name = name;
        this.buyPricePerUnit = 0.0;
        this.buyQty = 0;
        this.sellOrders = new ArrayList<>();
        sellPricePerUnit = 0.0;
    }

    public Order() {
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

    public Double getBuyPricePerUnit() {
        return buyPricePerUnit;
    }

    public void setBuyPricePerUnit(Double buyPricePerUnit) {
        this.buyPricePerUnit = buyPricePerUnit;
    }

    public int getBuyQty() {
        return buyQty;
    }

    public void setBuyQty(int buyQty) {
        this.buyQty = buyQty;
    }

    public boolean isSellQuantityAllowed(int qty) {
        if(buyQty - (getSoldQty() + qty) < 0) {
            return false;
        } else {
            return true;
        }
    }

    private int getSoldQty() {
        int soldQty = 0;
        for (SellOrder sellOrder : sellOrders) {
            soldQty += sellOrder.sellQty;
        }
        return soldQty;
    }

    public int getRemainingSellQty() {
        return buyQty - getSoldQty();
    }

    public double getActualSaleTotal() {
        double sale = 0.0;
        for (SellOrder sellOrder : sellOrders) {
            sale += sellOrder.sellQty * sellOrder.sellPrice;
        }
        return sale;
    }

    public double getProfitLoss() {
        return getActualSaleTotal() - (buyQty * buyPricePerUnit);
    }
}

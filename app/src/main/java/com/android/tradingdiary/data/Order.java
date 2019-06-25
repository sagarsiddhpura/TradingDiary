package com.android.tradingdiary.data;

import com.android.tradingdiary.R;
import com.android.tradingdiary.utils.Utils;

import java.util.ArrayList;

public class Order {
    public String id;
    public String name;
    public Double buyPricePerUnit;
    public Double buyQty;
    public Double sellPricePerUnit;
    public ArrayList<SellOrder> sellOrders;
    public long creationDate;
    public String userId;
    public String unit;
    public double sellPercentage;
    public int color;

    public Order(String id, String name) {
        this.id = id;
        this.name = name;
        this.buyPricePerUnit = 0.0;
        this.buyQty = 0.0;
        this.sellOrders = new ArrayList<>();
        this.sellPricePerUnit = 0.0;
        this.sellPercentage = 0.0;
        this.creationDate = System.currentTimeMillis();
        this.unit = "";
        this.userId = Utils.formatId(id);
        this.color = R.color.teal_700;
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

    public Double getBuyQty() {
        return buyQty;
    }

    public void setBuyQty(Double buyQty) {
        this.buyQty = buyQty;
    }

    public boolean isSellQuantityAllowed(double qty) {
        if(buyQty - (getSoldQty() + qty) < 0) {
            return false;
        } else {
            return true;
        }
    }

    private double getSoldQty() {
        double soldQty = 0.0;
        for (SellOrder sellOrder : getSellOrders()) {
            soldQty += sellOrder.sellQty;
        }
        return soldQty;
    }

    private ArrayList<SellOrder> getSellOrders() {
        if(sellOrders == null) {
            sellOrders = new ArrayList<>();
        }
        return sellOrders;
    }

    public double getRemainingSellQty() {
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

    public boolean isComplete() {
        return Math.abs(buyQty - getSoldQty()) <= 0.001;
    }

    public double getRemainingSaleTotal() {
        if(sellPricePerUnit != 0.0) {
            return sellPricePerUnit * buyQty;
        } else if (sellPercentage != 0.0) {
            double buyTotal = buyQty * buyPricePerUnit;
            return buyTotal + buyTotal * (sellPercentage / 100);
        } else {
            return 0.0;
        }
    }

    public boolean isMatchingSearch(String s) {
        String searchString = name + ":::" + userId;
        return searchString.toLowerCase().contains(s.toLowerCase());
    }
}

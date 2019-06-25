package com.android.tradingdiary.data;

import com.android.tradingdiary.R;
import com.android.tradingdiary.utils.Utils;

import java.util.ArrayList;

public class Backup {
    public ArrayList<Order> orders;
    public ArrayList<Order> completedOrders;

    public Backup(ArrayList<Order> orders, ArrayList<Order> completedOrders) {
        this.orders = orders;
        this.completedOrders = completedOrders;
    }

    public Backup() {
    }
}

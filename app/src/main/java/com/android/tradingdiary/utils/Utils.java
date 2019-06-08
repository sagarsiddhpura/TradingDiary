package com.android.tradingdiary.utils;

import android.os.Build;
import android.view.View;
import android.view.Window;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.mainscreen.MainActivity;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Utils {
    public static void setupSystemUI(@NotNull MainActivity activity) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.setStatusBarColor(activity.getResources().getColor(R.color.teal_200));
        } else {
            window.setStatusBarColor(activity.getResources().getColor(R.color.white, null));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static ArrayList<Order> getOrders() {
        ArrayList<Order> orders = Paper.book().read("orders");
        if (orders == null) {
            orders = new ArrayList<>();
            Paper.book().write("orders", orders);
        }
        return orders;
    }

    public static void saveOrders(ArrayList<Order> orders) {
        Paper.book().write("orders", orders);
    }

    public static ArrayList<Order> getCompletedOrders() {
        ArrayList<Order> orders = Paper.book().read("completed_orders");
        if (orders == null) {
            orders = new ArrayList<>();
            Paper.book().write("completed_orders", orders);
        }
        return orders;
    }

    public static void saveCompletedOrders(ArrayList<Order> completedOrders) {
        Paper.book().write("completed_orders", completedOrders);
    }
}

package com.android.tradingdiary.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Utils {
    public static void setupSystemUI(@NotNull Activity activity) {
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

    private static void saveOrders(ArrayList<Order> orders) {
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

    private static void saveCompletedOrders(ArrayList<Order> completedOrders) {
        Paper.book().write("completed_orders", completedOrders);
    }

    public static void saveOrder(Order order) {
        ArrayList<Order> orders = getOrders();
        int index = 0;
        boolean isFound = false;
        Iterator<Order> iterator = orders.iterator();
        while(iterator.hasNext()) {
            Order currentOrder = iterator.next();
            index++;
            if(currentOrder.id.equals(order.id)) {
               iterator.remove();
                isFound = true;
               break;
            }
        }
        if(isFound) {
            orders.add(index - 1, order);
        } else {
            orders.add(0, order);
        }
        saveOrders(orders);
    }

    public static Order getOrder(String orderId) {
        ArrayList<Order> orders = getOrders();
        for (Order order : orders) {
            if(order.id.equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public static void setupActionBar(Activity activity, boolean isLightBar, int bgColorRes,
                                      int bgColorRes2, int titleRes, Toolbar toolbar) {
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.setStatusBarColor(activity.getResources().getColor(bgColorRes2));
            if (!isLightBar) {
                toolbar.setTitleTextColor(activity.getResources().getColor(R.color.white));
            }
        } else {
            window.setStatusBarColor(activity.getResources().getColor(bgColorRes, null));
            if (isLightBar) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                toolbar.setTitleTextColor(activity.getResources().getColor(R.color.white));
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }

        toolbar.setTitle(activity.getString(titleRes));
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    public static ArrayList<Order> deleteOrder(String id) {
        ArrayList<Order> orders = getOrders();
        Iterator<Order> iterator = orders.iterator();
        while(iterator.hasNext()) {
            Order currentOrder = iterator.next();
            if(currentOrder.id.equals(id)) {
                iterator.remove();
                break;
            }
        }
        saveOrders(orders);
        return orders;
    }

    public static void hideKeyBoard(Activity activity) {
        if ((activity.getCurrentFocus() != null) && (activity.getCurrentFocus().getWindowToken() != null)) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void saveCompletedOrder(Order order) {
        if(order != null) {
            ArrayList<Order> orders = getCompletedOrders();
            Iterator<Order> iterator = orders.iterator();
            while(iterator.hasNext()) {
                Order currentOrder = iterator.next();
                if(currentOrder != null && currentOrder.id.equals(order.id)) {
                    iterator.remove();
                    break;
                }
            }
            orders.add(order);
            saveCompletedOrders(orders);
        }
    }

    public static void deleteCompletedOrder(String id) {
        ArrayList<Order> orders = getCompletedOrders();
        Iterator<Order> iterator = orders.iterator();
        while(iterator.hasNext()) {
            Order currentOrder = iterator.next();
            if(currentOrder.id.equals(id)) {
                iterator.remove();
                break;
            }
        }
        saveCompletedOrders(orders);
    }

    public static Order getCompletedOrder(String orderId) {
        ArrayList<Order> orders = getCompletedOrders();
        for (Order order : orders) {
            if(order.id.equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public static int getCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("COUNT", 100);
    }

    public static void incrementCount(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt("COUNT", getCount(context)+1)
                .apply();
    }

    public static String formatId(String id) {
        if(id != null) {
            String date = new SimpleDateFormat("yyyyMMddss").format(new Date());
            return date + "-" + id.substring(id.length() - 6);
        }
        return id;
    }

    public static void deleteAllOrders() {
        ArrayList<Object> orders = new ArrayList<>();
        Paper.book().write("orders", orders);
    }

    public static void deleteAllCompletedOrders() {
        ArrayList<Object> orders = new ArrayList<>();
        Paper.book().write("completed_orders", orders);
    }
}

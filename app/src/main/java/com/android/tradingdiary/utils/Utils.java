package com.android.tradingdiary.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.edit.EditOrderJavaActivity;
import io.paperdb.Paper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

    public static void saveOrder(Order order) {
        ArrayList<Order> orders = getOrders();
        Iterator<Order> iterator = orders.iterator();
        while(iterator.hasNext()) {
            Order currentOrder = iterator.next();
            if(currentOrder.id.equals(order.id)) {
               iterator.remove();
               break;
            }
        }
        orders.add(order);
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

    public static void setupActionBar(EditOrderJavaActivity activity, boolean isLightBar, int bgColorRes,
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

    public static void deleteEvent(String id) {
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
    }

    public static void hideKeyBoard(Activity activity) {
        if ((activity.getCurrentFocus() != null) && (activity.getCurrentFocus().getWindowToken() != null)) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showAlert(String title, String message) {
//        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle(title);
//        alert.setMessage(message);
//        alert.setPositiveButton("Ok", null);
//        alert.show();
    }

}

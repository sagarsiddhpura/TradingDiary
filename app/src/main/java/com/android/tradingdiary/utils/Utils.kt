package com.android.tradingdiary.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import android.view.View
import com.android.tradingdiary.R
import com.android.tradingdiary.data.Order
import io.paperdb.Paper
import java.util.ArrayList

import java.util.HashSet

object Utils2 {
    fun setupSystemUI(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            activity.window.statusBarColor = activity.resources.getColor(R.color.teal_200)
        } else {
            activity.window.statusBarColor = activity.resources.getColor(R.color.white, null)
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun getOrders(): ArrayList<Order> {
        var orders = Paper.book().read<ArrayList<Order>>("orders")
        if (orders == null) {
            orders = ArrayList()
            Paper.book().write<List<Order>>("orders", orders)
        }
        return orders
    }

    fun saveOrders(orders: ArrayList<Order>) {
        Paper.book().write<ArrayList<Order>>("orders", orders)
    }

    fun saveCompletedOrders(completedEvents: ArrayList<Order>) {
        Paper.book().write<List<Order>>("completed_events", completedEvents)
    }

    fun getCompletedOrders(): ArrayList<Order> {
        var completedOrders: ArrayList<Order>? = Paper.book().read<ArrayList<Order>>("completed_orders")
        if (completedOrders == null) {
            completedOrders = ArrayList()
            Paper.book().write<List<Order>>("completed_orders", completedOrders)
        }
        return completedOrders
    }
}


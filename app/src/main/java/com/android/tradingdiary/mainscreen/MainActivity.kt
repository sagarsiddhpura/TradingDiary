package com.android.tradingdiary.mainscreen

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.tradingdiary.R
import com.android.tradingdiary.data.Order
import com.android.tradingdiary.edit.EditOrderActivity
import com.android.tradingdiary.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mOrderAdapter: OrderAdapter? = null
    private var orders: ArrayList<Order>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.tasks_toolbar)
        setSupportActionBar(toolbar)
        Utils.setupSystemUI(this)

        setupEventList()

        fab_add.setOnClickListener {
            val intent = Intent(this@MainActivity, EditOrderActivity::class.java)
                intent.putExtra("IS_NEW", true)
                startActivity(intent)
        }
    }

    private fun setupEventList() {
        val recyclerView = findViewById<RecyclerView>(R.id.list_events)
        mOrderAdapter = OrderAdapter(this)
        recyclerView.adapter = mOrderAdapter
        val touchHelper = ItemTouchHelper(ItemTouchHelperCallback(mOrderAdapter))
        touchHelper.attachToRecyclerView(recyclerView)
        val animationController = AnimationUtils.loadLayoutAnimation(this,
            R.anim.layout_fall_down
        )
        recyclerView.layoutAnimation = animationController

        orders = Utils.getOrders()
        refreshList(orders!!)

        mOrderAdapter!!.setItemActionListener(object : ItemActionListener {
            override fun onItemSwiped(eventId: String) {
                deleteOrder(eventId)
                refreshList(orders!!)
            }

            override fun onItemClicked(eventId: String) {
//                val intent = Intent(this@MainActivity, EventDetailActivity::class.java)
//                intent.putExtra("EVENT_ID", eventId)
//                startActivity(intent)
            }
        })
    }

    private fun refreshList(events: List<Order>) {
        mOrderAdapter?.setEntities(events)
    }

    private fun deleteOrder(id: String) {
        var hasChanged = false
        val iterator = orders!!.iterator()
        while (iterator.hasNext()) {
            val order = iterator.next()
            if (order.id == id) {
                addToCompletedOrders(order)
                iterator.remove()
                hasChanged = true
            }
        }
        if (hasChanged) {
            Utils.saveOrders(orders!!)
        }
    }

    private fun addToCompletedOrders(order: Order) {
        var completedOrders = Utils.getCompletedOrders()
        completedOrders.add(order)
        Utils.saveCompletedOrders(completedOrders)
    }

    private fun showCompletedEvents() {
//        val intent = Intent(this@MainActivity, CompletedEventsActivity::class.java)
//        startActivity(intent)
    }
}

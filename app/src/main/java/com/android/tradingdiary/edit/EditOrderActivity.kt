package com.android.tradingdiary.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.tradingdiary.R
import com.android.tradingdiary.data.Order
import com.android.tradingdiary.utils.ConfirmationDialog
import com.android.tradingdiary.utils.Utils
import kotlinx.android.synthetic.main.activity_edit_order.*

class EditOrderActivity : AppCompatActivity() {

    private var isNew: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_order)
        var orderId: String

        intent.apply {
            isNew = getBooleanExtra("IS_NEW", false)
            orderId = getStringExtra("ORDER_ID")
        }

        if(isNew) {
            val order = Order()
            order.setId(System.currentTimeMillis().toString())
            loadOrder(order)
        } else {
            val orders = Utils.getOrders()
            loadOrder(orders.find { it.id == orderId }!!)
        }

        fab_confirm.setOnClickListener {
            validateAndSaveEntity()
        }
    }

    private fun validateAndSaveEntity() {
        if(title_edit.text == null || title_edit.text.toString() == "") {
            ConfirmationDialog(this, "Please enter name", R.string.yes, R.string.ok, 0) { }
            return
        }
    }

    private fun loadOrder(order: Order) {
        title_edit.setText(order.name)
        qty_edit.setText(order.buyQty)
        price_per_unit_edit.setText(order.buyPrice.toString())
    }
}

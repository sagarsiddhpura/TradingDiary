package com.android.tradingdiary.completedorders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.data.SellOrder;
import com.android.tradingdiary.edit.EditOrderActivity;
import com.android.tradingdiary.edit.SellOrderAdapter;
import com.android.tradingdiary.mainscreen.ItemActionListener;
import com.android.tradingdiary.utils.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class EditCompletedOrderActivity extends AppCompatActivity {

    private EditText name = null;
    private EditText buyQty;
    private EditText buyPrice;
    private EditText sellingPricePerUnit;
    private EditText sellingPriceTotal;
    private EditText estimatedProfitLoss;
    private Order order;
    private SellOrderAdapter adapter;
    private TextInputLayout estimatedProfitLossHint;
    private TextInputLayout actualProfitLossHint;
    private EditText actualProfitLoss;
    private EditText estimatedSellTotal;
    private EditText remarks;
    private CollapsingToolbarLayout appbar;
    private EditText buyTotal;
    private EditText buyUnit;
    private EditText estimatedSellPercentage;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);
        toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        Utils.setupActionBar(this, false, R.color.teal_700, R.color.teal_700, R.string.edit, toolbar);

        Intent intent = getIntent();
        final String orderId = intent.getStringExtra("ORDER_ID");

        appbar = findViewById(R.id.appbar);
        name = findViewById(R.id.title_edit);
        buyQty = findViewById(R.id.buy_qty);
        buyPrice = findViewById(R.id.buy_price_per_unit);
        buyUnit = findViewById(R.id.buy_unit);
        sellingPricePerUnit = findViewById(R.id.estimated_sell_price_per_unit);
        sellingPriceTotal = findViewById(R.id.estimated_total_selling_price);
        estimatedProfitLoss = findViewById(R.id.estimated_profit_loss);
        estimatedProfitLossHint = findViewById(R.id.profit_loss_hint);
        estimatedSellPercentage = findViewById(R.id.estimated_sell_percentage);
        EditText actualSaleTotal = findViewById(R.id.actual_total_selling_price);
        actualProfitLoss = findViewById(R.id.actual_profit_loss);
        actualProfitLossHint = findViewById(R.id.actual_profit_loss_hint);
        EditText buyUnit = findViewById(R.id.buy_unit);
        EditText estimatedSellPercentage = findViewById(R.id.estimated_sell_percentage);
        remarks = findViewById(R.id.remarks_edit);
        buyTotal = findViewById(R.id.buy_total);
        estimatedSellTotal = findViewById(R.id.estimated_total_selling_price);

        order = Utils.getCompletedOrder(orderId);
        loadOrder(order);
        Utils.hideKeyBoard(this);

        FloatingActionButton fab = findViewById(R.id.fab_confirm);
        fab.hide();
        name.setEnabled(false);
        buyQty.setEnabled(false);
        buyPrice.setEnabled(false);
        remarks.setEnabled(false);
        sellingPricePerUnit.setEnabled(false);
        actualSaleTotal.setText(String.valueOf(order.getActualSaleTotal()));
        buyUnit.setEnabled(false);
        estimatedSellPercentage.setEnabled(false);
        setupSellOrdersList(order);
    }

    private void loadOrder(final Order order) {
        name.setText(order.name);
        buyQty.setText(String.valueOf(order.buyQty));
        buyPrice.setText(String.valueOf(order.buyPricePerUnit));
        sellingPricePerUnit.setText(String.valueOf(order.sellPricePerUnit));
        sellingPriceTotal.setText(String.valueOf(order.buyQty * order.sellPricePerUnit));
        estimatedSellPercentage.setText(String.valueOf(order.sellPercentage));
        remarks.setText(order.remarks);
        buyUnit.setText(String.valueOf(order.unit));
        setProfitLossData();
        setBuyTotal();
        setSellTotal();
        // restore color
        int color = order.color;
        appbar.setBackgroundColor(getResources().getColor(color));
        appbar.setContentScrimColor(getResources().getColor(color));
        appbar.setStatusBarScrimColor(getResources().getColor(color));
        Utils.setupActionBar(EditCompletedOrderActivity.this, false, color, color, R.string.edit, toolbar);
    }

    private void setSellTotal() {
        estimatedSellTotal.setText(Utils.formatDouble(getSellTotal()));
    }

    private double getSellTotal() {
        if(getDouble(sellingPricePerUnit) != 0.0) {
            return getDouble(sellingPricePerUnit) * getDouble(buyQty);
        } else if (getDouble(estimatedSellPercentage) != 0.0) {
            double buyTotal = getDouble(buyQty) * getDouble(buyPrice);
            return buyTotal + buyTotal * (getDouble(estimatedSellPercentage) / 100);
        } else {
            return 0.0;
        }
    }

    private void setBuyTotal() {
        buyTotal.setText(Utils.formatDouble(getbuyTotal()));
    }

    private double getbuyTotal() {
        return getDouble(buyQty) * getDouble(buyPrice);
    }

    private void setProfitLossData() {
        double estimatedProfitLossValue = (getDouble(buyQty) * getDouble(sellingPricePerUnit)) - (getDouble(buyQty) * getDouble(buyPrice));
        if(estimatedProfitLossValue > 0) {
            estimatedProfitLoss.setText(String.valueOf(estimatedProfitLossValue));
            estimatedProfitLossHint.setHint("Est. Profit");
        } else {
            estimatedProfitLoss.setText(String.valueOf(estimatedProfitLossValue * -1));
            estimatedProfitLossHint.setHint("Est. Loss");
        }
        double actualProfitLossValue = order.getProfitLoss();
        if(actualProfitLossValue > 0) {
            actualProfitLoss.setText(String.valueOf(actualProfitLossValue));
            actualProfitLossHint.setHint("Actual Profit");
        } else {
            actualProfitLoss.setText(String.valueOf(actualProfitLossValue * -1));
            actualProfitLossHint.setHint("Actual Loss");
        }
    }

    private double getDouble(EditText sellingPricePerUnit) {
        try {
            return Double.parseDouble(sellingPricePerUnit.getText().toString());
        }catch (Exception e) {
            return 0.0;
        }
    }

    private void setupSellOrdersList(final Order order) {
        RecyclerView recyclerView = findViewById(R.id.orders_rv);
        adapter = new SellOrderAdapter(this);
        recyclerView.setAdapter(adapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(animationController);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        ArrayList<SellOrder> orders = order.sellOrders;
        if(orders == null) {
            orders = new ArrayList<>();
        }
        refreshList(orders);

        adapter.setItemActionListener(new ItemActionListener() {
            @Override
            public void onItemSwiped(String id) {
                Toast.makeText(getApplicationContext(),"Cannot edit deleted order. Please restore it to edit",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemClicked(String id) {
            }
        });
    }

    private void refreshList(ArrayList<SellOrder> sellOrders) {
        adapter.setEntities(sellOrders);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        menu.findItem(R.id.action_change_color).setVisible(false);
        menu.findItem(R.id.action_restore_order).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_del:
                deleteOrder();
                break;
            case R.id.action_done:
                finish();
                break;
            case R.id.action_restore_order:
                restoreOrder();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void restoreOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCompletedOrderActivity.this);
        builder.setMessage("Are you sure you want to Restore this Order?")
                .setTitle("Restore Order");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemId) {
                Utils.saveOrder(order);
                Utils.deleteCompletedOrder(order.id);
                Toast.makeText(getApplicationContext(),"Order restored...",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCompletedOrderActivity.this);
        builder.setMessage("Are you sure you want to delete this Order?")
                .setTitle("Delete Order");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemId) {
                Utils.deleteCompletedOrder(order.id);
                Toast.makeText(getApplicationContext(),"Order deleted...",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

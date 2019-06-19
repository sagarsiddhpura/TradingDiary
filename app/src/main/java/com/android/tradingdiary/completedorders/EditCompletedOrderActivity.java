package com.android.tradingdiary.completedorders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.data.SellOrder;
import com.android.tradingdiary.edit.SellOrderAdapter;
import com.android.tradingdiary.mainscreen.ItemActionListener;
import com.android.tradingdiary.mainscreen.ItemTouchHelperCallback;
import com.android.tradingdiary.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Iterator;

public class EditCompletedOrderActivity extends AppCompatActivity {

    private EditText name = null;
    private EditText buyQty;
    private EditText buyPrice;
    private EditText sellingPricePerUnit;
    private EditText sellingPriceTotal;
    private EditText estimatedProfitLoss;
    private Order order;
    private SellOrderAdapter adapter;
    private ArrayList<SellOrder> orders;
    private TextInputLayout estimatedProfitLossHint;
    private EditText actualSaleTotal;
    private TextInputLayout actualProfitLossHint;
    private EditText actualProfitLoss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        setSupportActionBar(toolbar);
        Utils.setupActionBar(this, false, R.color.teal_700, R.color.teal_700, R.string.edit, toolbar);

        Intent intent = getIntent();
        final String orderId = intent.getStringExtra("ORDER_ID");
        boolean isNew = intent.getBooleanExtra("IS_NEW", false);

        name = findViewById(R.id.title_edit);
        buyQty = findViewById(R.id.buy_qty);
        buyPrice = findViewById(R.id.buy_price_per_unit);
        sellingPricePerUnit = findViewById(R.id.estimated_sell_price_per_unit);
        sellingPriceTotal = findViewById(R.id.estimated_total_selling_price);
        estimatedProfitLoss = findViewById(R.id.estimated_profit_loss);
        estimatedProfitLossHint = findViewById(R.id.profit_loss_hint);
        actualSaleTotal = findViewById(R.id.actual_total_selling_price);
        actualProfitLoss = findViewById(R.id.actual_profit_loss);
        actualProfitLossHint = findViewById(R.id.actual_profit_loss_hint);

        if(isNew) {
            order = new Order(String.valueOf(System.currentTimeMillis()), "");
        } else {
            order = Utils.getCompletedOrder(orderId);
            loadOrder(order);
        }

        Utils.hideKeyBoard(this);

        FloatingActionButton fab = findViewById(R.id.fab_confirm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(EditCompletedOrderActivity.this, R.style.MyDialogTheme);
                alert.setTitle("Add Order");
                LayoutInflater inflater = LayoutInflater.from(EditCompletedOrderActivity.this);
                final View dialogView = inflater.inflate(R.layout.dialog_add_sell_order, null);

                final EditText sellPrice = dialogView.findViewById(R.id.sell_order_price_per_unit);
                sellPrice.setText(String.valueOf(getDouble(sellingPricePerUnit)));
                final EditText sellQty = dialogView.findViewById(R.id.sell_order_quantity);
                sellQty.setText(String.valueOf(order.getRemainingSellQty()));

                alert.setView(dialogView);
                alert.setPositiveButton("Ok", null);
                alert.setNegativeButton("Cancel", null);
                final AlertDialog dialog = alert.show();

                //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(sellPrice.getText() == null || sellPrice.getText().toString().equals("") || sellQty.getText() == null || sellQty.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(),"Please Enter valid Quantity and Price per unit",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if(!order.isSellQuantityAllowed(getDouble(sellQty))) {
                            Toast.makeText(getApplicationContext(),"Sell Quantity cannot be more than items remaining",Toast.LENGTH_LONG).show();
                            return;
                        }
                        order.sellOrders.add(new SellOrder(String.valueOf(System.currentTimeMillis()), "", getDouble(sellPrice),
                                getDouble(sellQty)));
                        dialog.dismiss();
                        refreshList(order.sellOrders);
                        setProfitLossData();
                        actualSaleTotal.setText(String.valueOf(order.getActualSaleTotal()));
                    }
                });
            }
        });

        buyQty.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    sellingPriceTotal.setText(String.valueOf(getDouble(sellingPricePerUnit) * getDouble(buyQty)));
                    setProfitLossData();
                }catch (Exception e) {
                    sellingPriceTotal.setText("0.0");
                    estimatedProfitLoss.setText("0.0");
                    estimatedProfitLossHint.setHint("Profit");
                }
            }
        });

        sellingPricePerUnit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    sellingPriceTotal.setText(String.valueOf(getDouble(sellingPricePerUnit) * getDouble(buyQty)));
                    setProfitLossData();
                } catch (Exception e) {
                    sellingPriceTotal.setText("0.0");
                    estimatedProfitLoss.setText("0.0");
                    estimatedProfitLossHint.setHint("Profit");
                }
            }
        });

        buyPrice.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    setProfitLossData();
                } catch (Exception e) {
                    estimatedProfitLoss.setText("0.0");
                    estimatedProfitLossHint.setHint("Profit");
                }
            }
        });

        setupSellOrdersList(order);
        actualSaleTotal.setText(String.valueOf(order.getActualSaleTotal()));
    }

    private void loadOrder(final Order order) {
        name.setText(order.name);
        buyQty.setText(String.valueOf(order.buyQty));
        buyPrice.setText(String.valueOf(order.buyPricePerUnit));
        sellingPricePerUnit.setText(String.valueOf(order.sellPricePerUnit));
        sellingPriceTotal.setText(String.valueOf(order.buyQty * order.sellPricePerUnit));
        setProfitLossData();
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
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        touchHelper.attachToRecyclerView(recyclerView);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(animationController);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        orders = order.sellOrders;
        if(orders == null) {
            orders = new ArrayList<>();
        }
        refreshList(orders);

        adapter.setItemActionListener(new ItemActionListener() {
            @Override
            public void onItemSwiped(String id) {
                deleteSellOrder(id);
                refreshList(orders);
                setProfitLossData();
                actualSaleTotal.setText(String.valueOf(order.getActualSaleTotal()));
            }

            @Override
            public void onItemClicked(String id) {
            }
        });
    }

    private void deleteSellOrder(String id) {
        Iterator<SellOrder> iterator = order.sellOrders.iterator();
        while (iterator.hasNext()) {
            SellOrder item = iterator.next();
            if(item.getId().equals(id)) {
                iterator.remove();
            }
        }
    }

    private void refreshList(ArrayList<SellOrder> sellOrders) {
        adapter.setEntities(sellOrders);
    }

    private void validateAndSaveEntity() {
        if(name.getText() == null || name.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"Please enter name",Toast.LENGTH_LONG).show();
            return;
        }
        if(buyQty.getText() == null || buyQty.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"Please enter Buy Quantity",Toast.LENGTH_LONG).show();
            return;
        }
        if(buyPrice.getText() == null || buyPrice.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"Please enter Buy Price per Unit",Toast.LENGTH_LONG).show();
            return;
        }

        order.name = name.getText().toString();
        order.buyQty = getDouble(buyQty);
        order.buyPricePerUnit = getDouble(buyPrice);
        order.sellPricePerUnit = getDouble(sellingPricePerUnit);

        Utils.saveCompletedOrder(order);
        Toast.makeText(getApplicationContext(),"Order saved...", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit, menu);
        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("IS_NEW", false);
        if(isNew) {
            menu.findItem(R.id.action_del).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_del:
                deleteOrder();
                break;
            case R.id.action_done:
                validateAndSaveEntity();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void deleteOrder() {
        Utils.deleteCompletedOrder(order.id);
        Toast.makeText(getApplicationContext(),"Order deleted...",Toast.LENGTH_LONG).show();
        finish();
    }
}

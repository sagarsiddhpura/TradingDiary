package com.android.tradingdiary.edit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.BuildConfig;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.data.SellOrder;
import com.android.tradingdiary.mainscreen.ItemActionListener;
import com.android.tradingdiary.mainscreen.ItemTouchHelperCallback;
import com.android.tradingdiary.mainscreen.MainActivity;
import com.android.tradingdiary.utils.DateTimeUtils;
import com.android.tradingdiary.utils.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import petrov.kristiyan.colorpicker.ColorPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class EditOrderActivity extends AppCompatActivity {

    private EditText name;
    private EditText buyQty;
    private EditText buyPrice;
    private EditText estimatedSellPricePerUnit;
    private EditText estimatedSellTotal;
    private EditText estimatedProfitLoss;
    private Order order;
    private SellOrderAdapter adapter;
    private ArrayList<SellOrder> orders;
    private TextInputLayout estimatedProfitLossHint;
    private EditText actualSaleTotal;
    private TextInputLayout actualProfitLossHint;
    private EditText actualProfitLoss;
    private EditText estimatedSellPercentage;
    private EditText buyTotal;
    private EditText buyUnit;
    private EditText remarks;
    private CollapsingToolbarLayout appbar;
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
        boolean isNew = intent.getBooleanExtra("IS_NEW", false);

        appbar = findViewById(R.id.appbar);
        name = findViewById(R.id.title_edit);
        buyQty = findViewById(R.id.buy_qty);
        buyPrice = findViewById(R.id.buy_price_per_unit);
        buyTotal = findViewById(R.id.buy_total);
        buyUnit = findViewById(R.id.buy_unit);
        estimatedSellPricePerUnit = findViewById(R.id.estimated_sell_price_per_unit);
        estimatedSellPercentage = findViewById(R.id.estimated_sell_percentage);
        estimatedSellTotal = findViewById(R.id.estimated_total_selling_price);
        estimatedProfitLoss = findViewById(R.id.estimated_profit_loss);
        estimatedProfitLossHint = findViewById(R.id.profit_loss_hint);
        actualSaleTotal = findViewById(R.id.actual_total_selling_price);
        actualProfitLoss = findViewById(R.id.actual_profit_loss);
        actualProfitLossHint = findViewById(R.id.actual_profit_loss_hint);
        remarks = findViewById(R.id.remarks_edit);

        if(isNew) {
            if(BuildConfig.DEBUG) {
                // 30-may
//                Calendar todayStart = Calendar.getInstance();
//                todayStart.set(Calendar.HOUR_OF_DAY, 4);
//                todayStart.set(Calendar.MINUTE, 5);
//                todayStart.set(Calendar.SECOND, 5);
//                todayStart.set(Calendar.DAY_OF_MONTH, -1);

                // 18-June
                Calendar todayStart = Calendar.getInstance();
                todayStart.set(Calendar.HOUR_OF_DAY, 4);
                todayStart.set(Calendar.MINUTE, 5);
                todayStart.set(Calendar.SECOND, 5);
                todayStart.set(Calendar.DAY_OF_MONTH, 11);
                String name1 = DateTimeUtils.longToString(todayStart.getTimeInMillis(), DateTimeUtils.DATE)
                        + " " + DateTimeUtils.longToString(todayStart.getTimeInMillis(), DateTimeUtils.TIME);
                order = new Order(String.valueOf(todayStart.getTimeInMillis()), name1);
                name.setText(name1);
                order.creationDate = todayStart.getTimeInMillis();
            } else {
                order = new Order(String.valueOf(System.currentTimeMillis()), "");
            }
        } else {
            order = Utils.getOrder(orderId);
            if(order == null) {
                Toast.makeText(getApplicationContext(),"Error loading Order.",Toast.LENGTH_LONG).show();
                finish();
                return;
            } else {
                loadOrder(order);
            }
        }

        Utils.hideKeyBoard(this);

        FloatingActionButton fab = findViewById(R.id.fab_confirm);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(EditOrderActivity.this, R.style.MyDialogTheme);
                alert.setTitle("Add Order");
                LayoutInflater inflater = LayoutInflater.from(EditOrderActivity.this);
                final View dialogView = inflater.inflate(R.layout.dialog_add_sell_order, null);

                final EditText sellPrice = dialogView.findViewById(R.id.sell_order_price_per_unit);
                sellPrice.setText(Utils.formatDouble(getSellPricePerUnit()));
                final EditText sellQty = dialogView.findViewById(R.id.sell_order_quantity);
                sellQty.setText(Utils.formatDouble(order.getRemainingSellQty()));

                alert.setView(dialogView);
                alert.setPositiveButton("Ok", null);
                alert.setNegativeButton("Cancel", null);
                final AlertDialog dialog = alert.show();

                //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getDouble(sellPrice) == 0.0 || getDouble(sellQty) == 0.0) {
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
                    setBuyTotal();
                    setSellTotal();
                    setProfitLossData();
                }catch (Exception e) {
                    estimatedSellTotal.setText("0.0");
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
                    setBuyTotal();
                    setSellTotal();
                    setProfitLossData();
                } catch (Exception e) {
                    estimatedProfitLoss.setText("0.0");
                    estimatedProfitLossHint.setHint("Profit");
                }
            }
        });

        estimatedSellPricePerUnit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(getDouble(estimatedSellPricePerUnit) != 0.0) {
                        estimatedSellPercentage.setText("");
                        setSellTotal();
                        setProfitLossData();
                    }
                } catch (Exception e) {
                    estimatedSellTotal.setText("0.0");
                    estimatedProfitLoss.setText("0.0");
                    estimatedProfitLossHint.setHint("Profit");
                    estimatedSellPercentage.setText("");
                }
            }
        });

        estimatedSellPercentage.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(getDouble(estimatedSellPercentage) != 0.0) {
                        estimatedSellPricePerUnit.setText("");
                        setSellTotal();
                        setProfitLossData();
                    }
                } catch (Exception e) {
                    estimatedSellTotal.setText("0.0");
                    estimatedProfitLoss.setText("0.0");
                    estimatedProfitLossHint.setHint("Profit");
                    estimatedSellPricePerUnit.setText("0.0");
                }
            }
        });

        setupSellOrdersList(order);
        actualSaleTotal.setText(String.valueOf(order.getActualSaleTotal()));
    }

    private void setBuyTotal() {
        buyTotal.setText(Utils.formatDouble(getbuyTotal()));
    }

    private double getbuyTotal() {
        return getDouble(buyQty) * getDouble(buyPrice);
    }

    private double getSellPricePerUnit() {
        if(getDouble(buyQty) == 0.0) {
            return 0.0;
        }
        return getSellTotal() / getDouble(buyQty);
    }

    private void setSellTotal() {
        estimatedSellTotal.setText(Utils.formatDouble(getSellTotal()));
    }

    private double getSellTotal() {
        if(getDouble(estimatedSellPricePerUnit) != 0.0) {
            return getDouble(estimatedSellPricePerUnit) * getDouble(buyQty);
        } else if (getDouble(estimatedSellPercentage) != 0.0) {
            double buyTotal = getDouble(buyQty) * getDouble(buyPrice);
            return buyTotal + buyTotal * (getDouble(estimatedSellPercentage) / 100);
        } else {
            return 0.0;
        }
    }

    private void loadOrder(final Order order) {
        name.setText(order.name);
        buyQty.setText(String.valueOf(order.buyQty));
        buyUnit.setText(String.valueOf(order.unit));
        buyPrice.setText(String.valueOf(order.buyPricePerUnit));
        estimatedSellPercentage.setText(String.valueOf(order.sellPercentage));
        estimatedSellPricePerUnit.setText(String.valueOf(order.sellPricePerUnit));
        estimatedSellTotal.setText(String.valueOf(order.buyQty * order.sellPricePerUnit));
        remarks.setText(order.remarks);
        setProfitLossData();
        setBuyTotal();
        setSellTotal();
        // restore color
        int color = order.color;
        appbar.setBackgroundColor(getResources().getColor(color));
        appbar.setContentScrimColor(getResources().getColor(color));
        appbar.setStatusBarScrimColor(getResources().getColor(color));
        Utils.setupActionBar(EditOrderActivity.this, false, color, color, R.string.edit, toolbar);
    }

    private void setProfitLossData() {
        double estimatedProfitLossValue = (getSellTotal()) - (getDouble(buyQty) * getDouble(buyPrice));
        if(estimatedProfitLossValue >= 0) {
            estimatedProfitLoss.setText(Utils.formatDouble(estimatedProfitLossValue));
            estimatedProfitLossHint.setHint("Est. Profit");
        } else {
            estimatedProfitLoss.setText(Utils.formatDouble(estimatedProfitLossValue * -1));
            estimatedProfitLossHint.setHint("Est. Loss");
        }
        double actualProfitLossValue = order.getProfitLoss();
        if(actualProfitLossValue >= 0) {
            actualProfitLoss.setText(Utils.formatDouble(actualProfitLossValue));
            actualProfitLossHint.setHint("Actual Profit");
        } else {
            actualProfitLoss.setText(Utils.formatDouble(actualProfitLossValue * -1));
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
        order.unit = buyUnit.getText().toString();
        order.buyPricePerUnit = getDouble(buyPrice);
        order.sellPricePerUnit = getDouble(estimatedSellPricePerUnit);
        order.sellPercentage = getDouble(estimatedSellPercentage);
        order.remarks = remarks.getText().toString();

        Utils.saveOrder(order);
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
            case R.id.action_change_color:
                changeColor();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void changeColor() {
        final ArrayList<Integer> colors = new ArrayList<>();
        colors.add(R.color.color1);
        colors.add(R.color.color2);
        colors.add(R.color.color3);
        colors.add(R.color.color4);
        colors.add(R.color.color5);
        colors.add(R.color.color6);
        colors.add(R.color.color7);
        colors.add(R.color.color8);
        colors.add(R.color.color10);
        colors.add(R.color.color11);
        colors.add(R.color.color12);
        colors.add(R.color.color13);
        colors.add(R.color.color14);
        colors.add(R.color.color15);
        colors.add(R.color.color16);

        ArrayList<Integer> colorsResolved = new ArrayList<>();
        for (Integer color : colors) {
            colorsResolved.add(getResources().getColor(color));
        }
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setRoundColorButton(true);
        colorPicker.setColors(colorsResolved.get(0), colorsResolved.get(1), colorsResolved.get(2), colorsResolved.get(3), colorsResolved.get(4),
                colorsResolved.get(5), colorsResolved.get(6), colorsResolved.get(7), colorsResolved.get(8), colorsResolved.get(9), colorsResolved.get(10),
                colorsResolved.get(11), colorsResolved.get(12), colorsResolved.get(13), colorsResolved.get(14));
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position,int color) {
                Integer colorSelected = colors.get(position);
                order.color = colorSelected;
                appbar.setBackgroundColor(getResources().getColor(colorSelected));
                appbar.setContentScrimColor(getResources().getColor(colorSelected));
                appbar.setStatusBarScrimColor(getResources().getColor(colorSelected));
                Utils.setupActionBar(EditOrderActivity.this, false, colorSelected, colorSelected, R.string.edit, toolbar);
            }

            @Override
            public void onCancel(){}
        });
    }

    private void deleteOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditOrderActivity.this);
        builder.setMessage("Are you sure you want to delete this Order?")
                .setTitle("Delete Order");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemId) {
                Utils.saveCompletedOrder(order);
                Utils.deleteOrder(order.id);
                Toast.makeText(getApplicationContext(),"Order deleted...",Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setNegativeButton("CANCEL",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

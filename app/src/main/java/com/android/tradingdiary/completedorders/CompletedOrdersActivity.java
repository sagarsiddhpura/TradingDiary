package com.android.tradingdiary.completedorders;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.mainscreen.ItemActionListener;
import com.android.tradingdiary.mainscreen.ItemTouchHelperCallback;
import com.android.tradingdiary.mainscreen.MainActivity;
import com.android.tradingdiary.mainscreen.OrderAdapter;
import com.android.tradingdiary.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CompletedOrdersActivity extends AppCompatActivity {

    private OrderAdapter adapter;
    private ArrayList<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tasks_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Completed Orders");
        Utils.setupSystemUI(this);

        setupList();

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.hide();
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.list_orders);
        adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(animationController);

        orders = Utils.getCompletedOrders();
        refreshList(orders);

        adapter.setItemActionListener(new ItemActionListener() {
            @Override
            public void onItemSwiped(final String id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CompletedOrdersActivity.this);
                builder.setMessage("Are you sure you want to delete this Order?")
                        .setTitle("Delete Order");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int itemId) {
                        Utils.deleteCompletedOrder(id);
                        orders = Utils.getCompletedOrders();
                        refreshList(orders);
                    }
                });
                builder.setNegativeButton("CANCEL",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onItemClicked(String id) {
                Intent intent = new Intent(CompletedOrdersActivity.this, EditCompletedOrderActivity.class);
                intent.putExtra("ORDER_ID", id);
                startActivity(intent);
            }
        });
    }

    private void refreshList(ArrayList<Order> orders) {
        adapter.setEntities(orders);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orders = Utils.getCompletedOrders();
        refreshList(orders);
    }
}

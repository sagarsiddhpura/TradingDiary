package com.android.tradingdiary.completedorders;

import android.content.Intent;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.mainscreen.ItemActionListener;
import com.android.tradingdiary.mainscreen.ItemTouchHelperCallback;
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
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        touchHelper.attachToRecyclerView(recyclerView);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(animationController);

        orders = Utils.getCompletedOrders();
        refreshList(orders);

        adapter.setItemActionListener(new ItemActionListener() {
            @Override
            public void onItemSwiped(String id) {
                Utils.deleteCompletedOrder(id);
                orders = Utils.getCompletedOrders();
                refreshList(orders);
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

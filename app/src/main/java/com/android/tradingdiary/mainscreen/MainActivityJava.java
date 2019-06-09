package com.android.tradingdiary.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.R;
import com.android.tradingdiary.completedorders.CompletedOrdersActivity;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.edit.EditOrderActivity;
import com.android.tradingdiary.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import io.paperdb.Paper;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivityJava extends AppCompatActivity {

    private OrderAdapter adapter;
    private ArrayList<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tasks_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trading Diary");
        Utils.setupSystemUI(this);

        setupList();

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(MainActivityJava.this, EditOrderActivity.class);
                intent.putExtra("IS_NEW", true);
                startActivity(intent);
            }
        });
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.list_orders);
        adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        touchHelper.attachToRecyclerView(recyclerView);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(animationController);

        orders = Utils.getOrders();
        refreshList(orders);

        adapter.setItemActionListener(new ItemActionListener() {
            @Override
            public void onItemSwiped(String id) {
//                deleteOrder(id);
                Order order = Utils.getOrder(id);
                Utils.saveCompletedOrder(order);
                orders = Utils.deleteOrder(id);
                refreshList(orders);
            }

            @Override
            public void onItemClicked(String id) {
                Intent intent = new Intent(MainActivityJava.this, EditOrderActivity.class);
                intent.putExtra("ORDER_ID", id);
                startActivity(intent);
            }
        });
    }

    private void deleteOrder(String id) {
        boolean hasChanged = false;
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order event = iterator.next();
            if(event.getId().equals(id)) {
//                addToCompletedEvents(event);
                iterator.remove();
                hasChanged = true;
            }
        }
        if(hasChanged) {
            Paper.book().write("orders", orders);
        }
    }

    private void refreshList(ArrayList<Order> orders) {
        adapter.setEntities(orders);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orders = Utils.getOrders();
        refreshList(orders);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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
            case R.id.action_completed_orders:
                showCompletedOrders();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showCompletedOrders() {
        Intent intent = new Intent(MainActivityJava.this, CompletedOrdersActivity.class);
        startActivity(intent);
    }
}

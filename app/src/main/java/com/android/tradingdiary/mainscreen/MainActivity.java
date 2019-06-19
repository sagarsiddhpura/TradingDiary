package com.android.tradingdiary.mainscreen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.BuildConfig;
import com.android.tradingdiary.R;
import com.android.tradingdiary.completedorders.CompletedOrdersActivity;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.edit.EditOrderActivity;
import com.android.tradingdiary.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
                Intent intent = new Intent(MainActivity.this, EditOrderActivity.class);
                intent.putExtra("IS_NEW", true);
                startActivity(intent);
            }
        });

        if(BuildConfig.DEBUG) {
//            Intent intent = new Intent(MainActivity.this, EditOrderActivity.class);
//            intent.putExtra("IS_NEW", true);
//            startActivity(intent);

            Intent intent = new Intent(MainActivity.this, EditOrderActivity.class);
            intent.putExtra("ORDER_ID", "1560936197335");
            startActivity(intent);
        }
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.list_orders);
        adapter = new OrderAdapter(this);
        recyclerView.setAdapter(adapter);
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(animationController);

        orders = Utils.getOrders();
        refreshList(orders);

        adapter.setItemActionListener(new ItemActionListener() {
            @Override
            public void onItemSwiped(final String id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to delete this Order?")
                        .setTitle("Delete Order");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int itemId) {
                        Order order = Utils.getOrder(id);
                        Utils.saveCompletedOrder(order);
                        orders = Utils.deleteOrder(id);
                        refreshList(orders);
                    }
                });
                builder.setNegativeButton("CANCEL",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onItemClicked(String id) {
                Intent intent = new Intent(MainActivity.this, EditOrderActivity.class);
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
        Intent intent = new Intent(MainActivity.this, CompletedOrdersActivity.class);
        startActivity(intent);
    }
}

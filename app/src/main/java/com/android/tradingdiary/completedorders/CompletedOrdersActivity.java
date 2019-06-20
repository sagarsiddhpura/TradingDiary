package com.android.tradingdiary.completedorders;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;
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
        getSupportActionBar().setTitle("Order History");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_completed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_orders:
                deleteAllOrders();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void deleteAllOrders() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompletedOrdersActivity.this);
        builder.setMessage("Are you sure you want to delete all Orders?")
                .setTitle("Delete All Orders");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemId) {
                Utils.deleteAllCompletedOrders();
                Toast.makeText(getApplicationContext(),"All Orders deleted...",Toast.LENGTH_LONG).show();
                orders = Utils.getOrders();
                refreshList(orders);
            }
        });
        builder.setNegativeButton("CANCEL",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

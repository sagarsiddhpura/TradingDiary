package com.android.tradingdiary.mainscreen;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.BuildConfig;
import com.android.tradingdiary.R;
import com.android.tradingdiary.completedorders.CompletedOrdersActivity;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.edit.EditOrderActivity;
import com.android.tradingdiary.utils.DateTimeUtils;
import com.android.tradingdiary.utils.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private OrderAdapter adapter;
    private ArrayList<Order> orders;
    private String filter = "";
    private Chip chip;
    private long filterTimeStart;
    private long filterTimeEnd;
    private EditText search;
    private LinearLayout searchParent;
    private TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tasks_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Trading Diary");
        Utils.setupSystemUI(this);

        setupList();

        chip = findViewById(R.id.chip);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFilter();
            }
        });
        search = findViewById(R.id.search);
        searchParent = findViewById(R.id.search_parent);
        Chip searchClose = findViewById(R.id.search_close);
        searchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                closeSearch();
            }
        });

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

//            Intent intent = new Intent(MainActivity.this, EditOrderActivity.class);
//            intent.putExtra("ORDER_ID", "1560936197335");
//            startActivity(intent);
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
        ArrayList<Order> ordersFiltered = orders;
        if(filter != null && !filter.equals("")) {
            if(filter.equals("Today")) {
                ordersFiltered = new ArrayList<>();
                for (Order order : orders) {
                    if (order.creationDate >= DateTimeUtils.getTodayStart()) {
                        ordersFiltered.add(order);
                    }
                }
            } else if(filter.equals("RecentlyAdded")) {
                ordersFiltered = new ArrayList<>();
                for (Order order : orders) {
                    if (order.creationDate >= DateTimeUtils.getPrevious7DaysEnd()) {
                        ordersFiltered.add(order);
                    }
                }
            } else if(filter.equals("Date")) {
                ordersFiltered = new ArrayList<>();
                for (Order order : orders) {
                    if (order.creationDate > filterTimeStart && order.creationDate < filterTimeEnd) {
                        ordersFiltered.add(order);
                    }
                }
            } else if(filter.equals("Search") && !"".equals(search.getText().toString())) {
                ordersFiltered = new ArrayList<>();
                for (Order order : orders) {
                    if (order.isMatchingSearch(search.getText().toString())) {
                        ordersFiltered.add(order);
                    }
                }
            }
        }
        adapter.setEntities(ordersFiltered);
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
            case R.id.filter_none:
                removeFilter();
                break;
            case R.id.filter_today:
                initFilter("Today", "Today");
                break;
            case R.id.filter_date:
                filterDateOrders();
                break;
            case R.id.filter_recently_added:
                initFilter("RecentlyAdded", "Recently Added");
                break;
            case R.id.action_search:
                startSearch();
                break;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to delete all Orders?")
                .setTitle("Delete All Orders");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemId) {
                Utils.deleteAllOrders();
                Toast.makeText(getApplicationContext(),"All Orders deleted...",Toast.LENGTH_LONG).show();
                orders = Utils.getOrders();
                refreshList(orders);
            }
        });
        builder.setNegativeButton("CANCEL",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initFilter(String action, String text) {
        closeSearch();
        filter = action;
        chip.setText(text);
        chip.setVisibility(View.VISIBLE);
        refreshList(orders);
    }

    private void startSearch() {
        removeFilter();
        filter = "Search";
        search.setText("");
        searchParent.setVisibility(View.VISIBLE);
        textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                refreshList(orders);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
        search.addTextChangedListener(textWatcher);
    }

    private void filterDateOrders() {
        long date = DateTimeUtils.getCurrentTimeWithoutSec();
        FragmentManager fm = getSupportFragmentManager();
        DateDialogFragment dialogFragment = DateDialogFragment.newInstance(date, false);
        dialogFragment.show(fm, "DATE");
    }

    private void removeFilter() {
        filter = "";
        chip.setText("");
        chip.setVisibility(View.GONE);
        refreshList(orders);
    }

    private void showCompletedOrders() {
        Intent intent = new Intent(MainActivity.this, CompletedOrdersActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        filterTimeStart = data.getLongExtra(DateDialogFragment.EXTRA_DATE, 0);
        Calendar dayEnd = Calendar.getInstance();
        dayEnd.setTimeInMillis(filterTimeStart);

        dayEnd.set(Calendar.HOUR_OF_DAY, 0);
        dayEnd.set(Calendar.MINUTE, 0);
        dayEnd.set(Calendar.SECOND, 0);
        filterTimeStart = dayEnd.getTimeInMillis();

        dayEnd.set(Calendar.HOUR_OF_DAY, 23);
        dayEnd.set(Calendar.MINUTE, 59);
        dayEnd.set(Calendar.SECOND, 59);
        filterTimeEnd = dayEnd.getTimeInMillis();

        initFilter("Date", DateTimeUtils.longToString(filterTimeStart, DateTimeUtils.DATE));
    }

    private void closeSearch() {
        searchParent.setVisibility(View.GONE);
        search.removeTextChangedListener(textWatcher);
        removeFilter();
        Utils.hideKeyBoard(MainActivity.this);
    }

    @Override
    public void onBackPressed() {
        if(!"".equals(filter)) {
            closeSearch();
            removeFilter();
        } else {
            super.onBackPressed();
        }
    }
}

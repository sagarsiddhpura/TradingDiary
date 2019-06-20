package com.android.tradingdiary.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.SellOrder;
import com.android.tradingdiary.mainscreen.ItemActionListener;
import com.android.tradingdiary.mainscreen.ItemTouchHelperListener;
import com.android.tradingdiary.utils.Utils;

import java.util.ArrayList;

public class SellOrderAdapter extends RecyclerView.Adapter<SellOrderAdapter.ViewHolder> implements ItemTouchHelperListener {

    private ItemActionListener mEventItemActionListener;
    private final LayoutInflater mInflater;
    private ArrayList<SellOrder> items;
    private Context context;

    @Override
    public void onItemSwipeToStart(int position) {
        mEventItemActionListener.onItemSwiped(items.get(position).getId());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private SellOrder item;
        private TextView qty;
        private TextView price;
        private TextView total;
        private ImageView delete;

        private ViewHolder(View itemView, int type) {
            super(itemView);
            qty = itemView.findViewById(R.id.order_qty);
            price = itemView.findViewById(R.id.order_price);
            total = itemView.findViewById(R.id.total_price);
            delete = itemView.findViewById(R.id.delete);
        }

        private void bind(final SellOrder item_) {
            item = item_;
            qty.setText("Quantity: " + Utils.formatDouble(item_.getSellQty()));
            price.setText("Price(Unit): " + Utils.formatDouble(item_.getSellPrice()));
            total.setText(Utils.formatDouble((item_.getSellQty() * item_.getSellPrice())));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                mEventItemActionListener.onItemSwiped(item_.getId());
                }
            });
        }
    }

    public SellOrderAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_sell_order, parent, false);
        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (items != null) {
            final SellOrder current = items.get(position);
            holder.bind(current);
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    public void setEntities(ArrayList<SellOrder> events) {
        items = events;
        notifyDataSetChanged();
    }

    public void setItemActionListener(ItemActionListener listener) {
        mEventItemActionListener = listener;
    }
}

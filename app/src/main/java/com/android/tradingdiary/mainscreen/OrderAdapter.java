package com.android.tradingdiary.mainscreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements ItemTouchHelperListener {

    private ItemActionListener mEventItemActionListener;
    private final LayoutInflater mInflater;
    private List<Order> items;
    private Context context;

    @Override
    public void onItemSwipeToStart(int position) {
        mEventItemActionListener.onItemSwiped(items.get(position).getId());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private Order item;
        private TextView mTitle;
        private TextView mDesc;
        private MaterialCardView mCard;

        private ViewHolder(View itemView, int type) {
            super(itemView);

            mCard = itemView.findViewById(R.id.event_card);
            mTitle = itemView.findViewById(R.id.event_title);
            mDesc = itemView.findViewById(R.id.event_desc);
        }

        private void bind(Order item_) {
            item = item_;
            mTitle.setText(item_.getName());
        }
    }

    OrderAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_event, parent, false);
        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (items != null) {
            final Order current = items.get(position);
            holder.bind(current);

            if (mEventItemActionListener != null) {
                holder.mCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEventItemActionListener.onItemClicked(current.getId());
                    }
                });
            }
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

    public void setEntities(List<Order> events) {
        items = events;
        notifyDataSetChanged();
    }

    public void setItemActionListener(ItemActionListener listener) {
        mEventItemActionListener = listener;
    }
}

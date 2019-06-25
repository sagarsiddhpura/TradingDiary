package com.android.tradingdiary.mainscreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.tradingdiary.R;
import com.android.tradingdiary.data.Order;
import com.android.tradingdiary.utils.DateTimeUtils;
import com.android.tradingdiary.utils.Utils;
import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements ItemTouchHelperListener {

    private ItemActionListener mEventItemActionListener;
    private final LayoutInflater mInflater;
    private ArrayList<Order> orders;
    private Context context;

    @Override
    public void onItemSwipeToStart(int position) {
        mEventItemActionListener.onItemSwiped(orders.get(position).getId());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mId;
        private final ImageView delete;
        private final TextView remarks;
        private TextView mSub;
        private Order item;
        private TextView mTitle;
        private TextView mDesc;
        private MaterialCardView mCard;

        private ViewHolder(View itemView, int type) {
            super(itemView);

            mCard = itemView.findViewById(R.id.event_card);
            mTitle = itemView.findViewById(R.id.event_title);
            mDesc = itemView.findViewById(R.id.event_desc);
            mSub = itemView.findViewById(R.id.event_subtitle);
            mId = itemView.findViewById(R.id.event_id);
            delete = itemView.findViewById(R.id.delete);
            remarks = itemView.findViewById(R.id.event_remarks);
        }

        private void bind(final Order item_) {
            item = item_;
            mTitle.setText(item_.getName());
            if(item_.getRemainingSellQty() == 0) {
                mDesc.setText(Utils.formatDouble(item_.buyQty) + " sold");
                if(item_.getProfitLoss() > 0) {
                    mSub.setText("Profit: " +  (Utils.formatDouble(item_.getProfitLoss())) + " Rs.");
                } else {
                    mSub.setText("Loss: " +  (Utils.formatDouble(item_.getProfitLoss() * -1)) + " Rs.");
                }
            } else {
                mDesc.setText(Utils.formatDouble(item_.getRemainingSellQty()) + "/" + Utils.formatDouble(item_.buyQty) + " " + item_.unit +" left");
                mSub.setText(Utils.formatDouble(item_.getRemainingSaleTotal()) + " Rs. sale remaining");
            }
            mId.setText("Order ID: " + item_.userId + "\nCreated: " + DateTimeUtils.longToString(item_.creationDate, DateTimeUtils.DATE)
                    + " " + DateTimeUtils.longToString(item_.creationDate, DateTimeUtils.TIME));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEventItemActionListener.onItemSwiped(item_.getId());
                }
            });
            if(item_.isComplete()) {
                mCard.setCardBackgroundColor(context.getResources().getColor(R.color.black_negative));
            } else {
                mCard.setCardBackgroundColor(context.getResources().getColor(item_.color));
            }
            if(item_.remarks != null && !item_.remarks.equals("")) {
                remarks.setVisibility(View.VISIBLE);
                remarks.setText(item_.remarks);
            } else {
                remarks.setVisibility(View.GONE);
            }
        }
    }

    public OrderAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_order, parent, false);
        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (orders != null) {
            final Order current = orders.get(position);
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
        if (orders != null) {
            return orders.size();
        } else {
            return 0;
        }
    }

    public void setEntities(ArrayList<Order> items_) {
        orders = new ArrayList<>(items_);
        notifyDataSetChanged();
    }

    public void setItemActionListener(ItemActionListener listener) {
        mEventItemActionListener = listener;
    }
}

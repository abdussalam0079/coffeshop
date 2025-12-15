package com.example.coffeshop2.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeshop2.Domain.OrderModel;
import com.example.coffeshop2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private final List<OrderModel> list;

    public OrderAdapter(Context context, List<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = list.get(position);
        
        if (order == null) {
            return;
        }

        // Set order ID
        String orderId = order.getOrderId();
        if (orderId != null && !orderId.isEmpty()) {
            holder.orderId.setText("Order #" + orderId.substring(0, Math.min(8, orderId.length())));
        } else {
            holder.orderId.setText("Order #N/A");
        }

        // Set date
        if (order.getTimestamp() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
            holder.orderDate.setText(sdf.format(new Date(order.getTimestamp())));
        } else {
            holder.orderDate.setText("Date not available");
        }

        // Set status with color
        String status = order.getStatus() != null ? order.getStatus() : "pending";
        holder.orderStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
        
        // Set status color
        int statusColor = getStatusColor(status);
        holder.orderStatus.setTextColor(ContextCompat.getColor(context, statusColor));

        // Set item count
        int itemCount = (order.getItems() != null) ? order.getItems().size() : 0;
        holder.itemCount.setText(itemCount + (itemCount == 1 ? " item" : " items"));

        // Set total price
        holder.totalPrice.setText(String.format("$%.2f", order.getTotal()));
    }

    private int getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "pending":
                return R.color.orange;
            case "confirmed":
            case "preparing":
                return R.color.dark_brown;
            case "ready":
                return android.R.color.holo_green_dark;
            case "delivered":
                return android.R.color.holo_blue_dark;
            case "cancelled":
                return android.R.color.holo_red_dark;
            default:
                return R.color.brown;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView orderDate;
        TextView orderStatus;
        TextView itemCount;
        TextView totalPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderStatus = itemView.findViewById(R.id.orderStatus);
            itemCount = itemView.findViewById(R.id.itemCount);
            totalPrice = itemView.findViewById(R.id.totalPrice);
        }
    }
}


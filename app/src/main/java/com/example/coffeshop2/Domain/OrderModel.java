package com.example.coffeshop2.Domain;

import java.util.List;

public class OrderModel {
    private String id;
    private String orderId;
    private String userId;
    private List<CartModel> items;
    private double totalAmount;
    private String status;
    private long timestamp;
    private String deliveryAddress;

    public OrderModel() {}

    public OrderModel(String id, String userId, List<CartModel> items, double totalAmount, String status, long timestamp, String deliveryAddress) {
        this.id = id;
        this.orderId = id;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.timestamp = timestamp;
        this.deliveryAddress = deliveryAddress;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartModel> getItems() { return items; }
    public void setItems(List<CartModel> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTotal() { return totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}
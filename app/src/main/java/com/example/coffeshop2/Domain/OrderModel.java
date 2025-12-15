package com.example.coffeshop2.Domain;

import java.util.List;

public class OrderModel {
    private String orderId;
    private String userId; // User ID for organizing orders by user
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String deliveryAddress;
    private List<CartModel> items;
    private double subtotal;
    private double deliveryFee;
    private double tax;
    private double total;
    private String discountCode;
    private String status; // "pending", "confirmed", "preparing", "ready", "delivered", "cancelled"
    private long timestamp;
    private String notes;

    public OrderModel() {
        // Required empty constructor for Firebase
    }

    public OrderModel(String orderId, String userId, String customerName, String customerPhone, String customerEmail,
                     String deliveryAddress, List<CartModel> items, double subtotal, double deliveryFee,
                     double tax, double total, String discountCode, String status, long timestamp, String notes) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.tax = tax;
        this.total = total;
        this.discountCode = discountCode;
        this.status = status;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public List<CartModel> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public String getDiscountCode() { return discountCode; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public String getNotes() { return notes; }

    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setItems(List<CartModel> items) { this.items = items; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public void setTax(double tax) { this.tax = tax; }
    public void setTotal(double total) { this.total = total; }
    public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setNotes(String notes) { this.notes = notes; }
}


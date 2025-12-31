package com.example.coffeshop2.Utils;

import com.example.coffeshop2.Domain.CartModel;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartModel> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(CartModel item) {
        // Check if item already exists in cart
        for (CartModel cartItem : cartItems) {
            if (cartItem.getItemId().equals(item.getItemId())) {
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        cartItems.add(item);
    }

    public void removeFromCart(String itemId) {
        cartItems.removeIf(item -> item.getItemId().equals(itemId));
    }

    public void updateQuantity(String itemId, int quantity) {
        for (CartModel item : cartItems) {
            if (item.getItemId().equals(itemId)) {
                if (quantity <= 0) {
                    removeFromCart(itemId);
                } else {
                    item.setQuantity(quantity);
                }
                break;
            }
        }
    }

    public List<CartModel> getCartItems() {
        return cartItems;
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (CartModel item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    public double getDeliveryFee() {
        return 10.0; // Fixed delivery fee
    }

    public double getTax() {
        return getSubtotal() * 0.03; // 3% tax
    }

    public double getTotal() {
        return getSubtotal() + getDeliveryFee() + getTax();
    }

    public void clearCart() {
        cartItems.clear();
    }

    public int getCartItemCount() {
        return cartItems.size();
    }
}


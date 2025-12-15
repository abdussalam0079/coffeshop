package com.example.coffeshop2.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeshop2.Utils.CartManager;
import com.example.coffeshop2.Utils.UserManager;
import com.example.coffeshop2.Repository.MainRepositry;
import com.example.coffeshop2.Domain.OrderModel;
import com.example.coffeshop2.databinding.ActivityCartBinding;
import com.example.coffeshop2.Domain.CartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnCartUpdateListener {

    private ActivityCartBinding binding;
    private CartItemAdapter adapter;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = CartManager.getInstance();

        // Back button
        binding.backButton.setOnClickListener(v -> finish());

        // Browse coffee button (empty state)
        binding.browseCoffeeButton.setOnClickListener(v -> {
            finish(); // Go back to main screen
        });

        // Setup RecyclerView
        binding.recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(this, cartManager.getCartItems(), this);
        binding.recyclerViewCartItems.setAdapter(adapter);

        // Apply discount button
        binding.applyDiscountButton.setOnClickListener(v -> {
            String code = binding.discountCodeInput.getText().toString().trim();
            if (!code.isEmpty()) {
                // TODO: Implement discount code logic
                Toast.makeText(this, "Discount code applied", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a discount code", Toast.LENGTH_SHORT).show();
            }
        });

        // Place Order button
        binding.proceedToCheckoutButton.setText("Place Order");
        binding.proceedToCheckoutButton.setOnClickListener(v -> {
            int itemCount = cartManager.getCartItemCount();
            if (itemCount > 0) {
                placeOrder();
            } else {
                Toast.makeText(this, "Your cart is empty. Please add items to cart first.", Toast.LENGTH_SHORT).show();
            }
        });

        updateCartSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateList(cartManager.getCartItems());
        updateCartSummary();
    }

    @Override
    public void onCartUpdated() {
        updateCartSummary();
    }

    private void updateCartSummary() {
        binding.subtotalText.setText(String.format("$%.2f", cartManager.getSubtotal()));
        binding.deliveryFeeText.setText(String.format("$%.2f", cartManager.getDeliveryFee()));
        binding.taxText.setText(String.format("$%.2f", cartManager.getTax()));
        binding.totalText.setText(String.format("$%.2f", cartManager.getTotal()));

        if (cartManager.getCartItemCount() == 0) {
            binding.recyclerViewCartItems.setVisibility(View.GONE);
            binding.discountContainer.setVisibility(View.GONE);
            binding.orderSummaryCard.setVisibility(View.GONE);
            binding.proceedToCheckoutButton.setVisibility(View.GONE);
            binding.emptyCartLayout.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewCartItems.setVisibility(View.VISIBLE);
            binding.discountContainer.setVisibility(View.VISIBLE);
            binding.orderSummaryCard.setVisibility(View.VISIBLE);
            binding.proceedToCheckoutButton.setVisibility(View.VISIBLE);
            binding.emptyCartLayout.setVisibility(View.GONE);
        }
    }

    private void placeOrder() {
        // Validate cart is not empty
        if (cartManager.getCartItemCount() == 0) {
            Toast.makeText(this, "Your cart is empty. Please add items to cart first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.proceedToCheckoutButton.setEnabled(false);
        binding.proceedToCheckoutButton.setText("Placing Order...");

        // Get cart items (create a copy to avoid issues)
        List<CartModel> cartItems = new ArrayList<>();
        for (CartModel item : cartManager.getCartItems()) {
            CartModel copy = new CartModel(
                    item.getItemId(),
                    item.getTitle(),
                    item.getExtra(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getImageUrl()
            );
            copy.setOriginalPrice(item.getOriginalPrice());
            cartItems.add(copy);
        }
        
        // Get discount code
        String discountCode = binding.discountCodeInput.getText().toString().trim();
        if (discountCode.isEmpty()) {
            discountCode = null;
        }

        // Calculate totals
        double subtotal = cartManager.getSubtotal();
        double deliveryFee = cartManager.getDeliveryFee();
        double tax = cartManager.getTax();
        double total = cartManager.getTotal();

        // Get user ID
        UserManager userManager = UserManager.getInstance(this);
        String userId = userManager.getUserId();

        // Create order model
        OrderModel order = new OrderModel(
                null, // Will be set by Firebase
                userId, // User ID
                "Customer", // Default customer name (can be updated later)
                "", // Phone (can be added later)
                "", // Email (can be added later)
                "", // Delivery address (can be added later)
                cartItems,
                subtotal,
                deliveryFee,
                tax,
                total,
                discountCode,
                "pending", // Order status
                System.currentTimeMillis(), // Timestamp
                "" // Notes
        );

        // Save order to Firebase with user-based structure
        MainRepositry.getInstance().saveOrder(order, userId, task -> {
            binding.proceedToCheckoutButton.setEnabled(true);
            binding.proceedToCheckoutButton.setText("Place Order");

            if (task.isSuccessful()) {
                String orderId = order.getOrderId();
                Toast.makeText(this, "Order placed successfully! Order ID: " + (orderId != null ? orderId : "N/A"), Toast.LENGTH_LONG).show();
                
                // Clear cart after successful order
                cartManager.clearCart();
                adapter.updateList(cartManager.getCartItems());
                updateCartSummary();
                
                // Clear discount code input
                binding.discountCodeInput.setText("");
                
                // Optionally navigate back or to order confirmation screen
                // finish();
            } else {
                String errorMessage = "Failed to place order.";
                if (task.getException() != null) {
                    Exception exception = task.getException();
                    String exceptionMessage = exception.getMessage();
                    
                    // Log full error for debugging
                    android.util.Log.e("CartActivity", "Order save failed", exception);
                    
                    if (exceptionMessage != null) {
                        if (exceptionMessage.toLowerCase().contains("permission") || 
                            exceptionMessage.toLowerCase().contains("denied")) {
                            errorMessage = "❌ Permission Denied!\n\n" +
                                    "Please update Firebase Database Rules:\n\n" +
                                    "1. Go to Firebase Console\n" +
                                    "2. Realtime Database → Rules\n" +
                                    "3. Use this rule:\n\n" +
                                    "{\n" +
                                    "  \"rules\": {\n" +
                                    "    \".read\": true,\n" +
                                    "    \".write\": true\n" +
                                    "  }\n" +
                                    "}\n\n" +
                                    "Then click Publish!";
                        } else {
                            errorMessage = "Error: " + exceptionMessage;
                        }
                    }
                    exception.printStackTrace();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}

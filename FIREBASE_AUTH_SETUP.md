# 🔐 Firebase Authentication Setup Guide

## Overview

The app now uses **Firebase Anonymous Authentication** to securely organize orders by user. This provides:
- ✅ Secure user identification
- ✅ User-specific order storage
- ✅ Proper security rules
- ✅ No login required (automatic anonymous auth)

## 📋 Setup Steps

### Step 1: Enable Anonymous Authentication in Firebase

1. **Go to Firebase Console**
   - https://console.firebase.google.com/
   - Select your project: **coffeshoppop**

2. **Enable Anonymous Authentication**
   - Click "Authentication" in left sidebar
   - Click "Get Started" (if first time)
   - Click "Sign-in method" tab
   - Find "Anonymous" in the list
   - Click on it
   - **Toggle "Enable"** to ON
   - Click "Save"

### Step 2: Update Database Rules

1. **Go to Realtime Database → Rules tab**

2. **Copy and paste these rules:**

```json
{
  "rules": {
    "Banner": {
      ".read": true,
      ".write": false
    },
    "Category": {
      ".read": true,
      ".write": false
    },
    "Popular": {
      ".read": true,
      ".write": false
    },
    "Items": {
      ".read": true,
      ".write": false
    },
    "orders": {
      "$uid": {
        ".read": "auth != null && auth.uid == $uid",
        ".write": "auth != null && auth.uid == $uid",
        "$orderId": {
          ".read": "auth != null && auth.uid == $uid",
          ".write": "auth != null && auth.uid == $uid",
          ".validate": "newData.hasChildren(['orderId', 'userId', 'items', 'total', 'status', 'timestamp']) && newData.child('total').isNumber() && newData.child('total').val() > 0 && newData.child('status').val().matches(/pending|confirmed|preparing|ready|delivered|cancelled/) && newData.child('timestamp').isNumber() && newData.child('userId').val() == $uid"
        }
      }
    },
    "MoodRecommendations": {
      "$uid": {
        ".read": "auth != null && auth.uid == $uid",
        ".write": "auth != null && auth.uid == $uid"
      }
    }
  }
}
```

3. **Click "Publish"**

### Step 3: Data Structure

Orders are now stored in this structure:

```
orders/
  {userId}/
    {orderId}/
      orderId: "..."
      userId: "..."
      items: [...]
      total: 45.50
      status: "pending"
      timestamp: 1678886400
      ...
```

### Step 4: Test the App

1. **Build and run the app**
2. **Add items to cart**
3. **Place an order**
4. **Check Firebase Console → Realtime Database → Data**
   - You should see: `orders/{userId}/{orderId}`

## 🔒 Security Features

### What the Rules Do:

1. **User Isolation**: Users can only read/write their own orders
   - `auth.uid == $uid` ensures users can only access their data

2. **Data Validation**: 
   - Total must be a positive number
   - Status must be from predefined list
   - Required fields must be present
   - User ID must match the path

3. **Admin Access** (Optional - see below)

## 👨‍💼 Admin Access (Optional)

To allow admins to view all orders, add this to rules:

```json
{
  "rules": {
    "orders": {
      ".read": "auth != null && (root.child('admins').child(auth.uid).exists() || root.child('orders').child(auth.uid).exists())",
      "$uid": {
        ".read": "auth != null && (auth.uid == $uid || root.child('admins').child(auth.uid).exists())",
        ".write": "auth != null && auth.uid == $uid",
        "$orderId": {
          ".read": "auth != null && (auth.uid == $uid || root.child('admins').child(auth.uid).exists())",
          ".write": "auth != null && auth.uid == $uid"
        }
      }
    }
  }
}
```

Then create an `admins` node with admin user IDs.

## ✅ Validation Rules Explained

The validation ensures:
- ✅ `total` is a number and > 0
- ✅ `status` is one of: pending, confirmed, preparing, ready, delivered, cancelled
- ✅ `timestamp` is a number
- ✅ `userId` matches the path (`$uid`)
- ✅ Required fields exist

## 🐛 Troubleshooting

### "Permission Denied" Error

1. **Check Anonymous Auth is enabled** (Step 1)
2. **Verify rules are published** (Step 2)
3. **Check Logcat** for authentication errors
4. **Uninstall and reinstall app** to get fresh auth token

### Orders Not Showing

1. **Check user ID** - Logcat will show the user ID
2. **Verify Firebase Console** - Check `orders/{userId}/` path
3. **Check authentication** - UserManager logs auth status

## 📱 How It Works

1. **App starts** → UserManager initializes
2. **Anonymous auth** → Firebase creates anonymous user
3. **User ID saved** → Stored locally for persistence
4. **Orders saved** → Under `orders/{userId}/{orderId}`
5. **Orders loaded** → Only user's own orders

## 🎯 Benefits

- ✅ **Secure**: Users can't access other users' orders
- ✅ **Organized**: Orders grouped by user
- ✅ **Validated**: Data integrity enforced
- ✅ **Scalable**: Easy to query and report
- ✅ **No Login**: Automatic anonymous authentication


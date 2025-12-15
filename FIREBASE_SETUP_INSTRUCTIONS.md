# Firebase Database Rules Setup Instructions

## 🔥 Fix "Permission Denied" Error

If you're getting a "permission denied" error when placing orders, you need to update your Firebase Realtime Database security rules.

### Steps to Fix:

1. **Go to Firebase Console**
   - Open [Firebase Console](https://console.firebase.google.com/)
   - Select your project

2. **Navigate to Realtime Database**
   - Click on "Realtime Database" in the left sidebar
   - Click on the "Rules" tab at the top

3. **Update the Rules**
   - Copy the rules from `firebase_database_rules.json` file
   - Paste them into the Firebase Console rules editor
   - Click "Publish" to save the changes

### What These Rules Do:

- **Banner, Category, Popular, Items**: Read-only access (users can read but not write)
- **Orders**: Read and write access (users can create and view orders)
- **MoodRecommendations**: Read and write access (users can save mood recommendations)

### For Development (Temporary - Less Secure):

If you want to allow all reads and writes for testing, you can use:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

⚠️ **Warning**: The above rule allows anyone to read/write everything. Only use for development/testing!

### For Production (Recommended):

For production, you should use authenticated users:

```json
{
  "rules": {
    "Banner": {
      ".read": true,
      ".write": "auth != null"
    },
    "Category": {
      ".read": true,
      ".write": "auth != null"
    },
    "Popular": {
      ".read": true,
      ".write": "auth != null"
    },
    "Items": {
      ".read": true,
      ".write": "auth != null"
    },
    "Orders": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "MoodRecommendations": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

### Quick Fix (Copy-Paste Ready):

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
    "Orders": {
      ".read": true,
      ".write": true
    },
    "MoodRecommendations": {
      ".read": true,
      ".write": true
    }
  }
}
```

### After Updating Rules:

1. The changes take effect immediately
2. Try placing an order again
3. The "permission denied" error should be resolved

### Troubleshooting:

- Make sure you clicked "Publish" after updating the rules
- Check that your Firebase project is correctly configured
- Verify that `google-services.json` is in the `app/` folder
- Make sure you're using the correct Firebase project


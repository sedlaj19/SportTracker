# Firebase Setup Guide for SportTracker

## Prerequisites
1. Google account
2. Firebase project

## Setup Steps

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project"
3. Enter project name (e.g., "SportTracker")
4. Follow the setup wizard

### 2. Add Android App to Firebase
1. In Firebase console, click "Add app" and select Android
2. Enter Android package name: `com.sporttracker.android`
3. Download `google-services.json`
4. Place the file in `/androidApp/` directory (NOT in src/)

### 3. Enable Firebase Services

#### Firestore Database
1. Go to Firebase Console → Build → Firestore Database
2. Click "Create database"
3. Select "Start in test mode" (for development)
4. Choose your preferred location
5. Click "Enable"

#### Authentication
1. Go to Firebase Console → Build → Authentication
2. Click "Get started"
3. Go to "Sign-in method" tab
4. Enable "Anonymous" authentication
5. Click "Save"

### 4. Firestore Security Rules (Development)
For development, use these permissive rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /activities/{document=**} {
      allow read, write: if true;
    }
  }
}
```

For production, use more secure rules:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /activities/{activityId} {
      allow read: if request.auth != null &&
        (resource.data.userId == request.auth.uid || !exists(/databases/$(database)/documents/activities/$(activityId)));
      allow create: if request.auth != null &&
        request.resource.data.userId == request.auth.uid;
      allow update: if request.auth != null &&
        resource.data.userId == request.auth.uid;
      allow delete: if request.auth != null &&
        resource.data.userId == request.auth.uid;
    }
  }
}
```

### 5. Test Firebase Integration
1. Build and run the app
2. Create a new activity with "Remote" storage
3. Check Firebase Console → Firestore Database
4. You should see the activity document in the `activities` collection

## Troubleshooting

### Common Issues

1. **Build fails with "google-services.json not found"**
   - Make sure `google-services.json` is in `/androidApp/` directory
   - NOT in `/androidApp/src/`

2. **Firebase initialization fails**
   - Check that package name in `google-services.json` matches `com.sporttracker.android`
   - Ensure Firebase project is properly configured

3. **Authentication fails**
   - Verify Anonymous authentication is enabled in Firebase Console
   - Check internet connection

4. **Firestore operations fail**
   - Verify Firestore is enabled in Firebase Console
   - Check security rules allow the operation
   - Check internet connectivity

## Project Structure
```
SportTracker/
├── androidApp/
│   ├── google-services.json  # Firebase config (add this file)
│   ├── google-services-example.json  # Example template
│   └── src/
└── shared/
    └── src/
        └── commonMain/
            └── kotlin/
                └── com/sporttracker/
                    └── data/
                        ├── auth/
                        │   └── FirebaseAuthService.kt
                        └── remote/
                            └── FirebaseRemoteDataSource.kt
```

## Environment Configuration
No additional environment variables needed - everything is configured through `google-services.json`.

## Next Steps
1. Download `google-services.json` from Firebase Console
2. Place it in `/androidApp/` directory
3. Build and run the app
4. Test creating activities with "Remote" storage option

## Security Notes
- Never commit `google-services.json` to version control
- Add `google-services.json` to `.gitignore`
- Use restrictive Firestore rules in production
- Enable additional authentication methods as needed
# GameHub

GameHub is an Android application built with Kotlin and Firebase for browsing games, exploring categories, reading reviews, managing favorites, and tracking personal game activity.

## Project Overview

This project appears to be a mobile game discovery and community app. The app includes:

- Login and signup flows
- Game browsing and category discovery
- Game detail and review views
- User favorites, ratings, and wishlist tracking
- Commenting on games and user profiles
- Firebase-backed authentication and data services

## Tech Stack

- Kotlin
- Android Jetpack
- ViewBinding
- Firebase Authentication
- Firebase Firestore / Realtime Database
- Firebase Storage
- Firebase Analytics / Crashlytics
- Glide for image loading
- Material Components and Navigation

## Architecture

The app uses a standard Android app structure:

- `app/src/main/java/com/example/gamehub` — application source code
- `app/src/main/res` — layouts, drawables, navigation, values, and XML resources
- `app/src/main/AndroidManifest.xml` — app manifest and activities
- `app/google-services.json` — Firebase configuration

## Main Features

- User authentication (`LoginActivity`, `SignupActivity`)
- Home and game discovery screens (`HomeFragment`, `FindFragment`, `GameFragment`)
- Game review and rating support (`ReviewFragment`, `ReviewAdapter`)
- Comments and discussion system (`CommentFragment`, `CommentDialogFragment`)
- Personalized pages (`MyPageFragment`, `MyFavoriteFragment`, `MyWishlistFragment`)
- Preferences and settings (`PreferenceFragment`)

## Project Structure

```text
GameHub/
├── app/
│   ├── src/
│   │   ├── androidTest/
│   │   ├── main/
│   │   └── test/
│   ├── build.gradle
│   ├── google-services.json
│   ├── proguard-rules.pro
│   └── .gitignore
├── gradle/
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
├── .gitignore
└── README.md
```

## Prerequisites

Before building the app, make sure you have:

- Android Studio
- JDK 8 or newer
- Android SDK configured
- A Firebase project with `google-services.json` connected to the app package

## Setup Instructions

1. Clone the repository:

```bash
git clone https://github.com/Park-Hyeong-Bin/GameHub.git
cd GameHub
```

2. Open the project in Android Studio.

3. Let Gradle sync the project.

4. Ensure Firebase is configured:
   - Add or update `app/google-services.json` if required for your environment.
   - Make sure the package name matches your Firebase project configuration.

5. Run the app on an emulator or Android device.

## Build

```bash
./gradlew build
```

For Windows:

```bash
gradlew.bat build
```

## Notes

This repository appears to be a Kotlin-based Android application using Firebase services. If you plan to use or extend it, verify the Firebase configuration and Android SDK versions for your local environment before running the app.

## License

This project does not appear to include a license file in the repository root. If you intend to distribute or reuse this project, consider adding a license.

## Author

Park Hyeong-Bin

# Herbs and Friends App

A comprehensive Android e-commerce application for selling herbs and plants, featuring both customer and admin interfaces with real-time notifications, payment integration, and Firebase backend services.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Team Members](#team-members)
- [Technologies Used](#technologies-used)
- [API Versions & SDK Information](#api-versions--sdk-information)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Features](#features)
- [Setup Instructions](#setup-instructions)
- [Dependencies](#dependencies)
- [Contributing](#contributing)

## 🎯 Project Overview

Herbs and Friends App is a modern Android e-commerce platform built with Java, featuring a dual-interface design that serves both customers and administrators. The application leverages Firebase services for backend operations, real-time notifications, and secure authentication.

### Key Features

- **Customer Interface**: Product browsing, shopping cart, checkout, order tracking
- **Admin Interface**: Product management, order processing, coupon management, dashboard analytics
- **Real-time Notifications**: Push notifications for order updates and promotions
- **Payment Integration**: ZaloPay payment gateway integration
- **Authentication**: Google Sign-in and email/password authentication
- **Image Management**: Product image upload and management via Firebase Storage

## 👥 Team Members

<!-- Add your team members here -->

- **Member 1**: [Name] - [Role] - [Student ID]
- **Member 2**: [Name] - [Role] - [Student ID]
- **Member 3**: [Name] - [Role] - [Student ID]
- **Member 4**: [Name] - [Role] - [Student ID]

## 🛠 Technologies Used

### Core Technologies

- **Language**: Java 11
- **Platform**: Android
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Compile SDK**: API 35

### Architecture & Design Patterns

- **MVVM (Model-View-ViewModel)**: Separation of concerns and data binding
- **Repository Pattern**: Abstraction layer for data operations
- **Dependency Injection**: Hilt for managing dependencies
- **Navigation Component**: Type-safe navigation between screens
- **ViewBinding & DataBinding**: Efficient view binding and data binding

### Firebase Services

- **Firebase Authentication**: User authentication and authorization
- **Firebase Firestore**: NoSQL cloud database
- **Firebase Realtime Database**: Real-time data synchronization
- **Firebase Storage**: File and image storage
- **Firebase Cloud Messaging (FCM)**: Push notifications
- **Firebase Analytics**: User behavior tracking

### UI/UX Libraries

- **Material Design Components**: Modern UI components
- **Navigation Component**: Fragment-based navigation
- **RecyclerView**: Efficient list and grid displays
- **ConstraintLayout**: Flexible layout system

### Image & Media

- **Glide**: Image loading and caching library
- **Firebase Storage**: Cloud image storage

### Networking & Data

- **OkHttp**: HTTP client for network requests
- **GSON**: JSON serialization/deserialization
- **Firebase SDK**: Backend service integration

### Authentication & Security

- **Google Play Services Auth**: Google Sign-in integration
- **Firebase Auth**: Email/password and social authentication

### Payment Integration

- **ZaloPay SDK**: Payment gateway integration
- **Commons Codec**: Cryptographic utilities

### Email Services

- **JavaMail**: Email functionality for notifications
- **Android Activation**: Email activation support

### Testing

- **JUnit**: Unit testing framework
- **Espresso**: UI testing framework
- **AndroidJUnitRunner**: Instrumentation testing

## 📱 API Versions & SDK Information

### Android SDK

- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 35 (Android 15)
- **Compile SDK**: API 35
- **Build Tools**: AGP 8.10.0

### Key Library Versions

- **Navigation Component**: 2.9.0
- **Hilt**: 2.56.2
- **Firebase BOM**: 33.15.0
- **Glide**: 4.16.0
- **GSON**: 2.13.1
- **OkHttp**: 4.11.0
- **JavaMail**: 1.6.7
- **Lifecycle**: 2.7.0
- **Material Design**: 1.12.0
- **AppCompat**: 1.7.1

## 🏗 Architecture

### Architectural Pattern: MVVM (Model-View-ViewModel)

Refer to [architechture.drawio.png](./architechture.drawio.png)

### Dependency Injection with Hilt

The application uses Hilt for dependency injection, providing:

- **Singleton Components**: Shared instances across the app
- **Scoped Dependencies**: Lifecycle-aware dependency management
- **Module Organization**: Clear separation of concerns

### Repository Pattern

Each data domain has its own repository:

- `AuthRepository`: User authentication and profile management
- `ProductRepository`: Product catalog and management
- `CartRepository`: Shopping cart operations
- `OrderRepository`: Order processing and management
- `CategoryRepository`: Product categorization
- `CouponRepository`: Discount and promotion management
- `DevicePushNotificationTokenRepository`: Notification token management

### Navigation Architecture

The app uses Navigation Component with Safe Args for type-safe navigation:

- **Customer Navigation**: Home → Products → Cart → Checkout → Orders
- **Admin Navigation**: Dashboard → Products → Orders → Coupons → Profile

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/group4/herbs_and_friends_app/
│   │   ├── data/
│   │   │   ├── api/                    # API interfaces and schemas
│   │   │   ├── communication/          # Notification and messaging
│   │   │   ├── mail/                   # Email functionality
│   │   │   ├── model/                  # Data models and enums
│   │   │   └── repository/             # Repository implementations
│   │   ├── di/                         # Dependency injection modules
│   │   ├── ui/
│   │   │   ├── admin_side/             # Admin interface components
│   │   │   ├── auth/                   # Authentication screens
│   │   │   ├── base/                   # Base classes and utilities
│   │   │   ├── customer_side/          # Customer interface components
│   │   │   └── notification/           # Notification components
│   │   └── utils/                      # Utility classes
│   ├── res/
│   │   ├── layout/                     # UI layouts
│   │   ├── values/                     # Resources and themes
│   │   ├── navigation/                 # Navigation graphs
│   │   └── drawable/                   # Images and drawables
│   └── AndroidManifest.xml
├── build.gradle.kts                    # App-level build configuration
└── google-services.json               # Firebase configuration
```

## ✨ Features

### Customer Features

- **Product Browsing**: Browse products by category with search and filtering
- **Shopping Cart**: Add/remove items with quantity management
- **Checkout Process**: Address management and payment integration
- **Order Tracking**: View order history and status updates
- **User Profile**: Manage personal information and addresses
- **Notifications**: Real-time order updates and promotions

### Admin Features

- **Dashboard**: Analytics and overview of business metrics
- **Product Management**: Add, edit, and manage product catalog
- **Order Management**: Process and track customer orders
- **Coupon Management**: Create and manage promotional codes
- **Profile Management**: Admin account settings

### Technical Features

- **Real-time Notifications**: Firebase Cloud Messaging integration
- **Offline Support**: Local data caching and synchronization
- **Image Management**: Cloud storage for product images
- **Payment Processing**: Secure payment gateway integration
- **Email Notifications**: Automated email communications

## 🚀 Setup Instructions

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK API 35
- Google account for Firebase services

### Installation Steps

1. **Clone the Repository**

   ```bash
   git clone [repository-url]
   cd herbs-and-friends-app
   ```

2. **Firebase Setup**

   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Download `google-services.json` and place it in the `app/` directory
   - Enable required Firebase services (Auth, Firestore, Storage, FCM)

3. **ZaloPay Integration**

   - Place ZaloPay SDK (`zpdk-release-v3.1.aar`) in the `libs/` directory
   - Configure ZaloPay credentials in the application

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

### Configuration

1. **Firebase Configuration**

   - Update Firebase project settings in `google-services.json`
   - Configure Firestore security rules
   - Set up Firebase Storage rules

2. **ZaloPay Configuration**
   - Configure merchant credentials
   - Set up payment environment (sandbox/production)

## 📦 Dependencies

### Core Dependencies

```gradle
// Navigation
implementation("androidx.navigation:navigation-fragment:2.9.0")
implementation("androidx.navigation:navigation-ui:2.9.0")

// Hilt
implementation("com.google.dagger:hilt-android:2.56.2")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")
implementation("com.google.firebase:firebase-storage")
implementation("com.google.firebase:firebase-database")
implementation("com.google.firebase:firebase-messaging")

// UI & Image Loading
implementation("com.github.bumptech.glide:glide:4.16.0")
implementation("com.google.android.material:material:1.12.0")

// Networking & Data
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.google.code.gson:gson:2.13.1")

// Payment & Email
implementation("com.sun.mail:android-mail:1.6.7")
implementation("commons-codec:commons-codec:1.14")
```

### Development Guidelines

- Follow MVVM architecture patterns
- Use Hilt for dependency injection
- Implement proper error handling
- Write unit tests for business logic
- Follow Material Design guidelines
- Use ViewBinding for view interactions

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Note**: This application is developed as part of the PRM392 course project. For educational purposes only.

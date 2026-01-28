# Bazaar - Modern Inventory & Sales Management

Bazaar is a production-grade Android application designed to streamline inventory management and sales tracking for small to medium-sized businesses. Built with a focus on technical excellence, it demonstrates modern Android development practices, including **Multi-module Clean Architecture**, **Jetpack Compose**, and **Offline-first capabilities**.

## 🚀 Key Features

*   **Inventory Management**: Full CRUD operations for products with support for weight units, pricing, and automated stock threshold alerts.
*   **Bulk Import**: Integrated CSV parser to allow users to upload large product datasets quickly via `ActivityResultContracts`.
*   **Voice Search**: Hands-free product discovery using Android's `RecognizerIntent`.
*   **Sales & Checkout**: A seamless transaction flow that auto-calculates totals and updates stock levels in real-time.
*   **Secure Authentication**: Robust user onboarding via **Firebase Auth**, supporting both Google Sign-In and Phone OTP verification.
*   **Dynamic Dashboard**: High-level overview of business health with real-time stock alerts (Low Stock/Out of Stock).
*   **Neumorphic Design**: A modern, custom-built UI using Jetpack Compose with specialized neumorphic components for a unique user experience.

## 🛠 Tech Stack

*   **Language**: Kotlin (100%)
*   **UI Framework**: Jetpack Compose (Declarative UI)
*   **Architecture**: Multi-module Clean Architecture with MVVM
*   **Dependency Injection**: Hilt (Dagger)
*   **Local Database**: Room (Offline-first support)
*   **Backend**: Firebase (Firestore for cloud sync, Firebase Auth)
*   **Asynchronous Flow**: Kotlin Coroutines & StateFlow
*   **Build System**: Gradle Kotlin DSL with Version Catalogs (`libs.versions.toml`)

## 🏗 Architecture Overview

The project follows a modularized approach to ensure scalability, testability, and separation of concerns:

*   **`:app`**: The entry point, orchestrating navigation and global DI setup.
*   **`:feature:*`**: Isolated functional modules (Dashboard, Products, Transactions) to reduce build times and improve code ownership.
*   **`:core:data`**: The repository layer, managing data sources between Firestore and Room.
*   **`:core:database`**: Local persistence layer using Room.
*   **`:core:designsystem`**: Centralized UI components, themes, and design tokens to ensure visual consistency.
*   **`:core:authentication`**: Encapsulated logic for user identity and security.

## 📈 Technical Highlights for Recruiters

*   **Circular Dependency Resolution**: Successfully refactored the project from a monolithic factory pattern to Hilt DI to break dependency cycles and improve modularity.
*   **Reactive UI**: leveraged `StateFlow` and `collectAsStateWithLifecycle` for efficient, lifecycle-aware UI updates.
*   **Custom Components**: Developed reusable Compose UI components like `NeumorphicTextField` and `PriceField` to maintain a consistent design language.
*   **Efficient Data Handling**: Implemented a synchronization strategy between Firebase Firestore and local Room DB to provide a fast, offline-available experience.

---

**Bazaar** is a testament to writing maintainable, scalable, and high-performance Android applications using the latest industry standards.

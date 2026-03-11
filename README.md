

# Bazaar - Modern Inventory & Sales Management

Bazaar is a modern, high-performance Android marketplace application designed with a focus on **Clean Architecture**, **Offline-first reliability**, and a seamless user experience. It serves as a robust implementation of contemporary Android development standards.

## 🚀 Key Features

*   **Dynamic Catalog**: Real-time product browsing with categorized navigation and state-aware search.
*   **Offline-First Sync**: Integrated local single source of truth (SSOT) to ensure seamless performance regardless of connectivity.
*   **Reactive State Management**: Fully declarative UI that responds instantly to data changes.
*   **Streamlined Checkout**: Optimized cart logic and order processing flow.
*   **Modern UI/UX**: A professional, minimalist aesthetic built for high-speed user interactions.
*   **Neumorphic Design**: A modern, custom-built UI using Jetpack Compose with specialized neumorphic components for a unique user experience.

## 🛠 Tech Stack

*   **Language**: Kotlin (100%)
*   **UI Framework**: Jetpack Compose (Declarative UI)
*   **Architecture**: Multi-module Clean Architecture with MVVM
*   **Dependency Injection**: Hilt (Dagger)
*   **Local Database**: Room (Robust offline caching and data integrity)
*   **Backend**: Firebase (Firestore for cloud sync, Firebase Auth)
*   **Asynchronous Logic**: Coroutines & Flow (Structured concurrency for non-blocking reactive streams)

## 🏗 Architecture Overview

The project follows the SOLID principles and Clean Architecture to ensure the codebase remains maintainable and testable:

*   **`:core:data`**: Handles API communication and Room database operations via the Repository pattern.
*   **`:feature:*`**: Isolated functional modules (Dashboard, Products, Transactions) to reduce build times and improve code ownership. Utilizes ViewModels to expose state to Compose-based screens, ensuring a unidirectional data flow (UDF).
*   **`:core:designsystem`**: Centralized UI components, themes, and design tokens to ensure visual consistency.

## 📈 Technical Highlights

*   **Reactive UI**: leveraged `StateFlow` and `collectAsStateWithLifecycle` for efficient, lifecycle-aware UI updates.
*   **Custom Components**: Developed reusable Compose UI components like `NeumorphicTextField` and `PriceField` to maintain a consistent design language.
*   **Offline-First Data Architecture:**: Engineered a robust Single Source of Truth (SSOT) using Room DB, ensuring immediate UI responsiveness and full offline availability. Background data integrity is maintained via WorkManager, which orchestrates reliable, constraint-aware synchronization between Firebase Firestore and the local cache.

---

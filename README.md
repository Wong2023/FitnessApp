# 🏋️ FitnessApp

A simple, state-driven **workout planning application** built using **Kotlin** and **Jetpack Compose**. Users can create training programs, add/edit exercises within them, and manage the app's dark mode setting.

## ✨ Features

* ✅ **Program Management (CRUD):** Create, view, edit, and delete workout programs (days).
* ✅ **Exercise Management (CRUD):** Add, edit, and delete exercises within each specific program.
* ✅ **Theme Toggling:** Supports **Material 3** theming with a toggle for **Light/Dark Mode**.
* ✅ **Local Persistence:** All programs and exercises are saved locally to survive app restarts.

---

## 🏗️ Tech Stack & Architecture

This project was intentionally built using core Compose features for navigation and state management, providing a clear demonstration of architectural fundamentals.

| Category | Component | Detail |
| :--- | :--- | :--- |
| **Platform** | Android (API 24+) | **Kotlin** |
| **UI Toolkit** | **Jetpack Compose** | Modern, declarative UI approach. |
| **Theming** | **Material 3** | Latest design components and dynamic colors support. |
| **Architecture** | **State-Driven (Unidirectional Data Flow)** | All application state (data, navigation) is managed centrally in `FitnessAppRoot`. |
| **Navigation** | **Custom State Navigation** | Uses a `sealed class Screen` state variable instead of the official Navigation Compose. |
| **Storage** | **`SharedPreferences` + JSON** | Data is manually serialized to **JSON strings** for lightweight local persistence. |

---

## 📸 Screenshots





---

## 🚀 How to Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YourUsername/FitnessApp.git](https://github.com/YourUsername/FitnessApp.git)
    ```
2.  **Open in Android Studio:** Open the cloned directory in **Android Studio** (recommended latest stable version).
3.  **Run:** Select a target and run the application on an emulator or a physical device (**API 26+** is recommended for Material 3 dynamic features).

---

## 📜 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.










\FitnessApp
A simple workout planning app built with Jetpack Compose and Material 3. Users can create training programs (days), add exercises, edit them, and delete them.


Features:

Jetpack Compose UI

Material 3 theme with light/dark mode

Create, edit, and delete workout programs

Add, edit, and delete exercises inside each program

Basic navigation implemented via Compose state

Local data persistence using SharedPreferences with JSON



Architecture:

UI: Jetpack Compose + Material 3

Navigation: custom state-based navigation (no Navigation Compose, no fragments)

State management: remember, mutableStateOf, mutableStateListOf

Storage: SharedPreferences (no Room, no DataStore)




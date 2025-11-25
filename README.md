# 🏋️ FitnessApp

A lightweight and robust **workout planning application** built using **Kotlin** and **Jetpack Compose**. It provides full control over creating, managing, and persisting personalized daily training programs and their corresponding exercises.

---

## ✨ Features

* ✅ **Jetpack Compose UI:** A modern, declarative user interface built entirely with Compose.
* ✅ **Material 3 Theming:** Full support for the latest Material design language, including a **Light/Dark Mode toggle**.
* ✅ **Program CRUD:** Complete functionality (Create, Read, Update, Delete) for workout programs (days).
* ✅ **Exercise CRUD:** Complete functionality (Create, Read, Update, Delete) for exercises within each program.
* ✅ **Basic State Navigation:** Uses custom, app-level state management for all navigation.
* ✅ **Local Persistence:** Data is reliably saved across app restarts.

---

## 🏗️ Technical Architecture

This project emphasizes a pure **Jetpack Compose** approach by implementing core architectural needs without external libraries like Navigation Compose or Room, focusing on fundamental state management techniques.

| Category | Component | Detail |
| :--- | :--- | :--- |
| **Platform** | Android (Kotlin) | Target API 24+ |
| **UI Toolkit** | **Jetpack Compose** | Utilizes core Compose and Material 3 components. |
| **Architecture** | **State-Driven (UDF)** | **Unidirectional Data Flow** managed centrally in `FitnessAppRoot`. |
| **State Management** | `remember`, `mutableStateOf`, `mutableStateListOf` | Used directly for all mutable application state. |
| **Navigation** | **Custom State-Based** | Relies on changing a single `sealed class Screen` state variable. |
| **Storage** | **`SharedPreferences` + JSON** | Manual serialization/deserialization of objects to JSON strings for persistence. |

---

## 📸 Screenshots





---

## 🚀 How to Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/YourUsername/FitnessApp.git](https://github.com/YourUsername/FitnessApp.git)
    ```
2.  **Open in Android Studio:** Open the cloned project in **Android Studio** (Koala+ recommended).
3.  **Run:** Build and run the application on an emulator or a physical device (**API 26+** recommended for full Material 3 feature support).

---

## 📜 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

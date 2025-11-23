FitnessApp
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

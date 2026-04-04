# Heart Rate Monitor

Check your heart rate instantly using your phone's camera. This app uses advanced image processing to detect pulse variations from your fingertip.

## Screenshots

<p align="center">
  <img src="https://github.com/rockingbapidas/hearrate/raw/master/screenshots/Screenshot_2020-05-22-13-36-22-286_com.bapidas.heartrate.jpg" height="400" alt="">
  <img src="https://github.com/rockingbapidas/hearrate/raw/master/screenshots/Screenshot_2020-05-22-13-36-28-229_com.bapidas.heartrate.jpg" height="400" alt="">
  <img src="https://github.com/rockingbapidas/hearrate/raw/master/screenshots/Screenshot_2020-05-22-13-36-33-574_com.bapidas.heartrate.jpg" height="400" alt="">
  <img src="https://github.com/rockingbapidas/hearrate/raw/master/screenshots/Screenshot_2020-05-22-13-36-48-528_com.bapidas.heartrate.jpg" height="400" alt="">
</p>

## Architecture

The project follows the **Clean Architecture** pattern combined with **MVVM (Model-View-ViewModel)**.

### Layers

1.  **UI Layer (Jetpack Compose):**
    *   **Screens:** Composable functions defining the UI for Heart Rate measurement and History.
    *   **ViewModels:** Manage UI state and interact with the Repository using Kotlin Coroutines and StateFlow.

2.  **Domain/Core Layer:**
    *   **HeartRate Engine:** Core logic for pulse detection and timing.
    *   **Camera Support:** Interface-based camera handling (abstracting CameraX implementation).
    *   **Processing:** Image processing logic to extract red-channel average values from camera frames.

3.  **Data Layer:**
    *   **Repository:** Orchestrates data flow between the local database and the UI.
    *   **Room Database:** Local persistence for storing heart rate measurement history.

### Tech Stack

*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Modern Declarative UI)
*   **Dependency Injection:** Hilt
*   **Asynchronous Programming:** Kotlin Coroutines & Flow
*   **Database:** Room
*   **Camera API:** CameraX (Modern, lifecycle-aware camera handling)
*   **Unit Testing:** JUnit 4, Mockito, Turbine (for Flow testing)

## How it Works

1.  **Image Capture:** The app uses CameraX to capture low-resolution frames at a high frequency.
2.  **Processing:** Each frame is processed to calculate the average "redness" of the fingertip.
3.  **Pulse Detection:** By tracking the subtle variations in the red channel over time (caused by blood flow), the app calculates the Beats Per Minute (BPM).
4.  **Feedback:** Real-time feedback is provided via a progress indicator and live BPM updates.

## Getting Started

1.  Clone the repository.
2.  Open the project in Android Studio (Ladybug or newer recommended).
3.  Build and run the `app` module on a physical device (Camera is required).

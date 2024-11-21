Weather Application
Overview
The Weather Application is a feature-rich and intuitive Android application that provides users with real-time weather updates. Built with Kotlin, Jetpack Compose, and Hilt, this application integrates with a robust weather API to fetch and display accurate weather details for any location. The application also follows modern Android development best practices, including MVVM architecture, reactive programming with Kotlin Flows, and dependency injection using Hilt.

Features
Real-Time Weather Updates: Displays the current temperature, weather conditions, and detailed descriptions.
Error Handling: Comprehensive error screens for network failures or API issues.
Retry Mechanism: Allows users to refresh and fetch weather updates seamlessly.
Clean UI: A user-centric design powered by Jetpack Compose ensures a fluid and modern UI experience.
Offline-Ready: Graceful handling of offline scenarios with meaningful feedback.
Architecture: Utilizes MVVM architecture for separation of concerns and maintainability.
Technology Stack
Programming Language: Kotlin
Architecture: MVVM (Model-View-ViewModel)
UI Framework: Jetpack Compose
Dependency Injection: Hilt (Dagger)
Networking: Retrofit with GSON for JSON parsing
Coroutines: For asynchronous operations
State Management: Kotlin Flows and MutableStateFlows
Modules

1. UI Layer
MainActivity: Entry point of the application. Handles navigation between different states like loading, success, and error.
WeatherScreen: Displays weather details including temperature and description.
ErrorScreen: Handles error scenarios like network failures or API errors, providing a retry mechanism.

3. ViewModel Layer
WeatherViewModel:
Manages app logic and state.
Fetches weather data asynchronously using Coroutines.
Exposes weather state as a StateFlow to the UI.

5. Data Layer
Repository: Acts as a single source of truth for fetching data from the API.
API Service: Defines endpoints for the weather API.
RetrofitInstance: Configures the Retrofit client.

7. Dependency Injection
AppModule:
Provides instances of Retrofit, WeatherApiService, and WeatherRepository.
Ensures Singleton instances across the app lifecycle.
API Integration
Weather API
The application uses a weather API to fetch real-time weather data. Key details:



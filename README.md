Project Outline: Weather App
1. Key Features
Search Screen: Allows users to input a U.S. city name to fetch weather details.
Location Access: Automatically fetch weather for the user's current location if permissions are granted.
Weather Icon Display: Use OpenWeatherMap icons for appropriate weather conditions.
Last City Cache: Auto-load weather details for the last searched city upon app launch.
Error Handling: Graceful handling of API errors, location denial, or invalid city input.
Image Caching: Cache weather icons to minimize repeated downloads.
2. Project Structure
Architecture: Use MVVM with clear separation of concerns:

Model: Manages data from the API.
ViewModel: Handles business logic, connects Model and View.
View: Displays UI and listens for user input.
3. Technologies
Coding Language: Kotlin (add Java for any specific components if needed).
Architecture: MVVM.
Network Library: Retrofit for API communication.
Image Loading: Glide or Coil for caching icons.
Concurrency: Kotlin Coroutines.
Dependency Injection: Hilt for easy DI management.
UI: Jetpack Compose (preferred) or XML for layouts.
Navigation: Jetpack Navigation.
Testing: JUnit for unit tests, Espresso/Mockito for UI tests.
4. Key Development Steps
Setup OpenWeatherMap API:
Register for an API key.
Understand API endpoints for fetching weather and geocoding.
Implement Location Services:
Ask for location permission using Android’s FusedLocationProviderClient.
Fetch weather for the current location if permission is granted.
Develop Search Screen:
Input field for city names.
Integrate Retrofit to fetch weather data.
Display weather details with icons.
Cache Last Searched City:
Use SharedPreferences or DataStore to save the last searched city.
Load its data on app launch.
Implement Weather Icon Caching:
Use Glide or Coil for efficient caching.
Error Handling:
Handle edge cases (e.g., invalid city names, API rate limits).
Show appropriate messages for errors.
Testing:
Write unit tests for ViewModel using JUnit.
Add UI tests for key flows using Espresso.
5. Documentation
Provide a README file with the following:

Overview: Brief app description and features.
Setup Instructions: Steps to clone, build, and run the app.
API Key Setup: Instructions to add an API key.
Technologies Used: List all libraries and tools with their purposes.
Usage: How to use the app (screens and functionalities).
Known Issues: Any limitations or areas for improvement.
Future Enhancements: Suggested improvements or next steps.
6. Deliverables
GitLab Repository: Public repository with:
Complete source code.
Proper folder structure (e.g., model, viewmodel, view, utils).
Documentation (README.md).
App Features: Ensure all requirements are implemented and tested.

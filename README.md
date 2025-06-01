🌤️ Weather App - Material Design
A modern and user-friendly Android weather application providing real-time weather data and 10-day forecasts.

📱 Features
Main Features
Current Weather: Real-time temperature, weather conditions, and location info

10-Day Forecast: Detailed weekly weather forecast

Smart Search: City search with auto-complete suggestions

Material Design: Modern and elegant user interface

Responsive Design: Adaptable to all screen sizes

Technical Features
MVVM Architecture: Clean code structure with clear separation of concerns

LiveData & ViewModel: Lifecycle-aware components and reactive programming

Navigation Component: Fragment-based navigation

Repository Pattern: Abstracted API layer

Error Handling: Comprehensive error management and user feedback


🛠️ Technologies Used
Core Android
Kotlin: Primary programming language

Android Architecture Components: ViewModel, LiveData, Navigation

Fragment: Modular UI structure

ViewBinding: Type-safe view references


Network & API

Retrofit: REST API client
Gson: JSON parsing
WeatherAPI: Weather data provider
Coroutines: Asynchronous programming

UI/UX
Material Design Components: Modern UI elements
ConstraintLayout: Flexible layout system
RecyclerView: Efficient list rendering
Glide: Image loading and caching
CardView: Material card design

Others
Custom Fonts: Poppins font family

Gradient Backgrounds: Custom background designs

Shimmer Effects: Loading animations

🏗️ Project Architecture

app/
├── MainActivity.kt                 # Main activity
├── fragments/
│   ├── MainScreenFragment.kt      # Main screen
│   └── ConditionWeekFragment.kt   # Weekly forecast
├── viewmodel/
│   └── WeatherViewModel.kt        # Business logic
├── network/
│   ├── WeatherApiService.kt       # API interface
│   ├── RetrofitClient.kt          # Network client
│   └── WeatherResponse.kt         # Data models
├── adapters/
│   ├── ForecastAdapter.kt         # Forecast list
│   └── SearchSuggestionsAdapter.kt # Search suggestions
└── ui/
    └── SearchSuggestion/          # Search UI components
    
🌟 Key Functionalities

1. Real-Time Weather
Live temperature display
Weather icons and descriptions
Location details (city, country)

2. Smart Search System

Live search suggestions
Debounce mechanism (300ms)
Maximum 5 suggestions displayed
Auto-complete support

4. Weekly Forecast

10-day detailed weather predictions
Max/min temperature for each day
Daily weather icons
Date formatting

6. Error Handling

Network connectivity checks
API key validation
User-friendly error messages
Graceful degradation for seamless experience

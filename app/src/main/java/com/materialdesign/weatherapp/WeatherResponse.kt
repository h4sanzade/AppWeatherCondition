package com.materialdesign.weatherapp

data class WeatherResponse(
    val location: Location,
    val current: CurrentWeather
)

data class Location(
    val name: String,
    val country: String
)

data class CurrentWeather(
    val temp_c: Double,
    val condition: WeatherCondition
)

data class WeatherCondition(
    val text: String,
    val icon: String
)

data class ForecastResponse(
    val location: Location,
    val current: CurrentWeather,
    val forecast: Forecast
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day
)

data class Day(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    val condition: WeatherCondition
)
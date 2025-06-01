package com.materialdesign.weatherapp.SearchSuggestion

data class SearchSuggestion(
    val id: Long,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
) {
    fun getDisplayText(): String {
        return if (region.isNotEmpty() && region != name) {
            "$name, $region, $country"
        } else {
            "$name, $country"
        }
    }
}
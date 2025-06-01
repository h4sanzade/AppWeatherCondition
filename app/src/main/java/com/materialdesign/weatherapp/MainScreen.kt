package com.materialdesign.weatherapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.materialdesign.weatherapp.databinding.FragmentMainScreenBinding

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var suggestionsAdapter: SearchSuggestionsAdapter
    private var currentLocation: String = "Baku" // Default location

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchSuggestionsList()
        setupClickListeners()
        setupSearchFunctionality()
        observeViewModel()

        // Load default location weather
        viewModel.fetchCurrentWeather(currentLocation)
    }

    private fun setupSearchSuggestionsList() {
        suggestionsAdapter = SearchSuggestionsAdapter { suggestion ->
            // When a suggestion is clicked
            currentLocation = suggestion.name
            binding.searchEditText.setText(suggestion.getDisplayText())
            binding.searchEditText.clearFocus()
            hideSuggestions()
            viewModel.fetchCurrentWeather(suggestion.name)
        }

        binding.suggestionsRecyclerView.apply {
            adapter = suggestionsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupClickListeners() {
        binding.goToListButton.setOnClickListener {
            try {
                // Pass current location to the next fragment
                val bundle = Bundle().apply {
                    putString("location", currentLocation)
                }
                findNavController().navigate(R.id.action_main_to_week, bundle)
            } catch (e: Exception) {
                Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Hide suggestions when clicking outside
        binding.root.setOnClickListener {
            hideSuggestions()
            binding.searchEditText.clearFocus()
        }
    }

    private fun setupSearchFunctionality() {
        binding.searchButton.setOnClickListener {
            val searchQuery = binding.searchEditText.text.toString().trim()
            if (searchQuery.isNotEmpty()) {
                currentLocation = searchQuery
                viewModel.fetchCurrentWeather(searchQuery)
                binding.searchEditText.clearFocus()
                hideSuggestions()
            } else {
                Toast.makeText(context, "Please enter a location", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle search on Enter key press
        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            binding.searchButton.performClick()
            true
        }

        // Add TextWatcher for real-time search suggestions
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 2) {
                    viewModel.searchLocations(query)
                    showSuggestions()
                } else {
                    viewModel.clearSearchSuggestions()
                    hideSuggestions()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Show suggestions when EditText gets focus
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.toString().length >= 2) {
                showSuggestions()
            } else if (!hasFocus) {
                // Add small delay to allow clicking on suggestions
                binding.root.postDelayed({
                    hideSuggestions()
                }, 150)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentWeather.observe(viewLifecycleOwner) { weatherResponse ->
            updateUI(weatherResponse)
        }

        viewModel.searchSuggestions.observe(viewLifecycleOwner) { suggestions ->
            suggestionsAdapter.submitList(suggestions)
            if (suggestions.isNotEmpty() && binding.searchEditText.hasFocus()) {
                showSuggestions()
            } else if (suggestions.isEmpty()) {
                hideSuggestions()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.searchButton.isEnabled = !isLoading
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.searchProgressBar.visibility = if (isSearching) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                showError(error)
            }
        }
    }

    private fun showSuggestions() {
        binding.suggestionsContainer.visibility = View.VISIBLE
    }

    private fun hideSuggestions() {
        binding.suggestionsContainer.visibility = View.GONE
    }

    private fun updateUI(weatherResponse: WeatherResponse) {
        val locationText = "${weatherResponse.location.name}, ${weatherResponse.location.country}"
        val weatherText = "${weatherResponse.current.temp_c.toInt()}°C\n${weatherResponse.current.condition.text}"

        binding.locationTextView.text = locationText
        binding.weatherTextView.text = weatherText

        // Update current location for future use
        currentLocation = weatherResponse.location.name
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        binding.locationTextView.text = "Location Not Found"
        binding.weatherTextView.text = "No Data"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
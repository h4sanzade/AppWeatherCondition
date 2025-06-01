package com.materialdesign.weatherapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
    private var currentLocation: String = "Baku"

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


        viewModel.fetchCurrentWeather(currentLocation)
    }

    private fun setupSearchSuggestionsList() {
        suggestionsAdapter = SearchSuggestionsAdapter { suggestion ->

            currentLocation = suggestion.name
            binding.searchEditText.setText(suggestion.getDisplayText())
            binding.searchEditText.clearFocus()
            hideSuggestions()
            viewModel.fetchCurrentWeather(suggestion.name)
            viewModel.clearSearchSuggestions()
        }

        binding.suggestionsRecyclerView.apply {
            adapter = suggestionsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupClickListeners() {
        binding.goToListButton.setOnClickListener {
            try {
                val bundle = Bundle().apply {
                    putString("location", currentLocation)
                }
                findNavController().navigate(R.id.action_main_to_week, bundle)
            } catch (e: Exception) {
                Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

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
                viewModel.clearSearchSuggestions()
            } else {
                Toast.makeText(context, "Please enter a location", Toast.LENGTH_SHORT).show()
            }
        }


        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            binding.searchButton.performClick()
            true
        }


        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 2) {
                    viewModel.searchLocations(query)
                } else {
                    viewModel.clearSearchSuggestions()
                    hideSuggestions()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.toString().length >= 2) {
                if (suggestionsAdapter.itemCount > 0) {
                    showSuggestions()
                }
            } else if (!hasFocus) {
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
            } else {
                hideSuggestions()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            binding.searchButton.isEnabled = !isLoading
        }

        viewModel.isSearching.observe(viewLifecycleOwner) { isSearching ->
            binding.searchProgressBar.isVisible = isSearching
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
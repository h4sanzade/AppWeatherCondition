package com.materialdesign.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.materialdesign.weatherapp.databinding.FragmentMainScreenBinding

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()

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

        setupClickListeners()
        observeViewModel()

        viewModel.fetchCurrentWeather("Baku")
    }

    private fun setupClickListeners() {
        binding.goToListButton.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_main_to_week)
            } catch (e: Exception) {
                Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentWeather.observe(viewLifecycleOwner) { weatherResponse ->
            updateUI(weatherResponse)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                showError(error)
            }
        }
    }

    private fun updateUI(weatherResponse: WeatherResponse) {
        val locationText = "${weatherResponse.location.name}, ${weatherResponse.location.country}"
        val weatherText = "${weatherResponse.current.temp_c.toInt()}°C\n${weatherResponse.current.condition.text}"

        binding.locationTextView.text = locationText
        binding.weatherTextView.text = weatherText
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
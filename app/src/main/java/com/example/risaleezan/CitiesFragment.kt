package com.example.risaleezan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.util.Locale

class CitiesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var countriesAdapter: CountriesAdapter
    private lateinit var searchView: SearchView

    private var allCountriesList = listOf<Pair<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCountries)
        progressBar = view.findViewById(R.id.progressBar)
        searchView = view.findViewById(R.id.searchViewCountries)

        setupRecyclerView()
        setupSearchView()
        loadCountriesFromProvider()
    }

    private fun loadCountriesFromProvider() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            // Veri sağlayıcısını yükle (eğer daha önce yüklenmediyse)
            CityDataProvider.loadCities(requireContext().applicationContext)
            // Tüm ülkeleri al
            allCountriesList = CityDataProvider.getAllCountries()
            // Listeyi adaptöre gönder
            countriesAdapter.updateData(allCountriesList.map { it.first })
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        countriesAdapter = CountriesAdapter(emptyList()) { selectedCountryName ->
            val countryPair = allCountriesList.find { it.first == selectedCountryName }
            countryPair?.let { (name, code) ->
                val action = CitiesFragmentDirections.actionCitiesFragmentToCityListFragment(code, name)
                findNavController().navigate(action)
            }
        }
        recyclerView.adapter = countriesAdapter
    }

    private fun setupSearchView() {
        searchView.setOnClickListener {
            searchView.isIconified = false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterCountries(newText)
                return true
            }
        })
    }

    private fun filterCountries(query: String?) {
        val originalList = allCountriesList.map { it.first }
        if (query.isNullOrEmpty()) {
            countriesAdapter.updateData(originalList)
        } else {
            val filteredList = originalList.filter { countryName ->
                countryName.lowercase(Locale("tr")).contains(query.lowercase(Locale("tr")))
            }
            countriesAdapter.updateData(filteredList)
        }
    }
}
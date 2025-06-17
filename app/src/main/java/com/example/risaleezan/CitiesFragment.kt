package com.example.risaleezan // Paket adını kontrol et

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.util.Locale

class CitiesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var countriesAdapter: CountriesAdapter
    private lateinit var searchView: SearchView
    private var allCountriesList = listOf<String>()

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
        fetchCountries()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        countriesAdapter = CountriesAdapter(emptyList()) { selectedCountry ->
            val action = CitiesFragmentDirections.actionCitiesFragmentToCityListFragment(selectedCountry)
            findNavController().navigate(action)
        }
        recyclerView.adapter = countriesAdapter
    }

    private fun fetchCountries() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        countriesAdapter.updateData(emptyList())

        val url = "https://countriesnow.space/api/v0.1/countries"
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val data = response.getJSONArray("data")
                val countryList = mutableListOf<String>()
                for (i in 0 until data.length()) {
                    countryList.add(data.getJSONObject(i).getString("country"))
                }

                allCountriesList = countryList.sorted()
                countriesAdapter.updateData(allCountriesList)

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            },
            { error ->
                Toast.makeText(requireContext(), "Ülkeler yüklenemedi.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        )

        jsonObjectRequest.setShouldCache(false)
        queue.add(jsonObjectRequest)
    }

    // --- KLAVYE SORUNU İÇİN GÜNCELLENEN FONKSİYON ---
    private fun setupSearchView() {
        searchView.setOnClickListener {
            // Arama kutusuna tıklandığında odaklanmasını sağla
            searchView.isIconified = false
        }

        // Arama kutusu odaklandığında (içine girildiğinde) klavyeyi aç
        searchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // Gecikmeli bir şekilde klavyeyi gösterme komutu veriyoruz,
                // çünkü arayüzün odaklanmaya hazır olması zaman alabilir.
                view.postDelayed({
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(view.findFocus(), InputMethodManager.SHOW_IMPLICIT)
                }, 200) // 200 milisaniye gecikme
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCountries(newText)
                return true
            }
        })
    }

    private fun filterCountries(query: String?) {
        if (query.isNullOrEmpty()) {
            countriesAdapter.updateData(allCountriesList)
        } else {
            val filteredList = allCountriesList.filter { country ->
                country.lowercase(Locale.getDefault())
                    .contains(query.lowercase(Locale.getDefault()))
            }
            countriesAdapter.updateData(filteredList)
        }
    }
}
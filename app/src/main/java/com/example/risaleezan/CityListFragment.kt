package com.example.risaleezan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.util.Locale

class CityListFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val args: CityListFragmentArgs by navArgs()
    private lateinit var cityListAdapter: CityListAdapter
    private var originalItemList: List<ListItem> = listOf()
    private var allCitiesForCountry: List<CityInfo> = listOf()

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var editTextSearch: EditText
    private val TAG = "CityListFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_city_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated çağrıldı. Gelen ülke kodu: ${args.countryCode}")

        recyclerView = view.findViewById(R.id.recyclerViewCitiesList)
        progressBar = view.findViewById(R.id.progressBar)
        editTextSearch = view.findViewById(R.id.editTextSearchCities)

        setupRecyclerView()
        setupSearch()
        loadCitiesFromAsset()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cityListAdapter = CityListAdapter(emptyList()) { selectedCityName ->
            // Seçilen şehrin tüm bilgilerini bul
            val selectedCityInfo = allCitiesForCountry.find { it.name == selectedCityName }
            if (selectedCityInfo != null) {
                // ViewModel'i koordinatlarla birlikte güncelle
                sharedViewModel.selectLocation(
                    SelectedLocation(
                        city = selectedCityInfo.name,
                        country = args.countryName, // Türkçe ismi kullanmaya devam et
                        latitude = selectedCityInfo.latitude,
                        longitude = selectedCityInfo.longitude
                    )
                )
                Toast.makeText(requireContext(), "${selectedCityInfo.name} seçildi", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.namazFragment, false)
            } else {
                Toast.makeText(requireContext(), "$selectedCityName için detay bulunamadı.", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = cityListAdapter
    }

    private fun loadCitiesFromAsset() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            // Veri sağlayıcısını yükle (eğer yüklenmediyse)
            CityDataProvider.loadCities(requireContext())
            // Ülkeye göre şehirleri al
            allCitiesForCountry = CityDataProvider.getCitiesByCountry(args.countryCode)

            if (allCitiesForCountry.isNotEmpty()) {
                val cityNames = allCitiesForCountry.map { it.name }.distinct().sorted()
                originalItemList = createGroupedList(cityNames)
                cityListAdapter.filterList(originalItemList)
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "${args.countryName} için şehir bulunamadı.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }


    private fun createGroupedList(cities: List<String>): List<ListItem> {
        val groupedList = mutableListOf<ListItem>()
        var currentHeader: String? = null
        for (city in cities) {
            if (city.isEmpty()) continue
            val firstLetter = city.first().uppercaseChar().toString()
            if (firstLetter != currentHeader) {
                currentHeader = firstLetter
                groupedList.add(ListItem.Header(currentHeader))
            }
            groupedList.add(ListItem.City(city))
        }
        return groupedList
    }

    private fun setupSearch() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(text: String?) {
        val filteredItems = mutableListOf<ListItem>()
        if (text.isNullOrEmpty()) {
            filteredItems.addAll(originalItemList)
        } else {
            val searchText = text.lowercase(Locale.getDefault())
            for (item in originalItemList) {
                if (item is ListItem.City && item.name.lowercase(Locale.getDefault()).contains(searchText)) {
                    filteredItems.add(item)
                }
            }
        }
        cityListAdapter.filterList(filteredItems)
    }
}
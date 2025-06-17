package com.example.risaleezan // Paket adını kontrol et

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.Locale

class CityListFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val args: CityListFragmentArgs by navArgs()
    private lateinit var cityListAdapter: CityListAdapter
    private var originalItemList: List<ListItem> = listOf()

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
        Log.d(TAG, "onViewCreated çağrıldı. Gelen ülke: ${args.countryName}")

        recyclerView = view.findViewById(R.id.recyclerViewCitiesList)
        progressBar = view.findViewById(R.id.progressBar)
        editTextSearch = view.findViewById(R.id.editTextSearchCities)

        setupRecyclerView()
        setupSearch() // Artık bu fonksiyon aşağıda tanımlı olduğu için hata vermeyecek
        fetchCitiesForCountry(args.countryName)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        cityListAdapter = CityListAdapter(emptyList()) { selectedCity ->
            sharedViewModel.selectLocation(SelectedLocation(selectedCity, args.countryName))
            Toast.makeText(requireContext(), "$selectedCity, ${args.countryName} seçildi", Toast.LENGTH_SHORT).show()

            // --- DÜZELTİLEN SATIR ---
            // "Namaz" ekranına kadar aradaki tüm ekranları kapatarak geri dön.
            findNavController().popBackStack(R.id.namazFragment, false)
        }
        recyclerView.adapter = cityListAdapter
    }

    private fun fetchCitiesForCountry(countryNameInTurkish: String) {
        cityListAdapter.filterList(emptyList())
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        editTextSearch.setText("")

        val apiCountryName = when (countryNameInTurkish) {
            "Türkiye" -> "Turkey"
            "Almanya" -> "Germany"
            "Fransa" -> "France"
            "İngiltere" -> "United Kingdom"
            "Suudi Arabistan" -> "Saudi Arabia"
            else -> countryNameInTurkish
        }
        Log.d(TAG, "API'ye gönderilen ülke: '$apiCountryName'")

        val url = "https://countriesnow.space/api/v0.1/countries/cities"
        val requestBody = JSONObject().apply {
            put("country", apiCountryName)
        }
        val queue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, requestBody,
            { response ->
                if (response.getBoolean("error")) {
                    Toast.makeText(requireContext(), response.getString("msg"), Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    return@JsonObjectRequest
                }

                val cities = response.getJSONArray("data")
                val cityList = mutableListOf<String>()
                for (i in 0 until cities.length()) {
                    cityList.add(cities.getString(i))
                }

                originalItemList = createGroupedList(cityList.sorted())
                cityListAdapter.filterList(originalItemList)

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            },
            { error ->
                Toast.makeText(requireContext(), "$countryNameInTurkish için şehirler bulunamadı.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        )
        jsonObjectRequest.setShouldCache(false)
        queue.add(jsonObjectRequest)
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

    // --- EKSİK OLAN FONKSİYON 1 ---
    private fun setupSearch() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // --- EKSİK OLAN FONKSİYON 2 ---
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
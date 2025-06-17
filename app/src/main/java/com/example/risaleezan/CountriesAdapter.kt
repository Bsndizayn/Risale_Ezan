package com.example.risaleezan // Paket adını kontrol et

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CountriesAdapter(
    private var countries: List<String>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryName: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]
        holder.countryName.text = country
        (holder.countryName as TextView).setTextColor(holder.itemView.context.getColor(R.color.off_white))
        (holder.countryName as TextView).textSize = 20f

        holder.itemView.setOnClickListener {
            onItemClicked(country)
        }
    }

    override fun getItemCount() = countries.size

    fun updateData(newCountries: List<String>) {
        countries = newCountries
        notifyDataSetChanged()
    }
}
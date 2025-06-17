package com.example.risaleezan // Paket adını kontrol et

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Veri modelimiz: Her eleman ya bir başlık ya da bir şehir olacak
sealed class ListItem {
    data class Header(val letter: String) : ListItem()
    data class City(val name: String) : ListItem()
}

class CityListAdapter(
    private var items: List<ListItem>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_CITY = 1

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerText: TextView = view.findViewById(R.id.textViewHeader)
    }

    class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityName: TextView = view.findViewById(android.R.id.text1)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.Header -> TYPE_HEADER
            is ListItem.City -> TYPE_CITY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            CityViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListItem.Header -> (holder as HeaderViewHolder).headerText.text = item.letter
            is ListItem.City -> {
                val cityHolder = holder as CityViewHolder
                cityHolder.cityName.text = item.name
                (cityHolder.cityName as TextView).setTextColor(holder.itemView.context.getColor(R.color.off_white))
                (cityHolder.cityName as TextView).textSize = 18f
                cityHolder.itemView.setOnClickListener {
                    onItemClicked(item.name)
                }
            }
        }
    }

    override fun getItemCount() = items.size

    fun filterList(filteredList: List<ListItem>) {
        items = filteredList
        notifyDataSetChanged()
    }
}
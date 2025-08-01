package com.example.risaleezan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SoundAdapter(
    private val sounds: List<Pair<String, Int>>,
    initialSelectedSoundId: Int,
    private val onItemClicked: (Pair<String, Int>) -> Unit
) : RecyclerView.Adapter<SoundAdapter.SoundViewHolder>() {

    private var selectedPosition = sounds.indexOfFirst { it.second == initialSelectedSoundId }

    inner class SoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val soundName: TextView = itemView.findViewById(R.id.textViewSoundName)
        private val radioButton: RadioButton = itemView.findViewById(R.id.radioButtonSound)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val previouslySelected = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previouslySelected)
                    notifyItemChanged(selectedPosition)
                    onItemClicked(sounds[selectedPosition])
                }
            }
        }

        fun bind(sound: Pair<String, Int>) {
            soundName.text = sound.first
            radioButton.isChecked = adapterPosition == selectedPosition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sound_selection_item, parent, false)
        return SoundViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        holder.bind(sounds[position])
    }

    override fun getItemCount() = sounds.size

    fun getSelectedSound(): Pair<String, Int>? {
        return if (selectedPosition != -1 && selectedPosition < sounds.size) {
            sounds[selectedPosition]
        } else {
            null
        }
    }
}
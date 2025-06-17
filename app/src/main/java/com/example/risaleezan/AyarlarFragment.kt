package com.example.risaleezan // Paket adını kontrol et

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class AyarlarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ayarlar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSelectCity = view.findViewById<Button>(R.id.buttonSelectCity)
        buttonSelectCity.setOnClickListener {
            // Navigasyon haritasında tanımladığımız action'ı kullanarak geçiş yap
            findNavController().navigate(R.id.action_ayarlarFragment_to_citiesFragment)
        }
    }
}
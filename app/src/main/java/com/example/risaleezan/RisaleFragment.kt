package com.example.risaleezan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RisaleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_risale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allBooks = listOf(
            // Külliyat (önceki listelerden)
            Book("Sözler", R.drawable.sozler),
            Book("Mektûbat", R.drawable.mektubat),
            Book("Lem'alar", R.drawable.lemalar),
            Book("Şualar", R.drawable.sualar),
            Book("Barla Lahikası", R.drawable.barla),
            Book("Kastamonu Lahikası", R.drawable.kastamonu),
            Book("Emirdağ Lahikası", R.drawable.emirdag),
            Book("İşaratü-l İ'caz", R.drawable.isarat),
            Book("Mesnevi-i Nuriye", R.drawable.mesnevi),
            Book("Sikke-i Tasdik-i Gaybi", R.drawable.sikke),
            Book("Asa-yı Musa", R.drawable.asa),
            Book("Tarihçe-i Hayat", R.drawable.tarihce),
            Book("İman ve Küfür Muvazeneleri", R.drawable.iman_kufur),
            Book("Muhakemat", R.drawable.muhakemat),

            // Küçük Kitaplar (önceki listeden devam)
            Book("MEYVE RİSALESİ", R.drawable.meyve_risalesi),
            Book("KÜÇÜK SÖZLER", R.drawable.kucuk_sozler),
            Book("GENÇLİK REHBERİ", R.drawable.genclik_rehberi),
            Book("YİRMİÜÇÜNCÜ SÖZ", R.drawable.yirmiucuncu_soz),
            Book("HASTALAR RİSALESİ", R.drawable.hastalar_risalesi),
            Book("HAŞİR RİSALESİ", R.drawable.hasir_risalesi),
            Book("AYET-ÜL KÜBRA", R.drawable.ayet_ul_kubra),
            Book("MÜNACAT", R.drawable.munacat),
            Book("OTUZÜÇ PENCERE", R.drawable.otuzuc_pencere),
            Book("İHTİYARLAR RİSALESİ", R.drawable.ihtiyarlar_risalesi),
            Book("HANIMLAR REHBERİ", R.drawable.hanimlar_rehberi),
            Book("HİZMET REHBERİ", R.drawable.hizmet_rehberi),
            Book("İHLAS RİSALESİ", R.drawable.ihlas_risalesi),
            Book("UHUVVET RİSALESİ", R.drawable.uhuvvet_risalesi),
            Book("MUCİZAT-I KUR'ANİYE", R.drawable.mucizat_i_kuraniye),
            Book("MUCİZAT-I AHMEDİYE", R.drawable.mucizat_i_ahmediye),
            Book("NUR ÇEŞMESİ", R.drawable.nur_cesmesi),
            Book("BEDİÜZZAMAN CEVAP VERİYOR", R.drawable.bedi_uz_cevap_veriyor),
            Book("NURUN İLK KAPISI", R.drawable.nurun_ilk_kapisi),
            Book("MİRKAT-ÜS SÜNNET", R.drawable.mirkat_us_sunnet),
            Book("ZÜHRETÜNNUR", R.drawable.zuhretunnur),
            Book("HAKİKAT NURLARI", R.drawable.hakikat_nurlari),
            Book("KONFERANS", R.drawable.konferans),
            Book("EL-HÜCCET-ÜZ ZEHRA", R.drawable.el_huccet_uz_zehra),
            Book("İMAN HAKİKATLARI", R.drawable.iman_hakikatlari),
            Book("HUTBE-İ ŞAMİYE", R.drawable.hutbe_i_samiye),
            Book("RAHMET ŞEFKAT İLAÇLARI", R.drawable.rahmet_sefkat_ilaclari),
            Book("MÜNAZARAT", R.drawable.munazarat),
            Book("MİRAC VE ŞAKKI KAMER", R.drawable.mirac_ve_sakki_kamer),
            Book("RAMAZAN İKTİSAT ŞÜKÜR RİSALESİ", R.drawable.ramazan_iktisat_sukr_risalesi),
            Book("MİFTAH-ÜL İMAN", R.drawable.miftah_ul_iman),
            Book("SUNUHAT-TULUAT-İŞARAT", R.drawable.sunuhat_tuluat_isarat),
            Book("İÇTİHAD RİSALESİ", R.drawable.ictihad_risalesi),
            Book("DİVAN-I HARBİ ÖRFİ", R.drawable.divan_i_harbi_orfi),
            Book("NUR ALEMİNİN BİR ANAHTARI", R.drawable.nur_aleminin_bir_anahtari),
            Book("ENE VE ZERRE RİSALESİ", R.drawable.ene_ve_zerre_risalesi),
            Book("TABİAT RİSALESİ", R.drawable.tabiat_risalesi),
            Book("OTUZUNCU LEM'A", R.drawable.otuzuncu_lem_a)
        )

        // RAF 1: Son Okunanlar Rafı
        val horizontalRecyclerView1 = view.findViewById<RecyclerView>(R.id.horizontalBookRecyclerView1)
        horizontalRecyclerView1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        horizontalRecyclerView1.adapter = BookAdapter(allBooks.subList(0, 7)) // Örnek olarak ilk 5 kitap

        // RAF 2: Külliyat Rafı
        val horizontalRecyclerView2 = view.findViewById<RecyclerView>(R.id.horizontalBookRecyclerView2)
        horizontalRecyclerView2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        horizontalRecyclerView2.adapter = BookAdapter(allBooks.subList(0, 14)) // İlk 14 kitap Külliyat olsun

        // RAF 3: Evrad ve Kuran Rafı (Şimdi yeni liste ile dolduruldu)
        val horizontalRecyclerView3 = view.findViewById<RecyclerView>(R.id.horizontalBookRecyclerView3)
        horizontalRecyclerView3.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val evradVeKuranBooks = listOf(
            Book("Kuran-ı Kerim", R.drawable.kuran_i_kerim), // Görseli ekleyin
            Book("Hizbü'l-Kuran", R.drawable.hizbul_kuran),   // Görseli ekleyin
            Book("Hizb-ül Hakaik", R.drawable.hizbul_hakaik), // Görseli ekleyin
            Book("Cevşen", R.drawable.cevsen),             // Görseli ekleyin
            Book("Namaz Tesbihatı", R.drawable.namaz_tesbihati), // Görseli ekleyin
            Book("Elif-Ba", R.drawable.elif_ba)             // Görseli ekleyin
        )
        horizontalRecyclerView3.adapter = BookAdapter(evradVeKuranBooks)

        // RAF 4: Küçük Kitaplar Rafı
        val horizontalRecyclerView4 = view.findViewById<RecyclerView>(R.id.horizontalBookRecyclerView4)
        horizontalRecyclerView4.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        horizontalRecyclerView4.adapter = BookAdapter(allBooks.subList(14, allBooks.size)) // Kalan tüm küçük kitaplar

        // Navigasyon çubuğuyla çakışmayı önlemek için WindowInsets dinleyicisi
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.shelfBackground)) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomNavHeight = systemBarsInsets.bottom

            val shelf4Layout = view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.shelfLayout4)
            val params = shelf4Layout.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = bottomNavHeight + 16.dpToPx(resources.displayMetrics) // Ek 16dp boşluk
            shelf4Layout.layoutParams = params

            insets
        }
    }
}

// Yardımcı fonksiyon: DP'yi piksele çevirmek için
fun Int.dpToPx(displayMetrics: android.util.DisplayMetrics): Int =
    (this * displayMetrics.density + 0.5f).toInt()
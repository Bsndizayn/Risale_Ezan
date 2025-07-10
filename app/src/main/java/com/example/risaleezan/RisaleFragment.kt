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
import androidx.core.view.updatePadding
import android.graphics.drawable.Drawable
import com.google.android.material.bottomnavigation.BottomNavigationView

class RisaleFragment : Fragment() {

    // KİTAPLARIN RAF İÇİNDEKİ DİKEY KONUMUNU BELİRLEYEN SABİTLER
    // BU DEĞERLERİ DEĞİŞTİREREK KİTAPLARIN RAF ARKA PLANINA TAM OTURMASINI SAĞLAYIN!
    // Pozitif değerler boşluk bırakır ve kitabı küçültür.
    private val SHELF_TOP_PADDING_DP = 60    // Kitabın raf arka planının üst kenarından boşluk (daha büyük bir değer deneyin)
    private val SHELF_BOTTOM_PADDING_DP = 23 // Kitabın raf arka planının alt kenarından boşluk (daha küçük bir değer deneyin)

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_risale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = (activity as MainActivity).findViewById(R.id.bottom_navigation)

        val allBooks = listOf(
            // Külliyat
            Book("Asa-yı Musa", R.drawable.asa),
            Book("Tarihçe-i Hayat", R.drawable.tarihce),
            Book("Sözler", R.drawable.sozler),
            Book("Mektûbat", R.drawable.mektubat),
            Book("Lem'alar", R.drawable.lemalar),
            Book("Şualar", R.drawable.sualar),
            Book("Barla ", R.drawable.barla),
            Book("Kastamonu ", R.drawable.kastamonu),
            Book("Emirdağ ", R.drawable.emirdag),
            Book("İ.İ'caz", R.drawable.isarat),
            Book("M.Nuriye", R.drawable.mesnevi),
            Book("S.T.Gaybi", R.drawable.sikke),
            Book("İman ve Küfür M.", R.drawable.iman_kufur),
            Book("Muhakemat", R.drawable.muhakemat),

            // Küçük Kitaplar (devam)
            Book("Mucizat-ı Kur.", R.drawable.mucizat_i_kuraniye),
            Book("Mucizat-ı Ah.", R.drawable.mucizat_i_ahmediye),
            Book("Nur Çeşmesi", R.drawable.nur_cesmesi),
            Book("Bedi Üz. Cvp.", R.drawable.bedi_uz_cevap_veriyor),
            Book("Gençlik Reh.", R.drawable.genclik_rehberi),
            Book("Hizmet Reh.", R.drawable.hizmet_rehberi),
            Book("Nurun İlk Kap.", R.drawable.nurun_ilk_kapisi),
            Book("Ayet-ül Kübra", R.drawable.ayet_ul_kubra),
            Book("Mirkat-üs Sün.", R.drawable.mirkat_us_sunnet),
            Book("Zuhretunnur", R.drawable.zuhretunnur),
            Book("Meyve Risalesi", R.drawable.meyve_risalesi),
            Book("Haşir Risalesi", R.drawable.hasir_risalesi),
            Book("Hakikat Nur.", R.drawable.hakikat_nurlari),
            Book("Hanımlar Reh.", R.drawable.hanimlar_rehberi),
            Book("Konferans", R.drawable.konferans),
            Book("El-Hüccet-üz Z.", R.drawable.el_huccet_uz_zehra),
            Book("İman Hak.", R.drawable.iman_hakikatlari),
            Book("Hutbe-i Şam.", R.drawable.hutbe_i_samiye),
            Book("Rhmt. Şfkt. İlaç.", R.drawable.rahmet_sefkat_ilaclari),
            Book("Küçük Sözler", R.drawable.kucuk_sozler),
            Book("Münazarat", R.drawable.munazarat),
            Book("Miraç & Ş. Kamer", R.drawable.mirac_ve_sakki_kamer),
            Book("Rmzn. İkt. Şükür Ris.", R.drawable.ramazan_iktisat_sukr_risalesi),
            Book("Miftah-ül İm.", R.drawable.miftah_ul_iman),
            Book("Sunuhat-Tuluat-İş.", R.drawable.sunuhat_tuluat_isarat),
            Book("İçtihad Risalesi", R.drawable.ictihad_risalesi),
            Book("23. Söz", R.drawable.yirmiucuncu_soz),
            Book("Hastalar Risalesi", R.drawable.hastalar_risalesi),
            Book("33 Pencere", R.drawable.otuzuc_pencere),
            Book("Divan-ı H. Örf.", R.drawable.divan_i_harbi_orfi),
            Book("İhlas Risalesi", R.drawable.ihlas_risalesi),
            Book("Uhuvvet Risalesi", R.drawable.uhuvvet_risalesi),
            Book("Nur Alemi Anahtarı", R.drawable.nur_aleminin_bir_anahtari),
            Book("Ene & Zerre Ris.", R.drawable.ene_ve_zerre_risalesi),
            Book("Tabiat Risalesi", R.drawable.tabiat_risalesi),
            Book("Münacat", R.drawable.munacat),
            Book("İhtiyarlar Ris.", R.drawable.ihtiyarlar_risalesi),
            Book("30. Lem'a", R.drawable.otuzuncu_lem_a)
        )
        val evradVeKuranBooks = listOf(
            Book("Kuran-ı Kerim", R.drawable.kuran_i_kerim),
            Book("Hizbü'l-Kuran", R.drawable.hizbul_kuran),
            Book("Hizb-ül Hakaik", R.drawable.hizbul_hakaik),
            Book("Cevşen", R.drawable.cevsen),
            Book("Namaz Tesbihatı", R.drawable.namaz_tesbihati),
            Book("Elif-Ba", R.drawable.elif_ba)
        )

        view.post { // view'ın boyutları hazır olduğunda çalışacak
            val rootLayout = view as ViewGroup
            val totalHeightPx = rootLayout.height // Fragment'ın toplam yüksekliği (padding sonrası)

            val density = resources.displayMetrics.density

            // Her rafın dikey yüksekliğini ve kitapların sığması gereken yüksekliği hesaplayalım
            val guideTopPx = (totalHeightPx * 0.00).toInt()
            val guide1BottomPx = (totalHeightPx * 0.25).toInt()
            val guide2BottomPx = (totalHeightPx * 0.50).toInt()
            val guide3BottomPx = (totalHeightPx * 0.75).toInt()
            val guideBottomPx = (totalHeightPx * 1.00).toInt()

            val shelf1HeightPx = guide1BottomPx - guideTopPx
            val shelf2HeightPx = guide2BottomPx - guide1BottomPx
            val shelf3HeightPx = guide3BottomPx - guide2BottomPx
            val shelf4HeightPx = guideBottomPx - guide3BottomPx

            val bookAreaPaddingTopPx = (SHELF_TOP_PADDING_DP * density).toInt()
            val bookAreaPaddingBottomPx = (SHELF_BOTTOM_PADDING_DP * density).toInt()

            val bookHeightForShelf1 = shelf1HeightPx - bookAreaPaddingTopPx - bookAreaPaddingBottomPx
            val bookHeightForShelf2 = shelf2HeightPx - bookAreaPaddingTopPx - bookAreaPaddingBottomPx
            val bookHeightForShelf3 = shelf3HeightPx - bookAreaPaddingTopPx - bookAreaPaddingBottomPx
            val bookHeightForShelf4 = shelf4HeightPx - bookAreaPaddingTopPx - bookAreaPaddingBottomPx

            // Kitapların minimum bir yüksekliği olmalı, aksi takdirde çok küçük görünebilirler
            val minBookHeightPx = (70 * density).toInt()

            view.findViewById<RecyclerView>(R.id.horizontalBookRecyclerView1).apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = BookAdapter(allBooks.subList(0, 5), Math.max(minBookHeightPx, bookHeightForShelf1))
            }

            view.findViewById<RecyclerView>(R.id.horizontalBookRecyclerView2).apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = BookAdapter(allBooks.subList(0, 14), Math.max(minBookHeightPx, bookHeightForShelf2))
            }

            view.findViewById<RecyclerView>(R.id.horizontalBookRecyclerView3).apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = BookAdapter(evradVeKuranBooks, Math.max(minBookHeightPx, bookHeightForShelf3))
            }

            view.findViewById<RecyclerView>(R.id.horizontalBookRecyclerView4).apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = BookAdapter(allBooks.subList(14, allBooks.size), Math.max(minBookHeightPx, bookHeightForShelf4))
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = v.paddingLeft + systemBarsInsets.left,
                top = v.paddingTop + systemBarsInsets.top,
                right = v.paddingRight + systemBarsInsets.right,
                bottom = v.paddingBottom + systemBarsInsets.bottom
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        if (::bottomNavigationView.isInitialized) {
            bottomNavigationView.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        if (::bottomNavigationView.isInitialized) {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }
}

// Yardımcı fonksiyon: DP'yi piksele çevirmek için
fun Int.dpToPx(displayMetrics: android.util.DisplayMetrics): Int =
    (this * displayMetrics.density + 0.5f).toInt()
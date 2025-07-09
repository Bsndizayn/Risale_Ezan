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
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.google.android.material.bottomnavigation.BottomNavigationView // Bu import'u ekle

class RisaleFragment : Fragment() {

    // Raf arka planlarının üst ve altından bırakılacak DP boşlukları
    private val SHELF_TOP_PADDING_DP = 5
    private val SHELF_BOTTOM_PADDING_DP = 70
    private val CATEGORY_TITLE_HEIGHT_DP = 18 // textSize=18sp'ye göre tahmini, gerçek TextView yüksekliği alınmalı
    private val CATEGORY_TITLE_MARGIN_BOTTOM_DP = 8 // layout_marginBottom 8dp'ye göre

    private lateinit var bottomNavigationView: BottomNavigationView // BottomNavigationView referansını tutmak için

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_risale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity'deki BottomNavigationView'a erişim
        bottomNavigationView = (activity as MainActivity).findViewById(R.id.bottom_navigation)

        val allBooks = listOf(
            // Külliyat
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

            // Küçük Kitaplar (devam) - Görselleri eklediğinizden emin olun!
            Book("MUCİZAT-I KUR'ANİYE", R.drawable.mucizat_i_kuraniye),
            Book("MUCİZAT-I AHMEDİYE", R.drawable.mucizat_i_ahmediye),
            Book("NUR ÇEŞMESİ", R.drawable.nur_cesmesi),
            Book("BEDİ ÜZ.CEVAP VERİYOR", R.drawable.bedi_uz_cevap_veriyor),
            Book("GENÇLİK REHBERİ", R.drawable.genclik_rehberi),
            Book("HİZMET REHBERİ", R.drawable.hizmet_rehberi),
            Book("NURUN İLK KAPISI", R.drawable.nurun_ilk_kapisi),
            Book("AYET-ÜL KÜBRA", R.drawable.ayet_ul_kubra),
            Book("MİRKAT-ÜS SÜNNET", R.drawable.mirkat_us_sunnet),
            Book("ZÜHRETÜNNUR", R.drawable.zuhretunnur),
            Book("MEYVE RİSALESİ", R.drawable.meyve_risalesi),
            Book("HAŞİR RİSALESİ", R.drawable.hasir_risalesi),
            Book("HAKİKAT NURLARI", R.drawable.hakikat_nurlari),
            Book("HANIMLAR REHBERİ", R.drawable.hanimlar_rehberi),
            Book("KONFERANS", R.drawable.konferans),
            Book("EL-HÜCCET-ÜZ ZEHRA", R.drawable.el_huccet_uz_zehra),
            Book("İMAN HAKİKATLARI", R.drawable.iman_hakikatlari),
            Book("HUTBE-İ ŞAMİYE", R.drawable.hutbe_i_samiye),
            Book("RAHMET ŞEFKAT İLAÇLARI", R.drawable.rahmet_sefkat_ilaclari),
            Book("KÜÇÜK SÖZLER", R.drawable.kucuk_sozler),
            Book("MÜNAZARAT", R.drawable.munazarat),
            Book("MİRAC VE ŞAKKI KAMER", R.drawable.mirac_ve_sakki_kamer),
            Book("RAMAZAN İKTİSAT ŞÜKÜR RİSALESİ", R.drawable.ramazan_iktisat_sukr_risalesi),
            Book("MİFTAH-ÜL İMAN", R.drawable.miftah_ul_iman),
            Book("SUNUHAT-TULUAT-İŞARAT", R.drawable.sunuhat_tuluat_isarat),
            Book("İÇTİHAD RİSALESİ", R.drawable.ictihad_risalesi),
            Book("YİRMİÜÇÜNCÜ SÖZ", R.drawable.yirmiucuncu_soz),
            Book("HASTALAR RİSALESİ", R.drawable.hastalar_risalesi),
            Book("OTUZÜÇ PENCERE", R.drawable.otuzuc_pencere),
            Book("DİVAN-I HARBİ ÖRFİ", R.drawable.divan_i_harbi_orfi),
            Book("İHLAS RİSALESİ", R.drawable.ihlas_risalesi),
            Book("UHUVVET RİSALESİ", R.drawable.uhuvvet_risalesi),
            Book("NUR ALEMİNİN BİR ANAHTARI", R.drawable.nur_aleminin_bir_anahtari),
            Book("ENE VE ZERRE RİSALESİ", R.drawable.ene_ve_zerre_risalesi),
            Book("TABİAT RİSALESİ", R.drawable.tabiat_risalesi),
            Book("MÜNACAT", R.drawable.munacat),
            Book("İHTİYARLAR RİSALESİ", R.drawable.ihtiyarlar_risalesi),
            Book("OTUZUNCU LEM'A", R.drawable.otuzuncu_lem_a)
        )

        // RAF 3 için özel kitap listesi tanımlıyoruz
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

            val categoryTitleHeightPx = (CATEGORY_TITLE_HEIGHT_DP * density).toInt()
            val categoryTitleMarginBottomPx = (CATEGORY_TITLE_MARGIN_BOTTOM_DP * density).toInt()

            val bookAreaPaddingTopPx = (SHELF_TOP_PADDING_DP * density).toInt()
            val bookAreaPaddingBottomPx = (SHELF_BOTTOM_PADDING_DP * density).toInt()

            val bookHeightForShelf1 = shelf1HeightPx - categoryTitleHeightPx - categoryTitleMarginBottomPx - bookAreaPaddingTopPx - bookAreaPaddingBottomPx
            val bookHeightForShelf2 = shelf2HeightPx - categoryTitleHeightPx - categoryTitleMarginBottomPx - bookAreaPaddingTopPx - bookAreaPaddingBottomPx
            val bookHeightForShelf3 = shelf3HeightPx - categoryTitleHeightPx - categoryTitleMarginBottomPx - bookAreaPaddingTopPx - bookAreaPaddingBottomPx
            val bookHeightForShelf4 = shelf4HeightPx - categoryTitleHeightPx - categoryTitleMarginBottomPx - bookAreaPaddingTopPx - bookAreaPaddingBottomPx

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

    // RisaleFragment görünür olduğunda BottomNavigationView'ı gizle
    override fun onResume() {
        super.onResume()
        if (::bottomNavigationView.isInitialized) {
            bottomNavigationView.visibility = View.GONE
        }
    }

    // RisaleFragment görünümden ayrıldığında BottomNavigationView'ı tekrar göster
    override fun onStop() { // onPause veya onStop kullanabiliriz, onStop daha güvenlidir
        super.onStop()
        if (::bottomNavigationView.isInitialized) {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }
}

// Yardımcı fonksiyon: DP'yi piksele çevirmek için
fun Int.dpToPx(displayMetrics: android.util.DisplayMetrics): Int =
    (this * displayMetrics.density + 0.5f).toInt()
package com.example.risaleezan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager // Bu importu ekle
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

        val recyclerView = view.findViewById<RecyclerView>(R.id.bookRecyclerView)

        // Tek bir yatay raf için LinearLayoutManager kullanıldı
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val books = listOf(
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
            Book("Muhakemat", R.drawable.muhakemat)
        )

        recyclerView.adapter = BookAdapter(books)

        // SpaceItemDecoration'a artık çok fazla ihtiyacımız yok, item_book.xml'deki marginEnd yeterli.
        // İstersen tamamen kaldırabilirsin veya çok küçük bir dikey boşluk için kullanabilirsin.
        // Şimdilik kaldırıyorum, dikey boşluğu item_book.xml içindeki TextView marginTop ile yönetiyoruz.
        // Eğer her öğe arasına eşit bir yatay boşluk istiyorsan, item_book.xml'deki marginEnd yeterli olacaktır.
        // Ancak RecyclerView'ın en başı ve sonuna boşluk eklemek istersen yine de bir ItemDecoration faydalı olabilir.

        // Navigasyon çubuğuyla çakışmayı önlemek için WindowInsets dinleyicisi
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Bottom navigation bar'ın yüksekliğini alıp padding olarak ekle
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                systemBarsInsets.bottom + view.paddingBottom // Mevcut paddingBottom'a ekle
            )
            insets // Insets'i tüketmeden geri döndür
        }
    }
}
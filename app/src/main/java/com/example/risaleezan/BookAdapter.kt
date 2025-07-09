package com.example.risaleezan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.Drawable // Drawable importunu ekle

// `expectedBookHeightPx` ve `bookMarginEndPx` parametrelerini ekledik
class BookAdapter(
    private val books: List<Book>,
    private val expectedBookHeightPx: Int // Raftan beklenen kitap yüksekliği (px)
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.bookImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.image.setImageResource(book.imageResId)

        // Kitabın ImageView boyutlarını dinamik olarak ayarla
        val layoutParams = holder.image.layoutParams
        layoutParams.height = expectedBookHeightPx // Yüksekliği dışarıdan gelen piksel değerine ayarla

        // Genişliği, yükseklik ve orijinal görselin oranına göre otomatik hesapla
        // Eğer adjustViewBounds="true" ise genelde genişliği manuel ayarlamaya gerek kalmaz
        // ancak daha garantili olması için yapabiliriz.
        holder.image.adjustViewBounds = true // Tekrar emin olalım
        holder.image.scaleType = ImageView.ScaleType.FIT_CENTER // Tekrar emin olalım

        // Kitabın orijinal görselini yükle ve oranını al
        val drawable: Drawable? = holder.image.context.resources.getDrawable(book.imageResId, null)
        if (drawable != null) {
            val aspectRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
            layoutParams.width = (expectedBookHeightPx * aspectRatio).toInt()
        }

        holder.image.layoutParams = layoutParams
    }
}
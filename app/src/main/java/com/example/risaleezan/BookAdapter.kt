package com.example.risaleezan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView // TextView importunu ekle
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.Drawable // Drawable importunu ekle

class BookAdapter(
    private val books: List<Book>,
    private val expectedBookHeightPx: Int // Raftan beklenen kitap yüksekliği (px)
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.bookImage)
        val title: TextView = view.findViewById(R.id.bookTitle) // TextView referansını ekle
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
        holder.title.text = book.title // Kitap başlığını TextView'a ata

        // Kitabın ImageView boyutlarını dinamik olarak ayarla (expectedBookHeightPx kullanılıyor)
        val layoutParams = holder.image.layoutParams
        layoutParams.height = expectedBookHeightPx // Yüksekliği dışarıdan gelen piksel değerine ayarla

        holder.image.adjustViewBounds = true // Tekrar emin olalım
        holder.image.scaleType = ImageView.ScaleType.FIT_CENTER // Tekrar emin olalım

        val drawable: Drawable? = holder.image.context.resources.getDrawable(book.imageResId, null)
        if (drawable != null) {
            val aspectRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()
            layoutParams.width = (expectedBookHeightPx * aspectRatio).toInt()
        }

        holder.image.layoutParams = layoutParams
    }
}
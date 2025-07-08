package com.example.risaleezan

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        // Bu boşluklar, item_book.xml'deki margin değerleriyle birlikte çalışacaktır.
        // Eğer item_book.xml'de marginHorizontal kullanıyorsan, burada left/right boşlukları 0 yapabilirsin.
        outRect.left = space
        outRect.right = space
        outRect.bottom = space
        outRect.top = space
    }
}
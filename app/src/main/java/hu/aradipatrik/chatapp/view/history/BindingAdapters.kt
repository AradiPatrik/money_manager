package hu.aradipatrik.chatapp.view.history

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("srcRes")
    fun ImageView.srcRes(resId: Int) {
        setImageResource(resId)
    }

    @JvmStatic
    @BindingAdapter("tint")
    fun ImageView.tint(tintResId: Int) {
        ImageViewCompat.setImageTintList(
                this,
                ColorStateList.valueOf(ContextCompat.getColor(context, tintResId))
        )
    }
}
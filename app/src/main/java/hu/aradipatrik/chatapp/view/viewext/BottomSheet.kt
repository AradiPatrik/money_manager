package hu.aradipatrik.chatapp.view.viewext

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun BottomSheetBehavior<*>.onStateChange(f: (Int) -> Unit) {
  addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) {
      // intentionally left blank
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
      f(newState)
    }
  })
}
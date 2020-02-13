package com.aradipatrik.yamm.common.viewext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun ViewGroup.inflate(layoutResource: Int) = LayoutInflater.from(context)
    .inflate(layoutResource, this, false)

fun View.hideAsBottomSheet() {
    BottomSheetBehavior.from(this).state = BottomSheetBehavior.STATE_HIDDEN
}

fun View.showAsBottomSheet() {
    BottomSheetBehavior.from(this).state = BottomSheetBehavior.STATE_EXPANDED
}
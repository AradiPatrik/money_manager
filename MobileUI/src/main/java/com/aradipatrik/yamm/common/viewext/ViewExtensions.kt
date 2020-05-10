package com.aradipatrik.yamm.common.viewext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.Observable

fun ViewGroup.inflate(layoutResource: Int) = LayoutInflater.from(context)
    .inflate(layoutResource, this, false)

fun View.hideAsBottomSheet() {
    asBottomSheet().state = BottomSheetBehavior.STATE_HIDDEN
}

fun View.showAsBottomSheet() {
    asBottomSheet().state = BottomSheetBehavior.STATE_EXPANDED
}

fun <V : View> BottomSheetBehavior<V>.isExpanded() = state == BottomSheetBehavior.STATE_EXPANDED

fun <V : View> BottomSheetBehavior<V>.isCollapsed() = state == BottomSheetBehavior.STATE_COLLAPSED

fun <V : View> BottomSheetBehavior<V>.collapseEvents(): Observable<Unit> {
    var bottomSheetEventListener: BottomSheetBehavior.BottomSheetCallback? = null
    return Observable.create<Unit> { emitter ->
        bottomSheetEventListener = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    emitter.onNext(Unit)
                }
            }
        }
        bottomSheetEventListener?.let(::addBottomSheetCallback)
    }.doOnDispose {
        bottomSheetEventListener?.let(::removeBottomSheetCallback)
    }
}

fun View.asBottomSheet() = BottomSheetBehavior.from(this)

package com.aradipatrik.yamm.common.viewext

import android.view.LayoutInflater
import android.view.ViewGroup

fun ViewGroup.inflate(layoutResource: Int) = LayoutInflater.from(context)
    .inflate(layoutResource, this, false)
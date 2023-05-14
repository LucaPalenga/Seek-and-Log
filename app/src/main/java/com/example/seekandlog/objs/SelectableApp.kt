package com.example.seekandlog.objs

import android.graphics.drawable.Drawable

data class SelectableApp(
    val logo: Drawable?,
    val data: SelectableAppData,
    var selected: Boolean = false
)
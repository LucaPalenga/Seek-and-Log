package com.example.seekandlog.objs

import android.graphics.drawable.Drawable

data class SelectableApp(
    val logo: Drawable?,
    val description: SelectableAppDescription,
    var selected: Boolean = false
)
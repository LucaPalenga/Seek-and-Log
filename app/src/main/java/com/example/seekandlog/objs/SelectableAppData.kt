package com.example.seekandlog.objs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectableAppData(
    val title: String,
    val packageName: String?,
) : Parcelable
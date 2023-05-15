package com.example.seekandlog.objs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * this class represent the description of a SelectableApp
 * composed by title and package name
 */
@Parcelize
data class SelectableAppDescription(
    val title: String,
    val packageName: String?,
) : Parcelable
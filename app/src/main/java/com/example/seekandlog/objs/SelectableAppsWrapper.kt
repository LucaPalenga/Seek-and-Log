package com.example.seekandlog.objs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * class wrapper for a list of SelectableAppData
 * this is useful for data transfer through Bundle
 */
@Parcelize
data class SelectableAppsWrapper(val apps: List<SelectableAppDescription>) : Parcelable
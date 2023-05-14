package com.example.seekandlog.objs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectableAppsWrapper(val apps: List<SelectableAppData>) : Parcelable
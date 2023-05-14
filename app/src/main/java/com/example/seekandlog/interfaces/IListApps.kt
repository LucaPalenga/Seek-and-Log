package com.example.seekandlog.interfaces

import android.content.pm.PackageManager
import com.example.seekandlog.objs.SelectableApp

interface IListApps {
    fun getApps(packageManager: PackageManager): List<SelectableApp>
}
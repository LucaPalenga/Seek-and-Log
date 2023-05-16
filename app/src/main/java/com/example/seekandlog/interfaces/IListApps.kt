package com.example.seekandlog.interfaces

import android.content.pm.PackageManager
import com.example.seekandlog.objs.SelectableApp

/**
 * interface implemented by ListApps class in the dynamic module seekappfeature
 * this is a workaround to use some expected methods in this module
 * letting do the work by external classes
 */
interface IListApps {
    fun getApps(packageManager: PackageManager, sorted: Boolean): List<SelectableApp>
}
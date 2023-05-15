package com.example.seekappfeature

import android.content.pm.PackageManager
import com.example.seekandlog.interfaces.IListApps
import com.example.seekandlog.objs.SelectableApp
import com.example.seekandlog.objs.SelectableAppDescription

class ListApps : IListApps {
    override fun getApps(packageManager: PackageManager): List<SelectableApp> {
        val appList = mutableListOf<SelectableApp>()

        packageManager.getInstalledApplications(PackageManager.INSTALL_REASON_UNKNOWN).map {
            appList.add(
                SelectableApp(
                    logo = packageManager.getApplicationIcon(it),
                    description = SelectableAppDescription(
                        title = packageManager.getApplicationLabel(it).toString(),
                        packageName = it.packageName
                    )
                )
            )
        }

        return appList
    }
}



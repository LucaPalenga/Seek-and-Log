package com.example.seekappfeature

import android.content.pm.PackageManager
import android.os.Build
import com.example.seekandlog.interfaces.IListApps
import com.example.seekandlog.objs.SelectableApp
import com.example.seekandlog.objs.SelectableAppDescription

/**
 * this class extract installed applications on device
 * @sorted if you want to get an ordered list (ascending order)
 */
class ListApps : IListApps {
    override fun getApps(
        packageManager: PackageManager,
        sorted: Boolean
    ): List<SelectableApp> {
        val appList = mutableListOf<SelectableApp>()

        val extractedApps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(PackageManager.INSTALL_REASON_UNKNOWN.toLong()))
                .map { it.applicationInfo }
        } else {
            packageManager.getInstalledApplications(PackageManager.INSTALL_REASON_UNKNOWN)
        }

        extractedApps.map {
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

        return if (sorted)
            appList.sortedWith(Comparator { s1, s2 ->
                return@Comparator s1.description.title.lowercase()
                    .compareTo(s2.description.title.lowercase())
            })
        else appList
    }
}



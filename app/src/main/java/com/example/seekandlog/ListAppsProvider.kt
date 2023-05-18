package com.example.seekandlog

import com.example.seekandlog.interfaces.IListApps

/**
 * provides dependency from the dynamic module seekappfeature
 */
object ListAppsProvider {

    private var dependency: IListApps? = null

    fun provide(): IListApps? {
        if (dependency == null) {
            val dynamicDependencyClass = Class.forName(Values.FEATURE_LIST_APPS_CLASS)
            dependency = dynamicDependencyClass.newInstance() as? IListApps
        }

        return dependency
    }
}
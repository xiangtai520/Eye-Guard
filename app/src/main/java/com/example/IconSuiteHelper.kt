package com.example

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

object IconSuiteHelper {

    private val ALIASES = listOf(
        "com.example.MainActivityForest",
        "com.example.MainActivityOcean",
        "com.example.MainActivityGeek"
    )

    fun updateLauncherIcon(context: Context, themeMode: String) {
        val pm = context.packageManager
        val packageName = context.packageName

        // Map theme mode to the respective activity-alias
        val activeAlias = when (themeMode) {
            "forest" -> "com.example.MainActivityForest"
            "ocean" -> "com.example.MainActivityOcean"
            "geek" -> "com.example.MainActivityGeek"
            "system" -> "com.example.MainActivityForest" // Default/system theme is Forest base
            else -> "com.example.MainActivityForest"
        }

        // Loop and enable the chosen one, disable all others
        for (aliasName in ALIASES) {
            val componentName = ComponentName(packageName, aliasName)
            val isTarget = (aliasName == activeAlias)
            val currentState = pm.getComponentEnabledSetting(componentName)
            val targetState = if (isTarget) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }

            if (currentState != targetState) {
                pm.setComponentEnabledSetting(
                    componentName,
                    targetState,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}

package com.imperivox.android2clean.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import com.imperivox.android2clean.data.model.AppInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class AppManagerRepository(private val context: Context) {
    fun getInstalledApps(): Flow<List<AppInfo>> = flow {
        val packageManager = context.packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .map { appInfo ->
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    icon = packageManager.getApplicationIcon(appInfo.packageName),
                    size = appInfo.sourceDir.let { File(it).length() },
                    cacheSize = context.getCacheDir(appInfo.packageName)?.length() ?: 0L,
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .sortedBy { it.appName }
        emit(apps)
    }

    suspend fun uninstallApp(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun Context.getCacheDir(packageName: String): File? {
        return try {
            File("/data/data/$packageName/cache")
        } catch (e: Exception) {
            null
        }
    }
}
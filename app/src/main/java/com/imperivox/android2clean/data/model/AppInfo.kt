package com.imperivox.android2clean.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val size: Long,
    val cacheSize: Long,
    val isSystemApp: Boolean
)
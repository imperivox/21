package com.imperivox.android2clean.ui.screens.appmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imperivox.android2clean.data.model.AppInfo
import com.imperivox.android2clean.data.repository.AppManagerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppManagerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppManagerRepository(application)
    
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    init {
        loadApps()
    }
    
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getInstalledApps()
                .collect { appList ->
                    _apps.value = appList
                    _isLoading.value = false
                }
        }
    }
    
    fun uninstallApp(packageName: String) {
        viewModelScope.launch {
            repository.uninstallApp(packageName)
        }
    }
}
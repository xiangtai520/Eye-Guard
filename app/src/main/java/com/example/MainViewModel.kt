package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val appState: StateFlow<AppState> = EyeGuardService.appState
    val timeLeftSeconds: StateFlow<Int> = EyeGuardService.timeLeftSeconds
    val isDemoMode: StateFlow<Boolean> = EyeGuardService.isDemoMode
    val onTriggerAlarm: SharedFlow<Unit> = EyeGuardService.onTriggerAlarm

    private val sharedPrefs = application.getSharedPreferences("eye_guard_prefs", Context.MODE_PRIVATE)

    private val _currentThemeMode = MutableStateFlow(sharedPrefs.getString("theme_mode", "forest") ?: "forest")
    val currentThemeMode: StateFlow<String> = _currentThemeMode.asStateFlow()

    private val _workDuration = MutableStateFlow(sharedPrefs.getInt("work_duration", 20))
    val workDuration: StateFlow<Int> = _workDuration.asStateFlow()

    private val _breakDuration = MutableStateFlow(sharedPrefs.getInt("break_duration", 20))
    val breakDuration: StateFlow<Int> = _breakDuration.asStateFlow()

    private val _customSoundUri = MutableStateFlow(sharedPrefs.getString("custom_sound_uri", "") ?: "")
    val customSoundUri: StateFlow<String> = _customSoundUri.asStateFlow()

    init {
        // Synchronize helper countdown state with saved prefs
        EyeGuardService.updateIdleTimerValue(application)
        IconSuiteHelper.updateLauncherIcon(application, _currentThemeMode.value)
    }

    companion object {
        const val DEFAULT_WORK_SECONDS = 20 * 60  // 20 minutes
        const val DEFAULT_REST_SECONDS = 20       // 20 seconds
        
        const val DEMO_WORK_SECONDS = 20          // 20 seconds demo
        const val DEMO_REST_SECONDS = 5           // 5 seconds demo
    }

    fun setThemeMode(mode: String) {
        _currentThemeMode.value = mode
        sharedPrefs.edit().putString("theme_mode", mode).apply()
        IconSuiteHelper.updateLauncherIcon(getApplication(), mode)
    }

    fun setWorkDuration(minutes: Int) {
        _workDuration.value = minutes
        sharedPrefs.edit().putInt("work_duration", minutes).apply()
        EyeGuardService.updateIdleTimerValue(getApplication())
    }

    fun setBreakDuration(seconds: Int) {
        _breakDuration.value = seconds
        sharedPrefs.edit().putInt("break_duration", seconds).apply()
    }

    fun setCustomSoundUri(uri: String) {
        _customSoundUri.value = uri
        sharedPrefs.edit().putString("custom_sound_uri", uri).apply()
    }

    fun toggleProtection() {
        val app = getApplication<Application>()
        if (appState.value == AppState.IDLE) {
            EyeGuardService.startService(app)
        } else {
            EyeGuardService.stopService(app)
        }
    }

    fun toggleDemoMode() {
        EyeGuardService.toggleDemo(getApplication())
    }

    fun skipRest() {
        EyeGuardService.skipRest(getApplication())
    }

    fun getWorkMaxSeconds(): Int {
        if (isDemoMode.value) return DEMO_WORK_SECONDS
        return _workDuration.value * 60
    }

    fun getRestMaxSeconds(): Int {
        if (isDemoMode.value) return DEMO_REST_SECONDS
        return _breakDuration.value
    }
}

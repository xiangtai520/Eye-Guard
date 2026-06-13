package com.example

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class EyeGuardService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    companion object {
        const val CHANNEL_ID = "eye_guard_notification_channel"
        const val NOTIFICATION_ID = 4020
        
        // Static States shared transparently with ViewModel and Jetpack UI
        private val _appState = MutableStateFlow(AppState.IDLE)
        val appState = _appState.asStateFlow()

        private val _timeLeftSeconds = MutableStateFlow(20 * 60)
        val timeLeftSeconds = _timeLeftSeconds.asStateFlow()

        private val _isDemoMode = MutableStateFlow(false)
        val isDemoMode = _isDemoMode.asStateFlow()

        private val _onTriggerAlarm = MutableSharedFlow<Unit>(extraBufferCapacity = 64)
        val onTriggerAlarm = _onTriggerAlarm.asSharedFlow()

        // Helper Intent commands
        const val ACTION_START_PROTECTION = "ACTION_START_PROTECTION"
        const val ACTION_STOP_PROTECTION = "ACTION_STOP_PROTECTION"
        const val ACTION_TOGGLE_DEMO = "ACTION_TOGGLE_DEMO"
        const val ACTION_SKIP_REST = "ACTION_SKIP_REST"

        fun startService(context: Context) {
            val intent = Intent(context, EyeGuardService::class.java).apply {
                action = ACTION_START_PROTECTION
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, EyeGuardService::class.java).apply {
                action = ACTION_STOP_PROTECTION
            }
            context.startService(intent)
        }

        fun skipRest(context: Context) {
            val intent = Intent(context, EyeGuardService::class.java).apply {
                action = ACTION_SKIP_REST
            }
            context.startService(intent)
        }

        fun toggleDemo(context: Context) {
            val intent = Intent(context, EyeGuardService::class.java).apply {
                action = ACTION_TOGGLE_DEMO
            }
            context.startService(intent)
        }

        fun updateIdleTimerValue(context: Context) {
            if (_appState.value == AppState.IDLE) {
                val prefs = context.getSharedPreferences("eye_guard_prefs", Context.MODE_PRIVATE)
                val workMin = prefs.getInt("work_duration", 20)
                _timeLeftSeconds.value = if (_isDemoMode.value) MainViewModel.DEMO_WORK_SECONDS else (workMin * 60)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_PROTECTION -> {
                startWorkCycle()
            }
            ACTION_STOP_PROTECTION -> {
                stopWorkCycle()
            }
            ACTION_TOGGLE_DEMO -> {
                _isDemoMode.value = !_isDemoMode.value
                if (_appState.value != AppState.IDLE) {
                    if (_appState.value == AppState.WORKING) {
                        startWorkCycle()
                    } else if (_appState.value == AppState.RESTING) {
                        startRestCycle()
                    }
                } else {
                    resetTimerValue()
                }
            }
            ACTION_SKIP_REST -> {
                if (_appState.value == AppState.RESTING) {
                    startWorkCycle()
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startWorkCycle() {
        timerJob?.cancel()
        _appState.value = AppState.WORKING
        val prefs = getSharedPreferences("eye_guard_prefs", Context.MODE_PRIVATE)
        val workMin = prefs.getInt("work_duration", 20)
        val maxDuration = if (_isDemoMode.value) MainViewModel.DEMO_WORK_SECONDS else (workMin * 60)
        _timeLeftSeconds.value = maxDuration
        startForeground(NOTIFICATION_ID, createNotification(maxDuration, AppState.WORKING))
        startCountdown(maxDuration) {
            triggerAlarmState()
            startRestCycle()
        }
    }

    private fun startRestCycle() {
        timerJob?.cancel()
        _appState.value = AppState.RESTING
        val prefs = getSharedPreferences("eye_guard_prefs", Context.MODE_PRIVATE)
        val breakSec = prefs.getInt("break_duration", 20)
        val maxDuration = if (_isDemoMode.value) MainViewModel.DEMO_REST_SECONDS else breakSec
        _timeLeftSeconds.value = maxDuration
        startForeground(NOTIFICATION_ID, createNotification(maxDuration, AppState.RESTING))
        startCountdown(maxDuration) {
            triggerAlarmState()
            startWorkCycle()
        }
    }

    private fun triggerAlarmState() {
        serviceScope.launch {
            _onTriggerAlarm.emit(Unit)
        }
        // Force fully penetrative sound/vibrate play from service background
        AlertHelper.playSoundAndVibrate(this)
    }

    private fun startCountdown(initialSeconds: Int, onComplete: () -> Unit) {
        timerJob = serviceScope.launch {
            var currentSeconds = initialSeconds
            while (currentSeconds > 0) {
                delay(1000)
                currentSeconds--
                _timeLeftSeconds.value = currentSeconds
                updateNotification(currentSeconds, _appState.value)
            }
            onComplete()
        }
    }

    private fun stopWorkCycle() {
        timerJob?.cancel()
        _appState.value = AppState.IDLE
        resetTimerValue()
        stopForeground(true)
        stopSelf()
    }

    private fun resetTimerValue() {
        val prefs = getSharedPreferences("eye_guard_prefs", Context.MODE_PRIVATE)
        val workMin = prefs.getInt("work_duration", 20)
        _timeLeftSeconds.value = if (_isDemoMode.value) MainViewModel.DEMO_WORK_SECONDS else (workMin * 60)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "护眼提醒"
            val descriptionText = "20-20-20护眼工作倒计时与远眺恢复计时推送"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(secondsRemaining: Int, state: AppState): Notification {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)

        val title = if (state == AppState.RESTING) "🌟 该休息了：请向20英尺外远眺！" else "💚 护眼保护正在生效中..."
        val contentText = if (state == AppState.RESTING) {
            "正在眼部放松，深度缓冲还剩 $formattedTime"
        } else {
            "距下次眼部放松：还有 $formattedTime 专注计时"
        }

        val activityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            activityIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)

        if (state == AppState.RESTING) {
            val skipIntent = Intent(this, EyeGuardService::class.java).apply {
                action = ACTION_SKIP_REST
            }
            val skipPendingIntent = PendingIntent.getService(
                this, 
                1, 
                skipIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(0, "跳过休息", skipPendingIntent)
        }

        return builder.build()
    }

    private fun updateNotification(secondsRemaining: Int, state: AppState) {
        val notification = createNotification(secondsRemaining, state)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

package com.example.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.R
import com.example.data.DraftSessionManager
import com.example.ui.theme.MyApplicationTheme
import kotlin.math.abs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OverlayService : Service() {

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private val overlayController = OverlayController()
  private val lifecycleOwner = OverlayLifecycleOwner()

  private var windowManager: WindowManager? = null
  private var overlayView: ComposeView? = null
  private var windowParams: WindowManager.LayoutParams? = null

  private var initialX = 0
  private var initialY = 0
  private var initialTouchX = 0f
  private var initialTouchY = 0f
  private var isDragging = false

  companion object {
    private const val TAG = "OverlayService"
    const val NOTIFICATION_ID = 1001
    const val CHANNEL_ID = "draftly_overlay_channel"

    const val ACTION_START = "com.example.overlay.ACTION_START"
    const val ACTION_STOP = "com.example.overlay.ACTION_STOP"

    fun start(context: Context) {
      val intent = Intent(context, OverlayService::class.java).apply {
        action = ACTION_START
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stop(context: Context) {
      val intent = Intent(context, OverlayService::class.java).apply {
        action = ACTION_STOP
      }
      context.startService(intent)
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    lifecycleOwner.onCreate()

    // Collect draft data changes from DraftSessionManager
    serviceScope.launch {
      DraftSessionManager.draftState.collectLatest { draft ->
        overlayController.updateDraftData(
          role = draft.selectedRole,
          recommendations = DraftSessionManager.recommendations.value
        )
      }
    }

    serviceScope.launch {
      DraftSessionManager.recommendations.collectLatest { recs ->
        overlayController.updateDraftData(
          role = DraftSessionManager.draftState.value.selectedRole,
          recommendations = recs
        )
      }
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.action == ACTION_STOP) {
      stopOverlay()
      stopSelf()
      return START_NOT_STICKY
    }

    if (!Settings.canDrawOverlays(this)) {
      Log.w(TAG, "Cannot start overlay: overlay permission not granted")
      stopSelf()
      return START_NOT_STICKY
    }

    startForegroundNotification()
    showOverlayWindow()
    overlayController.startOverlay()

    return START_STICKY
  }

  private fun startForegroundNotification() {
    val openAppIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val openPendingIntent = PendingIntent.getActivity(
      this,
      0,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val stopIntent = Intent(this, OverlayService::class.java).apply {
      action = ACTION_STOP
    }
    val stopPendingIntent = PendingIntent.getService(
      this,
      1,
      stopIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle("Draftly Overlay Active")
      .setContentText("Tap to open Draftly or use the floating control")
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentIntent(openPendingIntent)
      .addAction(0, "Stop", stopPendingIntent)
      .setOngoing(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .build()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      startForeground(
        NOTIFICATION_ID,
        notification,
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
      )
    } else {
      startForeground(NOTIFICATION_ID, notification)
    }
  }

  private fun showOverlayWindow() {
    if (overlayView != null) return

    windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    val params = WindowManager.LayoutParams(
      WindowManager.LayoutParams.WRAP_CONTENT,
      WindowManager.LayoutParams.WRAP_CONTENT,
      layoutType,
      WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.TOP or Gravity.START
      x = 80
      y = 180
    }
    windowParams = params

    val composeView = ComposeView(this).apply {
      setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
    }

    composeView.setViewTreeLifecycleOwner(lifecycleOwner)
    composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
    composeView.setViewTreeViewModelStoreOwner(lifecycleOwner)

    composeView.setContent {
      MyApplicationTheme {
        val overlayState by overlayController.state.collectAsState()

        OverlayContent(
          isExpanded = overlayState.isExpanded,
          selectedRole = overlayState.selectedRole,
          recommendations = overlayState.recommendations,
          onToggleExpand = {
            overlayController.toggleExpand()
          },
          onMinimize = {
            overlayController.minimize()
          },
          onStopOverlay = {
            stopOverlay()
            stopSelf()
          },
          onOpenApp = {
            val appIntent = Intent(this@OverlayService, MainActivity::class.java).apply {
              flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(appIntent)
          }
        )
      }
    }

    // Touch listener for smooth dragging around screen
    composeView.setOnTouchListener { _, event ->
      val currentParams = windowParams ?: return@setOnTouchListener false
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          initialX = currentParams.x
          initialY = currentParams.y
          initialTouchX = event.rawX
          initialTouchY = event.rawY
          isDragging = false
          false
        }
        MotionEvent.ACTION_MOVE -> {
          val dx = event.rawX - initialTouchX
          val dy = event.rawY - initialTouchY
          if (abs(dx) > 12 || abs(dy) > 12) {
            isDragging = true
            currentParams.x = (initialX + dx).toInt()
            currentParams.y = (initialY + dy).toInt()
            try {
              windowManager?.updateViewLayout(composeView, currentParams)
            } catch (e: Exception) {
              Log.e(TAG, "Error updating overlay layout", e)
            }
            true
          } else {
            false
          }
        }
        MotionEvent.ACTION_UP -> {
          isDragging
        }
        else -> false
      }
    }

    try {
      windowManager?.addView(composeView, params)
      overlayView = composeView
    } catch (e: Exception) {
      Log.e(TAG, "Failed to add overlay window", e)
    }
  }

  private fun stopOverlay() {
    overlayController.stopOverlay()
    removeOverlayView()
  }

  private fun removeOverlayView() {
    overlayView?.let { view ->
      try {
        if (view.isAttachedToWindow) {
          windowManager?.removeView(view)
        }
      } catch (e: Exception) {
        Log.e(TAG, "Failed to remove overlay view", e)
      }
    }
    overlayView = null
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Draftly Overlay Mode",
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = "Shows notifications when Draftly floating overlay is running above other apps"
        setShowBadge(false)
      }
      val manager = getSystemService(NotificationManager::class.java)
      manager?.createNotificationChannel(channel)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    stopOverlay()
    lifecycleOwner.onDestroy()
    serviceScope.cancel()
    DraftSessionManager.setOverlayActive(false)
  }
}

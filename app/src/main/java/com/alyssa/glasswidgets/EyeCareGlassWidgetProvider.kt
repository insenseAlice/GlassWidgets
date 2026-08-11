package com.alyssa.glasswidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews

class EyeCareGlassWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            WidgetPrefs.deleteAll(context, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_TOGGLE) {
            val widgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

            val running = WidgetPrefs.loadExtra(context, widgetId, EXTRA_RUNNING, "0") == "1"
            if (running) {
                // 正在倒计时 -> 点击后停止并重置
                WidgetPrefs.saveExtra(context, widgetId, EXTRA_RUNNING, "0")
            } else {
                // 未开始 -> 点击后从 20 分钟开始倒计时
                val base = SystemClock.elapsedRealtime() + TWENTY_MIN_MILLIS
                WidgetPrefs.saveExtra(context, widgetId, EXTRA_BASE, base.toString())
                WidgetPrefs.saveExtra(context, widgetId, EXTRA_RUNNING, "1")
            }

            val appWidgetManager = AppWidgetManager.getInstance(context)
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    companion object {
        const val ACTION_TOGGLE = "com.alyssa.glasswidgets.ACTION_EYECARE_TOGGLE"
        const val EXTRA_RUNNING = "eyecare_running"
        const val EXTRA_BASE = "eyecare_base"
        const val TWENTY_MIN_MILLIS = 20 * 60 * 1000L

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_eyecare_glass)

            val palette = GreenTheme.get(WidgetPrefs.loadPalette(context, widgetId))
            val alpha = WidgetPrefs.loadAlpha(context, widgetId)
            views.setImageViewResource(R.id.iv_glass_bg, palette.cardBackgroundRes)
            views.setInt(R.id.iv_glass_bg, "setImageAlpha", alpha)
            views.setInt(R.id.iv_leaf, "setColorFilter", palette.textColor)
            views.setTextColor(R.id.tv_title, palette.secondaryTextColor)
            views.setTextColor(R.id.chronometer, palette.textColor)
            views.setTextColor(R.id.tv_status, palette.secondaryTextColor)

            val running = WidgetPrefs.loadExtra(context, widgetId, EXTRA_RUNNING, "0") == "1"
            if (running) {
                val base = WidgetPrefs.loadExtra(context, widgetId, EXTRA_BASE, "0").toLongOrNull()
                    ?: (SystemClock.elapsedRealtime() + TWENTY_MIN_MILLIS)
                views.setChronometer(R.id.chronometer, base, "%s", true)
                views.setTextViewText(R.id.tv_status, "倒计时中，点击可重置")
            } else {
                // 未开始：显示满 20:00，不走动
                val staticBase = SystemClock.elapsedRealtime() + TWENTY_MIN_MILLIS
                views.setChronometer(R.id.chronometer, staticBase, "%s", false)
                views.setTextViewText(R.id.tv_status, "点击开始 20 分钟")
            }

            val toggleIntent = Intent(context, EyeCareGlassWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, widgetId, toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}

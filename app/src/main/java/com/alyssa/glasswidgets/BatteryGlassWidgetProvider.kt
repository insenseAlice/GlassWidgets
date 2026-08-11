package com.alyssa.glasswidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings
import android.widget.RemoteViews

class BatteryGlassWidgetProvider : AppWidgetProvider() {

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

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_battery_glass)

            val palette = GreenTheme.get(WidgetPrefs.loadPalette(context, widgetId))
            val alpha = WidgetPrefs.loadAlpha(context, widgetId)
            views.setImageViewResource(R.id.iv_glass_bg, palette.cardBackgroundRes)
            views.setInt(R.id.iv_glass_bg, "setImageAlpha", alpha)
            views.setInt(R.id.iv_leaf, "setColorFilter", palette.textColor)
            views.setTextColor(R.id.tv_percent, palette.textColor)
            views.setTextColor(R.id.tv_status, palette.secondaryTextColor)

            val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val charging = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ==
                BatteryManager.BATTERY_STATUS_CHARGING
            val percent = if (level >= 0 && scale > 0) (level * 100 / scale) else 0

            views.setTextViewText(R.id.tv_percent, "$percent%")
            views.setProgressBar(R.id.progress_battery, 100, percent, false)
            views.setTextViewText(
                R.id.tv_status,
                when {
                    charging -> "充电中"
                    percent <= 20 -> "电量低，记得充电"
                    percent <= 50 -> "电量正常"
                    else -> "电量充足"
                }
            )

            val settingsIntent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            val pendingIntent = PendingIntent.getActivity(
                context, widgetId, settingsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}

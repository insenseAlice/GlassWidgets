package com.alyssa.glasswidgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class MusicGlassWidgetProvider : AppWidgetProvider() {

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
        const val EXTRA_PACKAGE = "music_package"
        const val EXTRA_LABEL = "music_label"

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_music_glass)

            val palette = GreenTheme.get(WidgetPrefs.loadPalette(context, widgetId))
            val alpha = WidgetPrefs.loadAlpha(context, widgetId)
            views.setImageViewResource(R.id.iv_glass_bg, palette.cardBackgroundRes)
            views.setInt(R.id.iv_glass_bg, "setImageAlpha", alpha)
            views.setInt(R.id.iv_icon, "setColorFilter", palette.textColor)
            views.setTextColor(R.id.tv_app_name, palette.textColor)

            val pkg = WidgetPrefs.loadExtra(context, widgetId, EXTRA_PACKAGE, "")
            val label = WidgetPrefs.loadExtra(context, widgetId, EXTRA_LABEL, "选择 App")
            views.setTextViewText(R.id.tv_app_name, label)

            val launchIntent = if (pkg.isNotEmpty()) {
                context.packageManager.getLaunchIntentForPackage(pkg)
            } else null

            // 没选 App 之前点击会打开系统的音乐类默认 App（如果系统能识别）
            val fallbackIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC)
            val targetIntent = launchIntent ?: fallbackIntent

            val pendingIntent = PendingIntent.getActivity(
                context, widgetId, targetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}

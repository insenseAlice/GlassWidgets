package com.alyssa.glasswidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CountdownGlassWidgetProvider : AppWidgetProvider() {

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
        const val EXTRA_TITLE = "countdown_title"
        const val EXTRA_DATE = "countdown_date" // 存 yyyy-MM-dd
        val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_countdown_glass)

            val palette = GreenTheme.get(WidgetPrefs.loadPalette(context, widgetId))
            val alpha = WidgetPrefs.loadAlpha(context, widgetId)
            views.setImageViewResource(R.id.iv_glass_bg, palette.cardBackgroundRes)
            views.setInt(R.id.iv_glass_bg, "setImageAlpha", alpha)
            views.setInt(R.id.iv_leaf, "setColorFilter", palette.textColor)
            views.setTextColor(R.id.tv_countdown_title, palette.secondaryTextColor)
            views.setTextColor(R.id.tv_countdown_days, palette.textColor)

            val title = WidgetPrefs.loadExtra(context, widgetId, EXTRA_TITLE, "目标日")
            val dateStr = WidgetPrefs.loadExtra(context, widgetId, EXTRA_DATE, "")
            views.setTextViewText(R.id.tv_countdown_title, title)

            val targetDate = runCatching { LocalDate.parse(dateStr, DATE_FORMAT) }.getOrNull()
            if (targetDate != null) {
                val today = LocalDate.now()
                val days = ChronoUnit.DAYS.between(today, targetDate)
                views.setTextViewText(
                    R.id.tv_countdown_days,
                    when {
                        days > 0 -> "还有 $days 天"
                        days == 0L -> "就是今天"
                        else -> "已过 ${-days} 天"
                    }
                )
            } else {
                views.setTextViewText(R.id.tv_countdown_days, "未设置")
            }

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}

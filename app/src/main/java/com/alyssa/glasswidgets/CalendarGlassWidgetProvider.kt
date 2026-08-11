package com.alyssa.glasswidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CalendarGlassWidgetProvider : AppWidgetProvider() {

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
            val views = RemoteViews(context.packageName, R.layout.widget_calendar_glass)

            val palette = GreenTheme.get(WidgetPrefs.loadPalette(context, widgetId))
            val alpha = WidgetPrefs.loadAlpha(context, widgetId)
            views.setImageViewResource(R.id.iv_glass_bg, palette.cardBackgroundRes)
            views.setInt(R.id.iv_glass_bg, "setImageAlpha", alpha)
            views.setInt(R.id.iv_leaf, "setColorFilter", palette.textColor)
            views.setTextColor(R.id.tv_month_title, palette.textColor)
            views.setTextColor(R.id.tv_grid, palette.textColor)

            val today = LocalDate.now()
            val yearMonth = YearMonth.from(today)
            views.setTextViewText(R.id.tv_month_title, "${yearMonth.monthValue}月 ${yearMonth.year}")
            views.setTextViewText(R.id.tv_grid, buildMonthGrid(today))

            appWidgetManager.updateAppWidget(widgetId, views)
        }

        private fun buildMonthGrid(today: LocalDate): String {
            val yearMonth = YearMonth.from(today)
            val firstDay = yearMonth.atDay(1)
            val weekHeaders = (1..7).joinToString(" ") { dow ->
                java.time.DayOfWeek.of(dow).getDisplayName(TextStyle.NARROW, Locale.CHINA)
            }

            val sb = StringBuilder()
            sb.append(weekHeaders).append("\n")

            val leadingBlanks = firstDay.dayOfWeek.value - 1
            var column = 0
            repeat(leadingBlanks) {
                sb.append("   ")
                column++
            }

            for (day in 1..yearMonth.lengthOfMonth()) {
                val marker = if (day == today.dayOfMonth) "·${day.toString().padStart(2, ' ')}" else " ${day.toString().padStart(2, ' ')} "
                sb.append(marker)
                column++
                if (column == 7) {
                    sb.append("\n")
                    column = 0
                }
            }
            return sb.toString().trimEnd()
        }
    }
}

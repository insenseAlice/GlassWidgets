package com.alyssa.glasswidgets

import android.content.Context

/**
 * 每个小组件实例（widgetId）单独保存自己的磨砂玻璃透明度（0~255）。
 * 默认值 140，大约 55% 不透明，接近截图中的磨砂效果。
 */
object WidgetPrefs {
    private const val PREFS_NAME = "glass_widget_prefs"
    private const val KEY_PREFIX_ALPHA = "alpha_"
    const val DEFAULT_ALPHA = 140

    fun saveAlpha(context: Context, widgetId: Int, alpha: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_PREFIX_ALPHA + widgetId, alpha)
            .apply()
    }

    fun loadAlpha(context: Context, widgetId: Int): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_PREFIX_ALPHA + widgetId, DEFAULT_ALPHA)
    }

    fun deleteAlpha(context: Context, widgetId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_PREFIX_ALPHA + widgetId)
            .apply()
    }
}

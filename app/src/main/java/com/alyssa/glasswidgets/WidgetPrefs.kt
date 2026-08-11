package com.alyssa.glasswidgets

import android.content.Context

/**
 * 每个小组件实例（widgetId）单独保存：透明度、配色、以及各组件自己的额外文本数据
 * （比如音乐组件选的目标 App 包名、备忘组件写的文字、倒数日的目标日期）。
 */
object WidgetPrefs {
    private const val PREFS_NAME = "glass_widget_prefs"
    private const val KEY_ALPHA = "alpha_"
    private const val KEY_PALETTE = "palette_"
    private const val KEY_EXTRA = "extra_"
    const val DEFAULT_ALPHA = 200
    const val DEFAULT_PALETTE = 0

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveAlpha(context: Context, widgetId: Int, alpha: Int) {
        prefs(context).edit().putInt(KEY_ALPHA + widgetId, alpha).apply()
    }

    fun loadAlpha(context: Context, widgetId: Int): Int {
        return prefs(context).getInt(KEY_ALPHA + widgetId, DEFAULT_ALPHA)
    }

    fun savePalette(context: Context, widgetId: Int, paletteIndex: Int) {
        prefs(context).edit().putInt(KEY_PALETTE + widgetId, paletteIndex).apply()
    }

    fun loadPalette(context: Context, widgetId: Int): Int {
        return prefs(context).getInt(KEY_PALETTE + widgetId, DEFAULT_PALETTE)
    }

    fun saveExtra(context: Context, widgetId: Int, key: String, value: String) {
        prefs(context).edit().putString(KEY_EXTRA + key + "_" + widgetId, value).apply()
    }

    fun loadExtra(context: Context, widgetId: Int, key: String, default: String = ""): String {
        return prefs(context).getString(KEY_EXTRA + key + "_" + widgetId, default) ?: default
    }

    fun deleteAll(context: Context, widgetId: Int) {
        val editor = prefs(context).edit()
        editor.remove(KEY_ALPHA + widgetId)
        editor.remove(KEY_PALETTE + widgetId)
        editor.apply()
    }
}

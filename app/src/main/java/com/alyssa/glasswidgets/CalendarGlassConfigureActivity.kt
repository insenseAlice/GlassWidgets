package com.alyssa.glasswidgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

class CalendarGlassConfigureActivity : Activity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_configure)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val preview = findViewById<ImageView>(R.id.preview_bg)
        val seekBar = findViewById<SeekBar>(R.id.seek_alpha)
        val currentAlpha = WidgetPrefs.loadAlpha(this, widgetId)
        seekBar.progress = currentAlpha
        preview.imageAlpha = currentAlpha

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                preview.imageAlpha = progress
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        findViewById<android.widget.Button>(R.id.btn_save).setOnClickListener {
            WidgetPrefs.saveAlpha(this, widgetId, seekBar.progress)

            val appWidgetManager = AppWidgetManager.getInstance(this)
            CalendarGlassWidgetProvider.updateWidget(this, appWidgetManager, widgetId)

            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}

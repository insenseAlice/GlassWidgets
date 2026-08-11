package com.alyssa.glasswidgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class NoteEditActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_edit)

        val widgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val editText = findViewById<EditText>(R.id.edit_note)
        editText.setText(WidgetPrefs.loadExtra(this, widgetId, NoteGlassWidgetProvider.EXTRA_NOTE, ""))

        findViewById<Button>(R.id.btn_save_note).setOnClickListener {
            WidgetPrefs.saveExtra(this, widgetId, NoteGlassWidgetProvider.EXTRA_NOTE, editText.text.toString())

            val manager = AppWidgetManager.getInstance(this)
            val info = manager.getAppWidgetInfo(widgetId)
            if (info != null) {
                val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                    component = info.provider
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
                }
                sendBroadcast(updateIntent)
            }
            finish()
        }
    }
}

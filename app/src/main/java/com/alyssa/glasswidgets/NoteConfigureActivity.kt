package com.alyssa.glasswidgets

import android.widget.EditText
import android.widget.TextView

class NoteConfigureActivity : BaseThemeConfigureActivity() {

    private lateinit var editText: EditText

    override fun onExtraSetup() {
        val label = TextView(this)
        label.text = "初始文字（之后随时可以点小组件重写）"
        label.setTextColor(0xFF9FC2A6.toInt())
        label.textSize = 14f
        label.setPadding(0, 40, 0, 8)
        extraContainer.addView(label)

        editText = EditText(this)
        editText.hint = "今天的心情、待办、随便一句话都可以"
        editText.setHintTextColor(0xFF9FC2A6.toInt())
        editText.setTextColor(0xFFF4F7F2.toInt())
        editText.setText(WidgetPrefs.loadExtra(this, widgetId, NoteGlassWidgetProvider.EXTRA_NOTE, ""))
        extraContainer.addView(editText)
    }

    override fun onSaveExtra() {
        WidgetPrefs.saveExtra(this, widgetId, NoteGlassWidgetProvider.EXTRA_NOTE, editText.text.toString())
    }
}

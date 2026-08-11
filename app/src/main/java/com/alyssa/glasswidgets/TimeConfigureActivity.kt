package com.alyssa.glasswidgets

import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class TimeConfigureActivity : BaseThemeConfigureActivity() {

    private var fontChoice = "bold"
    private lateinit var boldBtn: Button
    private lateinit var lightBtn: Button

    override fun onExtraSetup() {
        fontChoice = WidgetPrefs.loadExtra(this, widgetId, TimeGlassWidgetProvider.EXTRA_FONT, "bold")

        val label = TextView(this)
        label.text = "字体"
        label.setTextColor(0xFF9FC2A6.toInt())
        label.textSize = 14f
        label.setPadding(0, 40, 0, 8)
        extraContainer.addView(label)

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER

        boldBtn = Button(this).apply {
            text = "圆润"
            setOnClickListener { fontChoice = "bold"; refreshButtons() }
        }
        lightBtn = Button(this).apply {
            text = "细体"
            setOnClickListener { fontChoice = "light"; refreshButtons() }
        }
        val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        row.addView(boldBtn, params)
        row.addView(lightBtn, params)
        extraContainer.addView(row)

        refreshButtons()
    }

    private fun refreshButtons() {
        boldBtn.alpha = if (fontChoice == "bold") 1f else 0.5f
        lightBtn.alpha = if (fontChoice == "light") 1f else 0.5f
    }

    override fun onSaveExtra() {
        WidgetPrefs.saveExtra(this, widgetId, TimeGlassWidgetProvider.EXTRA_FONT, fontChoice)
    }
}

package com.alyssa.glasswidgets

import android.app.DatePickerDialog
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.time.LocalDate

class CountdownConfigureActivity : BaseThemeConfigureActivity() {

    private lateinit var titleEdit: EditText
    private lateinit var dateBtn: Button
    private var selectedDate: LocalDate = LocalDate.now().plusDays(30)

    override fun onExtraSetup() {
        val savedDateStr = WidgetPrefs.loadExtra(this, widgetId, CountdownGlassWidgetProvider.EXTRA_DATE, "")
        runCatching { LocalDate.parse(savedDateStr, CountdownGlassWidgetProvider.DATE_FORMAT) }
            .getOrNull()?.let { selectedDate = it }

        val titleLabel = TextView(this)
        titleLabel.text = "标题（比如：期末考试 / 生日 / 旅行）"
        titleLabel.setTextColor(0xFF9FC2A6.toInt())
        titleLabel.textSize = 14f
        titleLabel.setPadding(0, 40, 0, 8)
        extraContainer.addView(titleLabel)

        titleEdit = EditText(this)
        titleEdit.setText(WidgetPrefs.loadExtra(this, widgetId, CountdownGlassWidgetProvider.EXTRA_TITLE, ""))
        titleEdit.hint = "目标日"
        titleEdit.setHintTextColor(0xFF9FC2A6.toInt())
        titleEdit.setTextColor(0xFFF4F7F2.toInt())
        extraContainer.addView(titleEdit)

        val dateLabel = TextView(this)
        dateLabel.text = "目标日期"
        dateLabel.setTextColor(0xFF9FC2A6.toInt())
        dateLabel.textSize = 14f
        dateLabel.setPadding(0, 24, 0, 8)
        extraContainer.addView(dateLabel)

        dateBtn = Button(this)
        updateDateBtnText()
        dateBtn.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    updateDateBtnText()
                },
                selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth
            ).show()
        }
        extraContainer.addView(dateBtn)
    }

    private fun updateDateBtnText() {
        dateBtn.text = selectedDate.toString()
    }

    override fun onSaveExtra() {
        WidgetPrefs.saveExtra(
            this, widgetId, CountdownGlassWidgetProvider.EXTRA_TITLE,
            titleEdit.text.toString().ifEmpty { "目标日" }
        )
        WidgetPrefs.saveExtra(
            this, widgetId, CountdownGlassWidgetProvider.EXTRA_DATE,
            selectedDate.format(CountdownGlassWidgetProvider.DATE_FORMAT)
        )
    }
}

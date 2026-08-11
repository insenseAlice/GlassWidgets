package com.alyssa.glasswidgets

import android.app.AlertDialog
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

class MusicConfigureActivity : BaseThemeConfigureActivity() {

    private var selectedPackage = ""
    private var selectedLabel = ""
    private lateinit var pickBtn: Button

    override fun onExtraSetup() {
        selectedPackage = WidgetPrefs.loadExtra(this, widgetId, MusicGlassWidgetProvider.EXTRA_PACKAGE, "")
        selectedLabel = WidgetPrefs.loadExtra(this, widgetId, MusicGlassWidgetProvider.EXTRA_LABEL, "")

        val label = TextView(this)
        label.text = "点击跳转的 App"
        label.setTextColor(0xFF9FC2A6.toInt())
        label.textSize = 14f
        label.setPadding(0, 40, 0, 8)
        extraContainer.addView(label)

        pickBtn = Button(this)
        pickBtn.text = if (selectedLabel.isNotEmpty()) "当前：$selectedLabel（点击更换）" else "选择 App"
        pickBtn.setOnClickListener { showAppPicker() }
        extraContainer.addView(pickBtn)
    }

    private fun showAppPicker() {
        val pm = packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(launcherIntent, 0)
            .filter { it.activityInfo.packageName != packageName }
            .distinctBy { it.activityInfo.packageName }
            .sortedBy { it.loadLabel(pm).toString() }

        val labels = apps.map { it.loadLabel(pm).toString() }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, labels)

        AlertDialog.Builder(this)
            .setTitle("选择要跳转的 App")
            .setAdapter(adapter) { _, which ->
                val chosen = apps[which]
                selectedPackage = chosen.activityInfo.packageName
                selectedLabel = chosen.loadLabel(pm).toString()
                pickBtn.text = "当前：$selectedLabel（点击更换）"
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onSaveExtra() {
        WidgetPrefs.saveExtra(this, widgetId, MusicGlassWidgetProvider.EXTRA_PACKAGE, selectedPackage)
        WidgetPrefs.saveExtra(
            this, widgetId, MusicGlassWidgetProvider.EXTRA_LABEL,
            selectedLabel.ifEmpty { "音乐" }
        )
    }
}

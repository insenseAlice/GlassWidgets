package com.alyssa.glasswidgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

/**
 * 所有小组件配置界面的公共基类：配色 + 透明度 + 保存并刷新对应组件。
 * 子类只需要在 onExtraSetup() 里往 extraContainer 塞自己额外需要的输入项
 * （比如音乐组件要选 App，备忘组件要写文字），并在 onSaveExtra() 里保存它们。
 */
abstract class BaseThemeConfigureActivity : Activity() {

    protected var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var selectedPalette = WidgetPrefs.DEFAULT_PALETTE

    protected lateinit var previewBg: ImageView
    protected lateinit var previewLeaf: ImageView
    protected lateinit var extraContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_theme_configure)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        previewBg = findViewById(R.id.preview_bg)
        previewLeaf = findViewById(R.id.preview_leaf)
        extraContainer = findViewById(R.id.extra_container)

        selectedPalette = WidgetPrefs.loadPalette(this, widgetId)
        buildPaletteRow()
        applyPreview()

        val seekBar = findViewById<SeekBar>(R.id.seek_alpha)
        seekBar.progress = WidgetPrefs.loadAlpha(this, widgetId)
        previewBg.imageAlpha = seekBar.progress
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                previewBg.imageAlpha = progress
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        onExtraSetup()

        findViewById<android.widget.Button>(R.id.btn_save).setOnClickListener {
            WidgetPrefs.saveAlpha(this, widgetId, seekBar.progress)
            WidgetPrefs.savePalette(this, widgetId, selectedPalette)
            onSaveExtra()
            pushWidgetUpdate()

            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }

    private fun buildPaletteRow() {
        val row = findViewById<LinearLayout>(R.id.palette_row)
        row.removeAllViews()
        GreenTheme.palettes.forEachIndexed { index, palette ->
            val swatch = ImageView(this)
            val size = (44 * resources.displayMetrics.density).toInt()
            val margin = (8 * resources.displayMetrics.density).toInt()
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(margin, 0, margin, 0)
            swatch.layoutParams = params
            swatch.setBackgroundResource(
                if (index == selectedPalette) R.drawable.swatch_circle_selected else R.drawable.swatch_circle
            )
            swatch.setColorFilter(palette.accentColor, PorterDuff.Mode.SRC_IN)
            swatch.setOnClickListener {
                selectedPalette = index
                buildPaletteRow()
                applyPreview()
            }
            row.addView(swatch)
        }
    }

    private fun applyPreview() {
        val palette = GreenTheme.get(selectedPalette)
        previewBg.setImageResource(palette.cardBackgroundRes)
        previewLeaf.setColorFilter(palette.textColor, PorterDuff.Mode.SRC_IN)
    }

    /** 触发对应 AppWidgetProvider 的刷新，不需要知道具体是哪个 Provider 类 */
    private fun pushWidgetUpdate() {
        val manager = AppWidgetManager.getInstance(this)
        val info = manager.getAppWidgetInfo(widgetId) ?: return
        val updateIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
            component = info.provider
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(widgetId))
        }
        sendBroadcast(updateIntent)
    }

    /** 子类在这里往 extraContainer 加自己的输入控件 */
    protected open fun onExtraSetup() {}

    /** 子类在这里保存 extraContainer 里输入控件的值 */
    protected open fun onSaveExtra() {}
}

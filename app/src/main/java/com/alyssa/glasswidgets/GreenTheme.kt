package com.alyssa.glasswidgets

import android.graphics.Color

/**
 * 护眼绿植主题的统一色板。所有小组件的"换色"功能都从这张表里选，
 * 保证 8 个组件看起来是同一套产品，而不是各画各的。
 */
data class GreenPalette(
    val label: String,
    val cardBackgroundRes: Int,
    val accentColor: Int,
    val textColor: Int = Color.parseColor("#F4F7F2"),
    val secondaryTextColor: Int = Color.parseColor("#D6E4D6")
)

object GreenTheme {

    val palettes = listOf(
        GreenPalette("薄荷", R.drawable.card_bg_mint, Color.parseColor("#DFF5EF")),
        GreenPalette("森林", R.drawable.card_bg_forest, Color.parseColor("#DCEBDD")),
        GreenPalette("鼠尾草", R.drawable.card_bg_sage, Color.parseColor("#EEF3E6")),
        GreenPalette("抹茶", R.drawable.card_bg_matcha, Color.parseColor("#F3F0DC")),
        GreenPalette("墨绿", R.drawable.card_bg_deepgreen, Color.parseColor("#E3EFE8"))
    )

    fun get(index: Int): GreenPalette {
        val safeIndex = index.coerceIn(0, palettes.size - 1)
        return palettes[safeIndex]
    }
}

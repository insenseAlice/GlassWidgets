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
        GreenPalette("墨绿", R.drawable.card_bg_deepgreen, Color.parseColor("#E3EFE8")),
        GreenPalette("竹影", R.drawable.bg_green_bamboo_leaves, Color.parseColor("#F5F5F0"), Color.parseColor("#FFFFFF"), Color.parseColor("#E8E8E0")),
        GreenPalette("叶隙天光", R.drawable.bg_green_bamboo_light, Color.parseColor("#F0F8F0"), Color.parseColor("#FFFFFF"), Color.parseColor("#E0F0E0")),
        GreenPalette("栀子青柠", R.drawable.bg_green_gardenia_lime, Color.parseColor("#F5FFE8"), Color.parseColor("#4A4A3A"), Color.parseColor("#6A6A5A")),
        GreenPalette("清雅百合", R.drawable.bg_green_lily_blue, Color.parseColor("#EDF5FF"), Color.parseColor("#3A4A5A"), Color.parseColor("#5A6A7A")),
        GreenPalette("青柠切面", R.drawable.bg_green_lime_slice, Color.parseColor("#F0FFF0"), Color.parseColor("#3A4A2A"), Color.parseColor("#5A6A4A"))
    )

    fun get(index: Int): GreenPalette {
        val safeIndex = index.coerceIn(0, palettes.size - 1)
        return palettes[safeIndex]
    }
}

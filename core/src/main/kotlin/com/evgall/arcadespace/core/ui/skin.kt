package com.evgall.arcadespace.core.ui

import com.evgall.arcadespace.core.ecs.asset.BitmapFontAsset
import com.evgall.arcadespace.core.ecs.asset.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.list
import ktx.style.skin

enum class LabelStyle {
    DEFAULT,
    GRADIENT
}

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.UI_GRAPHICS.descriptor]
    val gradientFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        label(LabelStyle.DEFAULT.name) {
            font = defaultFont
        }
        label(LabelStyle.GRADIENT.name) {
            font = gradientFont
        }
    }
}
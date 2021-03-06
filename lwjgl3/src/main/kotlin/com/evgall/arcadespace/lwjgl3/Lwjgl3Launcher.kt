package com.evgall.arcadespace.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.evgall.arcadespace.core.Boot

fun main() {
    Lwjgl3Application(Boot(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Arcade Space")
        setWindowedMode(640, 480)
        setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
    })
}
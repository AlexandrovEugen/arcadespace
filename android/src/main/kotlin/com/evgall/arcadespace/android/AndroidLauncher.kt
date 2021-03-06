package com.evgall.arcadespace.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.evgall.arcadespace.core.Boot

class AndroidLauncher: AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(Boot(), AndroidApplicationConfiguration())
    }

}
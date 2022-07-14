package org.river.exertion.desktop

import com.badlogic.gdx.Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import org.river.exertion.KoboldCave

//https://github.com/Quillraven/SimpleKtxGame/wiki
object DesktopLauncher {

    val windowWidth = 1024
    val windowHeight = 576

    @JvmStatic
    fun main(arg: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration().apply {
            setTitle("koboldCave v0.13")
            setWindowedMode(windowWidth, windowHeight)
            setBackBufferConfig(8, 8, 8, 8, 16, 0, 16)
        }
        Lwjgl3Application(KoboldCave(), config).logLevel = Application.LOG_DEBUG
    }
}

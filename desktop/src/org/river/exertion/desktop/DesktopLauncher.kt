package org.river.exertion.desktop

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import org.river.exertion.Game

//https://github.com/Quillraven/SimpleKtxGame/wiki
object DesktopLauncher {

    val windowWidth = 1366
    val windowHeight = 768

    @JvmStatic
    fun main(arg: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration().apply {
            setTitle("koboldCave v0.10")
            setWindowedMode(windowWidth, windowHeight)
            setBackBufferConfig(8, 8, 8, 8, 16, 0, 16)
        }
        Lwjgl3Application(Game(), config).logLevel = Application.LOG_DEBUG
    }
}

package org.river.exertion.assets

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Image
import ktx.assets.getAsset
import ktx.assets.load
import ktx.log.logger

val log = logger<Game>()
/*
// sounds
enum class SoundAssets(val path: String) {
    Drop("sounds/drop.wav")
}

fun AssetManager.load(asset: SoundAssets) = load<Sound>(asset.path)
operator fun AssetManager.get(asset: SoundAssets) = getAsset<Sound>(asset.path)
*/
// music
enum class MusicAssets(val path: String) {
    NavajoNight("music/navajo_clip.wav"),
    DarkMystery("music/mystery_clip.wav")
}

fun AssetManager.load(asset: MusicAssets) = load<Music>(asset.path)
operator fun AssetManager.get(asset: MusicAssets) = getAsset<Music>(asset.path)

// texture atlas
enum class TextureAssets(val path: String) {
    Kobold("images/kobold1.jpg"),
    Cave1("images/cave_bg1.jpg"),
    Cave2("images/cave_bg2.jpg")
}

fun AssetManager.load(asset: TextureAssets) = load<Texture>(asset.path)
operator fun AssetManager.get(asset: TextureAssets) = getAsset<Texture>(asset.path)

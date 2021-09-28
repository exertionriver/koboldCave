package org.river.exertion.assets

import com.badlogic.gdx.Game
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
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
/*
// texture atlas
enum class TextureAtlasAssets(val path: String) {
    Game("images/game.atlas")
}

fun AssetManager.load(asset: TextureAtlasAssets) = load<TextureAtlas>(asset.path)
operator fun AssetManager.get(asset: TextureAtlasAssets) = getAsset<TextureAtlas>(asset.path)
*/
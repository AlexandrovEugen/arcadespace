package com.evgall.arcadespace.core

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Pool
import com.evgall.arcadespace.core.ecs.asset.MusicAsset
import com.evgall.arcadespace.core.ecs.asset.SoundAsset
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import java.util.*
import kotlin.math.max

private val LOG = logger<AudioService>()
private const val MAX_SOUND_INSTANCE = 16

interface AudioService {
    fun play(soundAsset: SoundAsset, volume: Float = 1f)
    fun play(music: MusicAsset, volume: Float = 1f, looped: Boolean = true)
    fun pause()
    fun resume()
    fun stop(clearSounds: Boolean = true)
    fun update()
}


private class SoundRequest : Pool.Poolable {
    lateinit var soundAsset: SoundAsset
    var volume = 1f
    override fun reset() {
        volume = 1f
    }
}


private class SoundRequestPool : Pool<SoundRequest>() {
    override fun newObject() = SoundRequest()
}


class DefaultAudioService(private val assetStorage: AssetStorage) : AudioService {

    private val soundCache = EnumMap<SoundAsset, Sound>(SoundAsset::class.java)
    private val soundRequestPool = SoundRequestPool()
    private val soundRequests = EnumMap<SoundAsset, SoundRequest>(SoundAsset::class.java)
    private var currentMusic: Music? = null
    private var currentMusicAsset = MusicAsset.GAME

    override fun play(soundAsset: SoundAsset, volume: Float) {
        when {
            soundAsset in soundRequests -> {
                //some request is done in one frame multiple times ->
                //play the sound only once with the highest volume of both request
                soundRequests[soundAsset]?.let { request ->
                    request.volume = max(request.volume, volume)
                }
            }
            soundRequests.size >= MAX_SOUND_INSTANCE -> {
                LOG.debug { "Maximum sound request reached" }
                return
            }
            else -> {
                if (soundAsset.descriptor !in assetStorage) {
                    LOG.error { "Trying to play sound that hasn't loaded yet: $soundAsset" }
                    return
                } else if (soundAsset !in soundCache) {
                    soundCache[soundAsset] = assetStorage[soundAsset.descriptor]
                }
                soundRequests[soundAsset] = soundRequestPool.obtain().apply {
                    this.soundAsset = soundAsset
                    this.volume = volume
                }
            }
        }

    }

    override fun play(music: MusicAsset, volume: Float, looped: Boolean) {
        if (currentMusic != null) {
            currentMusic?.stop()
            KtxAsync.launch {
                assetStorage.unload(currentMusicAsset.descriptor)
            }
        }
        val musicDeferred = assetStorage.loadAsync(music.descriptor)
        KtxAsync.launch {
            musicDeferred.join()
            if (assetStorage.isLoaded(music.descriptor)) {
                currentMusicAsset = music
                currentMusic = assetStorage[music.descriptor].apply {
                    this.volume = volume
                    this.isLooping = looped
                    play()
                }
            }
        }
    }

    override fun pause() {
        currentMusic?.pause()
    }

    override fun resume() {
        currentMusic?.play()
    }

    override fun stop(clearSounds: Boolean) {
        currentMusic?.stop()
        if(clearSounds){
            soundRequests.clear()
        }
    }

    override fun update() {
        if (soundRequests.isEmpty().not()) {
            soundRequests.values.forEach { request ->
                soundCache[request.soundAsset]?.play(request.volume)
                soundRequestPool.free(request)
            }
            soundRequests.clear()
        }
    }
}

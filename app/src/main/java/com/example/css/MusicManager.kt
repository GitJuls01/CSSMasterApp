package com.example.css

import android.content.Context
import android.media.MediaPlayer

object MusicManager {

    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, musicResId: Int) {
        stop()

        mediaPlayer = MediaPlayer.create(context.applicationContext, musicResId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }
}

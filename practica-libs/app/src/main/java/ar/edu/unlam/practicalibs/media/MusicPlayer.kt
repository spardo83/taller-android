package ar.edu.unlam.practicalibs.media

import ItunesResult
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class MusicPlayer(progressCallback: (progress: Int) -> Unit) {

    private lateinit var observer: MediaObserver
    private lateinit var player: MediaPlayer
    var currentlyPlaying: ItunesResult? = null

    fun playPause() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.start()
        }
        Thread(observer).start()
    }

    fun stop() {
        player.stop()
        player.reset()
    }

    fun initPlayer(
        album: ItunesResult,
        onCompletionCallback: () -> Unit = fun() {},
        onPreparedCallback: () -> Unit = fun() {}
    ) {
        player.let {
            it.reset()
            it.setDataSource(album.previewUrl)
            it.setOnCompletionListener {
                stop()
                onCompletionCallback()
            }
            it.setOnPreparedListener {
                playerReady(onPreparedCallback)
            }
            it.prepareAsync()
        }
        currentlyPlaying = album
    }

    private fun playerReady(onPreparedCallback: () -> Unit) {
        onPreparedCallback()
    }

    fun isPlaying(): Boolean {
        return this::player.isInitialized && player.isPlaying
    }


    private class MediaObserver(
        val callback: (progress: Int) -> Unit,
        val mediaPlayer: MediaPlayer
    ) :
        Runnable {

        override fun run() {
            while (mediaPlayer.isPlaying) {
                callback((mediaPlayer.currentPosition.toDouble() / mediaPlayer.duration.toDouble() * 100).toInt())
                try {
                    Thread.sleep(200)
                } catch (ex: Exception) {
                    Log.e(MusicPlayer.TAG, "Error sleeping observer thread", ex)
                }
            }
        }
    }

    companion object {
        fun getInstance(progressCallback: (progress: Int) -> Unit): MusicPlayer {
            if (!this::instance.isInitialized) {
                instance = MusicPlayer(progressCallback)
            }
            return instance
        }

        private lateinit var instance: MusicPlayer
        val TAG = MusicPlayer::class.simpleName
    }

    init {
        if (!this::player.isInitialized) {
            player = MediaPlayer()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            observer = MediaObserver(progressCallback, player)
        }
    }

}
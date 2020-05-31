package ar.edu.unlam.practicalibs.media

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import ar.edu.unlam.practicalibs.R
import ar.edu.unlam.practicalibs.utils.hide
import ar.edu.unlam.practicalibs.utils.show

class MusicPlayer {


    private val player = MediaPlayer()
    private lateinit var playerLayout: LinearLayout
    private lateinit var previewProgress: ProgressBar
    private lateinit var loadingPreview: ProgressBar
    private lateinit var play: ImageButton
    private lateinit var previewUrl: String

    private val observer = MediaObserver(previewProgress, player)

    private fun playPause() {
        if (player.isPlaying) {
            player.pause()
            play.setImageResource(R.drawable.ic_play)
        } else {
            player.start()
            play.setImageResource(R.drawable.ic_pause)
        }

        Thread(observer).start()
    }


    private fun stop() {
        play.setImageResource(R.drawable.ic_play)
        player.stop()
        previewProgress.progress = 0

        initPlayer(previewUrl)

    }

    private fun createPlayer() {
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
    }

    private fun preparePlayer(
        previewUrl: String,
        previewProgress: ProgressBar,
        loadingPreview: ProgressBar,
        playerLayout: LinearLayout
    ) {
        this.previewUrl = previewUrl
        this.previewProgress = previewProgress
        this.loadingPreview = loadingPreview
        this.playerLayout = playerLayout

        initPlayer(previewUrl)
    }

    private fun initPlayer(previewUrl: String) {

        player.let {
            it.reset()
            it.setDataSource(previewUrl)
            it.setOnCompletionListener { stop() }
            it.setOnPreparedListener { playerReady() }
            it.prepareAsync()
        }
    }

    private fun playerReady() {
        loadingPreview.hide()
        playerLayout.show()
    }


    private class MediaObserver(
        val progressBar: ProgressBar,
        val mediaPlayer: MediaPlayer
    ) :
        Runnable {

        override fun run() {
            while (mediaPlayer.isPlaying) {
                progressBar.progress =
                    (mediaPlayer.currentPosition.toDouble() / mediaPlayer.duration.toDouble() * 100).toInt()
                try {
                    Thread.sleep(200)
                } catch (ex: Exception) {
                    Log.e(TAG, "Error sleeping observer thread", ex)
                }
            }
        }
    }

    companion object {
        val instance = MusicPlayer()
        val TAG = MusicPlayer::class.simpleName
    }

}
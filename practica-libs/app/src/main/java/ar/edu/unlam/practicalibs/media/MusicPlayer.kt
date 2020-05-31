package ar.edu.unlam.practicalibs.media

import android.media.AudioAttributes
import android.media.MediaPlayer

class MusicPlayer {


    lateinit var player: MediaPlayer

    init {
        if(!this::player.isInitialized){
            player = MediaPlayer()
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }
    }

    companion object {
        val instance = MusicPlayer()
        val TAG = MusicPlayer::class.simpleName
    }

}
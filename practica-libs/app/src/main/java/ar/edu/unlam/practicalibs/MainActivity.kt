package ar.edu.unlam.practicalibs

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ar.edu.unlam.practicalibs.api.Api
import ar.edu.unlam.practicalibs.entity.SearchResult
import ar.edu.unlam.practicalibs.utils.hide
import ar.edu.unlam.practicalibs.utils.hideKeyboard
import ar.edu.unlam.practicalibs.utils.isVisible
import ar.edu.unlam.practicalibs.utils.show
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private var player: MediaPlayer = MediaPlayer()
    private var observer: MediaObserver? = null
    private var currentPreview: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createPlayer()
        searchAction.setOnClickListener { search(searchText.editText?.text.toString()) }
        play.setOnClickListener { playPause() }
        stop.setOnClickListener { stop() }
        observer = MediaObserver(previewProgress, player)

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setIcon(R.mipmap.ic_launcher_foreground)

    }

    private fun search(term: String) {
        hideKeyboard()
        toggleLoading()
        playerLayout.hide()
        Api().search(term, object : Callback<SearchResult> {

            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                Snackbar.make(mainContainer, R.string.no_internet, Snackbar.LENGTH_LONG).show()
                toggleLoading()
            }

            override fun onResponse(call: Call<SearchResult>, response: Response<SearchResult>) {

                when (response.code()) {
                    in 200..299 -> setAlbumValues(response.body()!!)
                    404 -> Toast.makeText(
                        this@MainActivity,
                        R.string.resource_not_found,
                        Toast.LENGTH_LONG
                    )
                        .show()
                    400 -> Toast.makeText(
                        this@MainActivity,
                        R.string.bad_request,
                        Toast.LENGTH_LONG
                    )
                        .show()
                    in 500..599 -> Toast.makeText(
                        this@MainActivity,
                        R.string.server_error,
                        Toast.LENGTH_LONG
                    )
                        .show()
                    else -> Toast.makeText(
                        this@MainActivity,
                        R.string.unknown_error,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }

                toggleLoading(response.isSuccessful)
            }
        })
    }

    private fun setAlbumValues(body: SearchResult) {
        if (body.resultCount > 0) {
            body.results.get(0).let { firstAlbum ->
                artistName.text = firstAlbum.artistName
                albumName.text = firstAlbum.collectionName
                albumPrice.text =
                    getString(R.string.album_price, firstAlbum.collectionPrice, firstAlbum.currency)
                trackPrice.text =
                    getString(R.string.track_price, firstAlbum.trackPrice, firstAlbum.currency)

                Picasso.get()
                    .load(firstAlbum.artworkUrl100)
                    .placeholder(R.drawable.loading_spinner)
                    .error(R.drawable.ic_error)
                    .into(thumbNail)

                currentPreview = firstAlbum.previewUrl
                loadingPreview.show()
                preparePlayer(firstAlbum.previewUrl)
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                R.string.not_found,
                Toast.LENGTH_LONG
            )
                .show()
        }


    }

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
        observer?.stop()
        previewProgress.progress = 0
        preparePlayer(currentPreview.toString())
    }

    private fun toggleLoading(hasResults: Boolean = false) {
        if (progressBar.isVisible()) {
            progressBar.hide()
        } else {
            progressBar.show()
        }

        if (hasResults) {
            dataContainer.show()
        } else {
            dataContainer.hide()
        }
    }

    private fun createPlayer() {
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
    }

    private fun preparePlayer(previewUrl: String) {
        player.let {
            it.reset()
            it.setDataSource(previewUrl)
            it.prepareAsync()
            it.setOnCompletionListener { stop() }
            it.setOnPreparedListener { playerReady() }
        }
    }

    private fun playerReady() {
        loadingPreview.hide()
        playerLayout.show()
    }


    override fun isDestroyed(): Boolean {
        player.release()
        return super.isDestroyed()
    }

    private class MediaObserver(val progressBar: ProgressBar, val mediaPlayer: MediaPlayer) :
        Runnable {
        private val stop: AtomicBoolean = AtomicBoolean(false)
        fun stop() {
            stop.set(true)
        }

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
        val TAG = MainActivity::class.simpleName
    }
}

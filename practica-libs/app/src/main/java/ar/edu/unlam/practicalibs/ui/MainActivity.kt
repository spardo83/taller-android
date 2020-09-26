package ar.edu.unlam.practicalibs.ui

import ItunesResult
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unlam.practicalibs.R
import ar.edu.unlam.practicalibs.adapters.AlbumAdapter
import ar.edu.unlam.practicalibs.api.Api
import ar.edu.unlam.practicalibs.entity.SearchResult
import ar.edu.unlam.practicalibs.media.MusicPlayer
import ar.edu.unlam.practicalibs.utils.hide
import ar.edu.unlam.practicalibs.utils.hideKeyboard
import ar.edu.unlam.practicalibs.utils.isVisible
import ar.edu.unlam.practicalibs.utils.show
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private var currentSearch: SearchResult? = null
    private var currentSearchTerm: String = ""
    private var adapter = AlbumAdapter({ onAlbumPreviewSelected(it) }, { onBuyTrackClick(it) })


    private var musicPlayer = MusicPlayer.getInstance { onProgress(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchAction.setOnClickListener {
            search(searchText.editText?.text.toString())
        }

        orderBySelector.adapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item, AlbumAdapter.Order.values()
        )

        orderBySelector.onItemSelectedListener = onItemSelected()

        play.setOnClickListener { playPause() }
        stop.setOnClickListener { stop() }
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            albumList.layoutManager = LinearLayoutManager(this)
        } else {
            albumList.layoutManager = GridLayoutManager(this, 2)
        }
        albumList.adapter = adapter

        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setIcon(R.mipmap.ic_launcher_foreground)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (currentSearch != null) {
            outState.putString(CURRENT_SEARCH_KEY, Gson().toJson(currentSearch))
        }
        outState.putString(CURRENT_SEARCH_TERM, currentSearchTerm)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(CURRENT_SEARCH_KEY)) {
            val currentSearchJson = savedInstanceState.getString(CURRENT_SEARCH_KEY)
            currentSearch = Gson().fromJson(currentSearchJson, SearchResult::class.java)
            if (currentSearch != null) {
                setAlbumValues(currentSearch!!)
            }
        }
        currentSearchTerm = savedInstanceState.getString(CURRENT_SEARCH_TERM, "")
        searchText.editText?.setText(currentSearchTerm)
        if (musicPlayer.isPlaying()) {
            playerLayout.show()
            play.setImageResource(R.drawable.ic_pause)
            playingSongName.text = getString(
                R.string.current_paying,
                musicPlayer.currentlyPlaying?.artistName,
                musicPlayer.currentlyPlaying?.trackName
            )
        } else {
            playerLayout.hide()
        }
    }

    private fun search(term: String) {
        currentSearchTerm = term
        hideKeyboard()
        toggleLoading()
        Log.i(TAG, "Search method called with term: $term")
        Api().search(term, object : Callback<SearchResult> {

            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                Snackbar.make(
                    mainContainer,
                    R.string.no_internet, Snackbar.LENGTH_LONG
                ).show()
                toggleLoading()
                Log.e(TAG, "Search call failed", t)
            }

            override fun onResponse(
                call: Call<SearchResult>,
                response: Response<SearchResult>
            ) {
                Log.i(
                    TAG,
                    "The response of search call was:\ncode: ${response.code()}\nbody: ${response.body()
                        ?.toString()}"
                )
                when (response.code()) {
                    in 200..299 -> {
                        currentSearch = response.body()!!
                        setAlbumValues(response.body()!!)
                    }
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

                toggleLoading()
            }
        })
    }

    private fun setAlbumValues(body: SearchResult) {
        if (body.resultCount > 0) {
            adapter.albumList = body.results
        } else {
            adapter.albumList = ArrayList()

            Toast.makeText(
                this@MainActivity,
                R.string.not_found,
                Toast.LENGTH_LONG
            )
                .show()
        }

        adapter.notifyDataSetChanged()
    }


    private fun toggleLoading() {
        if (progressBar.isVisible()) {
            progressBar.hide()
        } else {
            progressBar.show()
        }
    }

    private fun onItemSelected() = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            adapter.orderBy(AlbumAdapter.Order.values()[position])
            adapter.notifyDataSetChanged()
        }

    }

    private fun onAlbumPreviewSelected(album: ItunesResult) {
        initPlayer(album)
    }

    private fun playPause() {
        musicPlayer.playPause()
        if (musicPlayer.isPlaying()) {
            play.setImageResource(R.drawable.ic_pause)
        } else {
            play.setImageResource(R.drawable.ic_play)
        }


    }


    private fun stop() {
        musicPlayer.stop()
        play.setImageResource(R.drawable.ic_play)
        previewProgress.progress = 0
    }


    private fun initPlayer(album: ItunesResult) {
        loadingPreview.show()
        musicPlayer.initPlayer(album, { stop() }, { playerReady() })
    }

    private fun playerReady() {
        loadingPreview.hide()
        playerLayout.show()
        playingSongName.text = getString(
            R.string.current_paying,
            musicPlayer.currentlyPlaying?.artistName,
            musicPlayer.currentlyPlaying?.trackName
        )
        playPause()

    }

    private fun onProgress(progress: Int) {
        previewProgress.progress = progress
    }

    private fun onBuyTrackClick(album: ItunesResult) {
        val intent = Intent(this, BuyTrackActivity::class.java)
        intent.putExtra(BuyTrackActivity.ALBUM_PARAM, Gson().toJson(album))
        startActivity(intent)
    }

    companion object {
        val TAG = MainActivity::class.simpleName
        val CURRENT_SEARCH_KEY = "CURRENT_SEARCH_KEY"
        val CURRENT_SEARCH_TERM = "CURRENT_SEARCH_TERM"
    }
}

package ar.edu.unlam.practicalibs

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.unlam.practicalibs.adapters.AlbumAdapter
import ar.edu.unlam.practicalibs.api.Api
import ar.edu.unlam.practicalibs.entity.SearchResult
import ar.edu.unlam.practicalibs.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var currentSearch: SearchResult? = null
    private var currentSearchTerm: String = ""
    private var adapter = AlbumAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchAction.setOnClickListener {
            search(searchText.editText?.text.toString())
        }

        orderByTrack.setOnClickListener {
            adapter.orderBy(AlbumAdapter.Order.TRACK_NAME)
            adapter.notifyDataSetChanged()
        }

        orderByAlbum.setOnClickListener {
            adapter.orderBy(AlbumAdapter.Order.ALBUM_NAME)
            adapter.notifyDataSetChanged()
        }

//        observer = MediaObserver(previewProgress, player)
        albumList.layoutManager = LinearLayoutManager(this)
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
    }

    private fun search(term: String) {
        currentSearchTerm = term
        hideKeyboard()
        toggleLoading()
        Log.i(TAG, "Search method called with term: $term")
        Api().search(term, object : Callback<SearchResult> {

            override fun onFailure(call: Call<SearchResult>, t: Throwable) {
                Snackbar.make(mainContainer, R.string.no_internet, Snackbar.LENGTH_LONG).show()
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

                toggleLoading(response.isSuccessful)
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


    private fun toggleLoading(hasResults: Boolean = false) {
        if (progressBar.isVisible()) {
            progressBar.hide()
        } else {
            progressBar.show()
        }
    }


    companion object {
        val TAG = MainActivity::class.simpleName
        val CURRENT_SEARCH_KEY = "CURRENT_SEARCH_KEY"
        val CURRENT_SEARCH_TERM = "CURRENT_SEARCH_TERM"
    }
}

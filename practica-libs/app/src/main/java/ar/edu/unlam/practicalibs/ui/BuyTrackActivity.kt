package ar.edu.unlam.practicalibs.ui

import ItunesResult
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ar.edu.unlam.practicalibs.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_buy_track.*
import kotlinx.android.synthetic.main.item_album.view.*
import kotlinx.android.synthetic.main.item_album.view.albumName
import kotlinx.android.synthetic.main.item_album.view.albumPrice
import kotlinx.android.synthetic.main.item_album.view.artistName
import kotlinx.android.synthetic.main.item_album.view.songName
import kotlinx.android.synthetic.main.item_album.view.thumbNail
import kotlinx.android.synthetic.main.item_album.view.trackPrice

class BuyTrackActivity : AppCompatActivity() {

    private lateinit var album: ItunesResult
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_track)

        if (intent.extras!!.containsKey(ALBUM_PARAM)) {
            album =
                Gson().fromJson(intent.extras!!.getString(ALBUM_PARAM), ItunesResult::class.java)
        }

        favorites.setOnClickListener { toggleFavorites() }

        sharedPreferences =
            getSharedPreferences(getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE)

        if (this::album.isInitialized) {
            checkFavorite()
            setAlbumValues()
        }

    }

    private fun toggleFavorites() {
        //Agregamos a los favoritos
        val isFavorite = sharedPreferences.getBoolean(album.trackId.toString(), false)
        val editor = sharedPreferences.edit()
        editor.putBoolean(album.trackId.toString(), !isFavorite)
        editor.apply()
        
        checkFavorite()
    }

    private fun checkFavorite() {
        val isFavorite = sharedPreferences.getBoolean(album.trackId.toString(), false)
        if(isFavorite){
            favorites.setImageDrawable(getDrawable(R.drawable.ic_checked_star))
        }else{
            favorites.setImageDrawable(getDrawable(R.drawable.ic_unchecked_favorite))
        }
    }

    private fun setAlbumValues() {

        album.let { album ->
            artistName.text = album.artistName
            albumName.text = album.collectionName
            songName.text = album.trackName
            albumPrice.text =
                getString(
                    R.string.album_price,
                    album.collectionPrice,
                    album.currency
                )
            trackPrice.text =
                getString(
                    R.string.track_price,
                    album.trackPrice,
                    album.currency
                )

            Picasso.get()
                .load(album.artworkUrl100)
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.ic_error)
                .into(thumbNail)
        }
    }

    companion object {
        val ALBUM_PARAM = "ALBUM_PARAM"
    }
}
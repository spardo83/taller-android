package ar.edu.unlam.practicalibs.adapters

import ItunesResult
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import ar.edu.unlam.practicalibs.ItunesApp
import ar.edu.unlam.practicalibs.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_buy_track.*
import kotlinx.android.synthetic.main.item_album.view.*
import kotlinx.android.synthetic.main.item_album.view.favorites

class AlbumAdapter(
    val previewCallback: (album: ItunesResult) -> Unit,
    val onBuyTrackClick: (album: ItunesResult) -> Unit
) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    var albumList = ArrayList<ItunesResult>()

    fun orderBy(filed: Order) {
        when (filed) {
            Order.TRACK_NAME -> {
                albumList.sortBy { it.trackName }
            }
            Order.ALBUM_NAME -> {
                albumList.sortBy { it.collectionName }
            }
            Order.ARTIST_NAME -> {
                albumList.sortBy { it.artistName }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_album, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        albumList[position].let { album ->
            holder.itemView.artistName.text = album.artistName
            holder.itemView.albumName.text = album.collectionName
            holder.itemView.songName.text = album.trackName
            holder.itemView.albumPrice.text =
                holder.itemView.context.getString(
                    R.string.album_price,
                    album.collectionPrice,
                    album.currency
                )
            holder.itemView.trackPrice.text =
                holder.itemView.context.getString(
                    R.string.track_price,
                    album.trackPrice,
                    album.currency
                )

            Picasso.get()
                .load(album.artworkUrl100)
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.ic_error)
                .into(holder.itemView.thumbNail)

            holder.itemView.selectPreview.setOnClickListener {
                previewCallback(album)
            }

            holder.itemView.buyTrack.setOnClickListener {
                onBuyTrackClick(album)
            }

            val context = holder.itemView.context
            val sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.sharedPreferencesKey),
                Context.MODE_PRIVATE
            )

            val isFavorite = sharedPreferences.getBoolean(album.trackId.toString(), false)
            if (isFavorite) {
                holder.itemView.favorites.setImageDrawable(context.getDrawable(R.drawable.ic_checked_star))
            } else {
                holder.itemView.favorites.setImageDrawable(context.getDrawable(R.drawable.ic_unchecked_favorite))
            }

        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    enum class Order(@StringRes val text: Int) {
        TRACK_NAME(R.string.order_by_track),
        ALBUM_NAME(R.string.order_by_album),
        ARTIST_NAME(R.string.order_by_artist);

        override fun toString(): String {
            return ItunesApp.instance.applicationContext.getString(this.text)
        }
    }
}
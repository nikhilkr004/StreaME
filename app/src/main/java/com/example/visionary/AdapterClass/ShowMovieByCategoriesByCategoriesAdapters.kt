package com.example.visionary.AdapterClass

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.TimeUtils.formatDuration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.visionary.Activities.PlayerActivity
import com.example.visionary.DataClass.MovieData
import com.example.visionary.databinding.MovieItemBinding
import com.example.visionary.databinding.ShowMovieByCategoriesItemBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import java.util.concurrent.TimeUnit

class ShowMovieByCategoriesByCategoriesAdapters (private val banners: List<MovieData>) :
    RecyclerView.Adapter<ShowMovieByCategoriesByCategoriesAdapters.BannerViewHolder>() {


    inner class BannerViewHolder(val binding: ShowMovieByCategoriesItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieData) {
            val  context=binding.root.context
            Glide.with(context).load(data.movieImage).into(binding.movieImage)
            binding.movieName.text=data.movieName
            binding.movieCategories.text=data.movieType
            getMediaDuration(context, data.movieVideo){
                durations->
               val formatDurations=formatDuration(durations)
                binding.movieDurations.text=formatDurations.toString()
            }

//            binding.movieDurations.text=duration.toString()
            binding.root.setOnClickListener {
                val intent= Intent(context, PlayerActivity::class.java)
                intent.putExtra("uri",data.movieVideo)
                intent.putExtra("movieName",data.movieName)
                intent.putExtra("videoImage",data.movieImage)
                context.startActivity(intent)
            }
        }
    }

    fun getMediaDuration(context: Context, mediaUrl: String, callback: (Long) -> Unit) {
        val player = ExoPlayer.Builder(context).build()

        val mediaItem = MediaItem.fromUri(Uri.parse(mediaUrl))
        player.setMediaItem(mediaItem)

        player.prepare()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    val duration = player.duration
                    callback(duration)
                    player.release()
                }
            }
        })
    }

    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60

        return String.format("%02d:%02d", hours, minutes)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding= ShowMovieByCategoriesItemBinding.inflate(inflater)
        return BannerViewHolder(binding)

    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        holder.bind(banner)



    }

    override fun getItemCount(): Int = banners.size
}

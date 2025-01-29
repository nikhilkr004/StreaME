package com.example.visionary.AdapterClass

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.visionary.Activities.PlayerActivity
import com.example.visionary.DataClass.MovieData
import com.example.visionary.DataClass.ParentMovie
import com.example.visionary.databinding.AllMovieItemBinding


class MainMovieAdapter(private val banners: List<MovieData>) : RecyclerView.Adapter<MainMovieAdapter.BannerViewHolder>() {


    inner class BannerViewHolder(val binding:AllMovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: MovieData) {
            val  context=binding.root.context
            Glide.with(context).load(data.movieImage).into(binding.movieImage)
            binding.movieName.text=data.movieName.toString()

            binding.root.setOnClickListener {
                val intent=Intent(context,PlayerActivity::class.java)
                intent.putExtra("uri",data.movieVideo)
                intent.putExtra("movieName",data.movieName)
                intent.putExtra("videoImage",data.movieImage)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=AllMovieItemBinding.inflate(inflater)
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = banners[position]
        holder.bind(banner)

    }

    override fun getItemCount(): Int {
     return banners.size
    }
}

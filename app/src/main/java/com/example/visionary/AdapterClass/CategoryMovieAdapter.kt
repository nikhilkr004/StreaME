package com.example.visionary.AdapterClass

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.visionary.Activities.PlayerActivity
import com.example.visionary.DataClass.MovieData
import com.example.visionary.R
import com.example.visionary.databinding.AllMovieItemBinding
import com.example.visionary.databinding.MovieItemBinding

class CategoryMovieAdapter (private var categoryList: List<MovieData>) :
    RecyclerView.Adapter<CategoryMovieAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(private val binding:AllMovieItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: MovieData) {
            val context=binding.root.context

            binding.movieName.text=category.movieName.toString()
            Glide.with(context).load(category.movieImage).into(binding.movieImage)

            binding.root.setOnClickListener {
                val intent= Intent(context, PlayerActivity::class.java)
                intent.putExtra("uri",category.movieVideo)
                intent.putExtra("movieName",category.movieName)
                intent.putExtra("videoImage",category.movieImage)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
      val inflater=LayoutInflater.from(parent.context)
        val binding=AllMovieItemBinding.inflate(inflater)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
      holder.bind(category)
    }

    override fun getItemCount(): Int = categoryList.size
    fun updateList(newList: List<MovieData>) {
        categoryList = newList
        notifyDataSetChanged()
    }

}
package com.example.visionary.AdapterClass

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.visionary.Activities.ShowMovieByCategory
import com.example.visionary.DataClass.Category
import com.example.visionary.databinding.MovieCategoryItemBinding

class CategoryAdapter(private val data:List<Category>):RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    class ViewHolder(val binding:MovieCategoryItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Category) {
            val context=binding.root.context
            binding.categoryName.text=data.text
            Glide.with(context).load(data.imageUrl).into(binding.imageView4)

            binding.root.setOnClickListener {
                val intent=Intent(context,ShowMovieByCategory::class.java)
                intent.putExtra("categories",data.text)
                intent.putExtra("image",data.imageUrl)
                context.startActivity(intent)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=MovieCategoryItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data=data[position]
        holder.bind(data)
    }
}
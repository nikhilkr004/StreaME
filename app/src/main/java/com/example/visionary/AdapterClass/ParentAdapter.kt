package com.example.visionary.AdapterClass

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visionary.DataClass.ParentMovie
import com.example.visionary.databinding.ParentItemBinding

class  ParentAdapter(private val parentList: List<ParentMovie>):RecyclerView.Adapter<ParentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding:ParentItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data: ParentMovie) {
            val context=binding.root.context
            binding.categoryTitle.text=data.title.toString()
            val recyclerView=binding.movieRecyclerView
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
            val adapters=MainMovieAdapter(data.mList)
            recyclerView.adapter=adapters
            adapters.notifyDataSetChanged()


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding= ParentItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return parentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val data=parentList[position]
        holder.bind(data)
    }
}
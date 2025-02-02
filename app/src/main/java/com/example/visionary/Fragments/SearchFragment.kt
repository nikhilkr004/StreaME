package com.example.visionary.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visionary.AdapterClass.CategoryAdapter
import com.example.visionary.AdapterClass.CategoryMovieAdapter
import com.example.visionary.AdapterClass.MainMovieAdapter
import com.example.visionary.DataClass.Category
import com.example.visionary.DataClass.MovieData
import com.example.visionary.databinding.FragmentSearchBinding
import com.google.firebase.firestore.FirebaseFirestore


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var db: FirebaseFirestore
    private var movieDataForSearch= mutableListOf<MovieData>()
    private lateinit var adapters:CategoryMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSearchBinding.inflate(inflater, container, false)

        db = FirebaseFirestore.getInstance()

        val recyclerView = binding.popularRecyclerview
        recyclerView.layoutManager =LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)

        // Initialize adapter outside the listener

         adapters = CategoryMovieAdapter(movieDataForSearch)
        recyclerView.adapter = adapters





        fatchMoview()
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                filterList(p0.toString())
            }
        })
        fatchCategoty()



        return binding.root
    }

    private fun fatchMoview() {


        val collection = db.collection("mediaFiles")

        collection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                println("Error fetching data: ${exception.message}")
                Toast.makeText(requireContext(), "${exception.message} error", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                movieDataForSearch.clear()

                for (document in snapshot.documents) {
                    val dataItem = document.toObject(MovieData::class.java)
                    if (dataItem != null) {
                        movieDataForSearch.add(dataItem)
                    }
                }

                adapters.updateList(movieDataForSearch)

                // Notify adapter after data changes
                adapters.notifyDataSetChanged()
            }
        }
    }

    private fun fatchCategoty() {
        val movieData = ArrayList<Category>()
        val collectionRef = db.collection("Category")
        collectionRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle any errors
                println("Error fetching data: ${exception.message}")

                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {

                movieData.clear()

                for (document in snapshot.documents) {
                    // Convert document to TeacherItem object
                    val teaItem = document.toObject(Category::class.java)
                    if (teaItem != null) {
                        movieData.add(teaItem)

                    }
                }

                setUpAdapter(movieData)


            }
        }
    }


    private fun setUpAdapter(movieData: ArrayList<Category>) {
        val rv = binding.categoryRecyclerview
        rv.layoutManager = GridLayoutManager(requireContext(),2)
        rv.setHasFixedSize(true)

        val adapters = CategoryAdapter(movieData)

        rv.adapter = adapters

    }
    private fun filterList(query: String) {
        val filteredList = movieDataForSearch.filter {
            it.movieName.contains(query, ignoreCase = true)
        }
        adapters.updateList(filteredList)
    }
}

package com.example.visionary.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visionary.AdapterClass.MainMovieAdapter
import com.example.visionary.AdapterClass.MovieAdapter
import com.example.visionary.AdapterClass.ParentAdapter
import com.example.visionary.DataClass.MovieData
import com.example.visionary.DataClass.ParentMovie


import com.example.visionary.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread


class HomeFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()


    private lateinit var bannerAdapter: MainMovieAdapter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var allMovieRecyclerView: RecyclerView
    private val parentList = ArrayList<ParentMovie>()

    companion object {
        val sliderMovieList = mutableListOf<MovieData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        getSliderMovieData()
        setCategories()



        return binding.root


    }


    //////////////////////fetch data for slider /////////////////////////////////
    private fun getSliderMovieData() {
        val recyclerview = binding.sliderRecyclerview
        recyclerview.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val adapters = MovieAdapter(sliderMovieList)

        val collectionRef = db.collection("Banner")
        collectionRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle any errors
                println("Error fetching data: ${exception.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                sliderMovieList.clear() // Clear the existing list

                for (document in snapshot.documents) {
                    // Convert document to TeacherItem object
                    val teaItem = document.toObject(MovieData::class.java)
                    if (teaItem != null) {
                        sliderMovieList.add(teaItem)

                    }
                }

                recyclerview.adapter = adapters
                adapters.notifyDataSetChanged()

            }
        }
    }

    private fun setCategories() {
        allMovieRecyclerView = binding.allMovieRecycler

        allMovieRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        val categories = listOf("Action","Banner","mediaFiles","sadf")// Add all your collection names here


        for (categoryName in categories) {
            db.collection(categoryName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val movies = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(MovieData::class.java)
                    }

                    val category = ParentMovie(categoryName, movies)
                    parentList.add(category)



                    // When all categories are fetched, update the adapter
                    if (parentList.size == categories.size) {
                        updateRecyclerView(parentList)
                    }

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch $categoryName: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }
    }

    private fun updateRecyclerView(parentList: List<ParentMovie>) {



        val adapter=ParentAdapter(parentList)
        allMovieRecyclerView.adapter=adapter

    }


}


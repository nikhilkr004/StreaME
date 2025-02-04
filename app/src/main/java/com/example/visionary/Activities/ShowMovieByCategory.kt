package com.example.visionary.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visionary.AdapterClass.ShowMovieByCategoriesByCategoriesAdapters
import com.example.visionary.DataClass.MovieData
import com.example.visionary.R
import com.example.visionary.databinding.ActivityShowMovieByCategoryBinding
import com.google.firebase.firestore.FirebaseFirestore

class ShowMovieByCategory : AppCompatActivity() {
    private val binding by lazy {
        ActivityShowMovieByCategoryBinding.inflate(layoutInflater)
    }
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()
        val categoriesName = intent.getStringExtra("categories").toString()
        val categoriesImage = intent.getStringExtra("image").toString()

        initalizedBinding(categoriesName)

        getMovieByCategoriesName(categoriesName, categoriesImage)

    }

    private fun initalizedBinding(categoriesName: String) {
        binding.categoriesName.text = categoriesName

        binding.backBtn.setOnClickListener { finish() }

    }

    private fun getMovieByCategoriesName(categoriesName: String, categoriesImage: String) {
        val movieData = ArrayList<MovieData>()
        val recyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)

        val adapters=ShowMovieByCategoriesByCategoriesAdapters(movieData)


        val ref = db.collection(categoriesName)

        ref.addSnapshotListener { snapshot, exectpion ->
            if (exectpion != null) {
                Toast.makeText(this, "${exectpion.message} error", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                movieData.clear()

                for (document in snapshot.documents) {
                    val data = document.toObject(MovieData::class.java)
                    if (data != null) {
                        movieData.add(data)
                    }
                }

                recyclerView.adapter=adapters
                adapters.notifyDataSetChanged()

            }

        }


    }
}
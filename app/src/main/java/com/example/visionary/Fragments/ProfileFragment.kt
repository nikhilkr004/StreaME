package com.example.visionary.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.example.visionary.DataClass.UserData
import com.example.visionary.R
import com.example.visionary.Utils
import com.example.visionary.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        db = FirebaseFirestore.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        getUserData()

        return binding.root
    }


    private fun getUserData() {
        val ref =databaseReference.child("user").child(FirebaseAuth.getInstance().currentUser!!.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val name =snapshot.child("name").value as String
                    val email =snapshot.child("email").value as String
                    val profileImage=snapshot.child("profileImage").value as String
                    binding.userName.text=name
                    binding.userEmail.text=email
                    Glide.with(requireContext()).load(profileImage).placeholder(R.drawable.user_).into(binding.profileImage)
    


                }
                else{
                  Log.d("@@@@@","error to find the user ")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}

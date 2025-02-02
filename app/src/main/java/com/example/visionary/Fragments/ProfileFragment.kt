package com.example.visionary.Fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.visionary.DataClass.UserData
import com.example.visionary.R
import com.example.visionary.Utils
import com.example.visionary.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.util.UUID


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var databaseReference: DatabaseReference
    private var profileImageUri: Uri? = null
    private lateinit var storage: StorageReference
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
        storage = FirebaseStorage.getInstance().reference

        getUserData()

        binding.editProfile.setOnClickListener {
            editProfile(profileImageUri)
        }


        binding


        return binding.root
    }

    private fun editProfile(profileImageUri: Uri?) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.profile_edit_dialog, null)
        val saveBtn = view.findViewById<TextView>(R.id.editprofile)
        val cancel = view.findViewById<TextView>(R.id.cencel)
        val profileImage = view.findViewById<CircleImageView>(R.id.profile_image)
        val name: EditText = view.findViewById(R.id.name)


        ///// show previous uer info
        val ref = databaseReference.child("user").child(Utils.currentUserId())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val data = snapshot.getValue(UserData::class.java)
                if (data != null) {
                    name.setText(data.name)

                    if (profileImageUri == null) Glide.with(requireContext())
                        .load(data.profileImage)
                        .placeholder(R.drawable.user_).into(profileImage)
                    else Glide.with(requireContext()).load(profileImageUri).into(profileImage)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        cancel.setOnClickListener {
            dialog.dismiss()
        }

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)

        }

        saveBtn.setOnClickListener {
            val newName = name.text.toString()
            if (newName.isNotEmpty()) {
                uploadImageToFirebase(newName, dialog)
                Utils.showDialog(requireContext(), "saving info...")


            } else {
                Utils.hideDialog()
                Toast.makeText(
                    requireContext(),
                    "Please select an image and enter a name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }




        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun uploadImageToFirebase(newName: String, dialog: BottomSheetDialog) {

        if (profileImageUri != null) {
            val imageFileName = UUID.randomUUID().toString() + ".jpg"
            val imageRef = storage.child("profile_images/$imageFileName")

            imageRef.putFile(profileImageUri!!)
                .addOnSuccessListener {
                    // Get the download URL of the uploaded image
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveUserDataToDatabase(newName, uri.toString(), dialog)
                    }
                }
                .addOnFailureListener {
                    Utils.hideDialog()
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun saveUserDataToDatabase(name: String, imageUrl: String, dialog: BottomSheetDialog) {
        val userData = mapOf(
            "name" to name,
            "profileImage" to imageUrl
        )
        databaseReference.child("user").child(Utils.currentUserId()).updateChildren(userData)
            .addOnSuccessListener {

                Utils.hideDialog()
                dialog.dismiss()
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT)
                    .show()

            }
            .addOnFailureListener {
                Utils.hideDialog()
            }
    }


    private fun getUserData() {
        val ref =
            databaseReference.child("user").child(FirebaseAuth.getInstance().currentUser!!.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val data = snapshot.getValue(UserData::class.java)


                    if (data != null) {
                        binding.userName.text = data.name
                        binding.userEmail.text = data.email
                        Glide.with(requireContext()).load(data.profileImage)
                            .placeholder(R.drawable.user_).into(binding.profileImage)
                    }


                } else {
                    Log.d("@@@@@", "error to find the user ")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    /// upload image on storage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            profileImageUri = data.data
            editProfile(profileImageUri)

        }
    }

}

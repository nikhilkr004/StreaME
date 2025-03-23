package com.example.visionary.Fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ActivityManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Debug
import android.os.Parcel
import android.os.Parcelable
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
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.UUID


class ProfileFragment() : Fragment(), PaymentResultWithDataListener {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var databaseReference: DatabaseReference
    private var profileImageUri: Uri? = null
    private lateinit var storage: StorageReference

    constructor(parcel: Parcel) : this() {
        profileImageUri = parcel.readParcelable(Uri::class.java.classLoader)
    }

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

        val memoryUsageMB = getMemoryUsageInMB()

        binding.userName.text=memoryUsageMB.toString()


////payment
        Checkout.preload(requireContext())
        val co = Checkout()
        // apart from setting it in AndroidManifest.xml, keyId can also be set
        // programmatically during runtime
        co.setKeyID("rzp_test_N9hgXP1L6tCGPm")


        binding.pay.setOnClickListener {
            initPayment()
        }



        return binding.root
    }

    private fun initPayment() {
        val activity: Activity = requireActivity()
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Visionary")
            options.put("description","Movie Streaming app")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image","http://example.com/image/rzp.jpg")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
//            options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount","10")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","nikhilabc860@gmail.com")
            prefill.put("contact","9876543210")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
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
                        saveUserDataToDatabase(newName,uri.toString(), dialog)
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

    fun getMemoryUsageInMB(): Double {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)

        // Get total private dirty memory usage in KB
        val totalMemoryUsageKB = memoryInfo.totalPrivateDirty

        // Convert KB to MB
        return totalMemoryUsageKB / 1024.0
    }


    companion object CREATOR : Parcelable.Creator<ProfileFragment> {
        override fun createFromParcel(parcel: Parcel): ProfileFragment {
            return ProfileFragment(parcel)
        }

        override fun newArray(size: Int): Array<ProfileFragment?> {
            return arrayOfNulls(size)
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        val ref= mapOf(
            "subscribe" to "true"
        )

        databaseReference.child("user").child(Utils.currentUserId()).updateChildren(ref).addOnSuccessListener {
            Toast.makeText(requireContext(), "Success..", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Toast.makeText(requireContext(), "Error..", Toast.LENGTH_SHORT).show()
    }

}

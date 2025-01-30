package com.example.visionary

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater

import com.example.visionary.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth

object Utils {
    private val auth = FirebaseAuth.getInstance()

    private var dialog: android.app.AlertDialog? = null
    fun showDialog(context: Context, message: String) {
        val process = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        process.text.text = message.toString()

        dialog = AlertDialog.Builder(context).setView(process.root).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog() {
        dialog!!.dismiss()
    }
    fun currentUserId(): String {
        return auth.currentUser!!.uid
    }
}
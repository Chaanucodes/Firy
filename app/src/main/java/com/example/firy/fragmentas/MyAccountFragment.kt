package com.example.firy.fragmentas

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.firy.R
import com.example.firy.SignInActivity
import com.example.firy.databinding.FragmentMyAccountBinding
import com.example.firy.gliding.GlideApp
import com.example.firy.util.FirestoreUtil
import com.example.firy.util.StorageUtil
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_my_account.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class MyAccountFragment : Fragment() {

    private lateinit var binding: FragmentMyAccountBinding
    private val SIGN_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account, container, false)

        binding.apply {
            imageViewProfilePicture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    SIGN_SELECT_IMAGE
                )
            }

            buttonSave.setOnClickListener {
                if (::selectedImageBytes.isInitialized) {
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) {
                        FirestoreUtil.updateCurrentUser(
                            editTextName.text.toString(),
                            editTextBio.text.toString(),
                            it
                        )
                    }
                } else {
                        FirestoreUtil.updateCurrentUser(
                            editTextName.text.toString(),
                            editTextBio.text.toString(),
                            null
                        )

                }
                Toast.makeText(activity, "Changes Saved", Toast.LENGTH_SHORT).show()
            }
            buttonSignOut.setOnClickListener {
                AuthUI.getInstance()
                    .signOut(this@MyAccountFragment.context!!)
                    .addOnCompleteListener {
                        startActivity(Intent(activity, SignInActivity::class.java).
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    }
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            val selectedImageBmp =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()


            GlideApp.with(this)
                .load(selectedImageBytes)
                .into(imageViewProfilePicture)
            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()

        FirestoreUtil.getCurrentUser {
            if (this.isVisible){
                editTextName.setText(it?.name)
                editTextBio.setText(it?.bio)
                if(!pictureJustChanged && it?.profilePicturePath != null)
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(it.profilePicturePath))
                        .placeholder(R.drawable.ic_my_acc_nav)
                        .into(imageViewProfilePicture)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = "SPLASH (beta)"
    }

}
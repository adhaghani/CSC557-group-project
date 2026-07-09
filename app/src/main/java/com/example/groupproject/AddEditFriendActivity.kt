package com.example.groupproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.groupproject.data.Friend
import com.example.groupproject.databinding.ActivityAddEditFriendBinding
import com.example.groupproject.viewmodel.FriendViewModel
import kotlinx.coroutines.launch

class AddEditFriendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditFriendBinding
    private lateinit var viewModel: FriendViewModel
    private var editingFriendId: Int? = null
    private var selectedPhotoPath: String? = null
    private var photoUri: Uri? = null
    private lateinit var states: Array<String>

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
            binding.ivPhoto.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FriendViewModel::class.java]

        // Set up the state dropdown with all 14 Malaysian states
        states = resources.getStringArray(R.array.malaysian_states)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, states)
        binding.etAddress4.setAdapter(adapter)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editingFriendId = intent.getIntExtra("friend_id", -1).takeIf { it != -1 }

        binding.btnPickPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveFriend()
        }

        setupScrollOnFocus(
            binding.etName,
            binding.etPhone,
            binding.etEmail,
            binding.etAddress1,
            binding.etAddress2,
            binding.etAddress3,
            binding.etAddress4
        )

        if (editingFriendId != null) {
            supportActionBar?.title = "Edit Buddy"
            loadExistingFriend()
        } else {
            supportActionBar?.title = "Add Buddy"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupScrollOnFocus(vararg views: View) {
        views.forEach { view ->
            view.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.scrollView.postDelayed({
                        binding.scrollView.smoothScrollTo(0, v.bottom)
                    }, 200)
                }
            }
        }
    }

    private fun loadExistingFriend() {
        lifecycleScope.launch {
            val friend = viewModel.getById(editingFriendId!!)
            friend?.let { populateFields(it) }
        }
    }

    private fun populateFields(friend: Friend) {
        binding.etName.setText(friend.name)
        if (friend.gender == "Female") {
            binding.rbFemale.isChecked = true
        } else {
            binding.rbMale.isChecked = true
        }
        binding.etPhone.setText(friend.phoneNumber)
        binding.etEmail.setText(friend.emailAddress)
        binding.etAddress1.setText(friend.addressLine1)
        binding.etAddress2.setText(friend.addressLine2)
        binding.etAddress3.setText(friend.addressLine3)
        binding.etAddress4.setText(friend.addressLine4)
        selectedPhotoPath = friend.photoPath

        friend.photoPath?.let { path ->
            binding.ivPhoto.setImageURI(Uri.parse("file://$path"))
        }
    }

    private fun saveFriend() {
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.name_required, Toast.LENGTH_SHORT).show()
            return
        }

        val gender = if (binding.rbFemale.isChecked) "Female" else "Male"
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val address1 = binding.etAddress1.text.toString().trim()
        val address2 = binding.etAddress2.text.toString().trim()
        val address3 = binding.etAddress3.text.toString().trim()
        val address4 = binding.etAddress4.text.toString().trim()

        // Save image if a new one was picked
        var photoPath = selectedPhotoPath
        if (photoUri != null) {
            photoPath = viewModel.saveImage(photoUri!!)
        }

        val friend = Friend(
            id = editingFriendId ?: 0,
            name = name,
            gender = gender,
            phoneNumber = phone,
            emailAddress = email,
            photoPath = photoPath,
            addressLine1 = address1,
            addressLine2 = address2,
            addressLine3 = address3,
            addressLine4 = address4
        )

        if (editingFriendId != null) {
            viewModel.update(friend)
        } else {
            viewModel.insert(friend)
        }

        finish()
    }
}

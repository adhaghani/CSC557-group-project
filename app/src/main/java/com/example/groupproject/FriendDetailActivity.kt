package com.example.groupproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.groupproject.databinding.ActivityFriendDetailBinding
import com.example.groupproject.viewmodel.FriendViewModel
import kotlinx.coroutines.launch

class FriendDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendDetailBinding
    private lateinit var viewModel: FriendViewModel
    private var friendId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FriendViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        friendId = intent.getIntExtra("friend_id", -1)
        if (friendId == -1) {
            finish()
            return
        }

        loadFriend()

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, AddEditFriendActivity::class.java)
            intent.putExtra("friend_id", friendId)
            startActivity(intent)
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        if (friendId != -1) {
            loadFriend()
        }
    }

    private fun loadFriend() {
        lifecycleScope.launch {
            val friend = viewModel.getById(friendId)
            if (friend == null) {
                finish()
                return@launch
            }

            binding.tvName.text = friend.name
            binding.tvGender.text = friend.gender
            binding.tvPhone.text = friend.phoneNumber.ifEmpty { "-" }
            binding.tvEmail.text = friend.emailAddress.ifEmpty { "-" }
            binding.tvAddress1.text = friend.addressLine1.ifEmpty { "-" }
            binding.tvAddress2.text = friend.addressLine2.ifEmpty { "-" }
            binding.tvAddress3.text = friend.addressLine3.ifEmpty { "-" }
            binding.tvAddress4.text = friend.addressLine4.ifEmpty { "-" }

            if (!friend.photoPath.isNullOrEmpty()) {
                binding.ivPhoto.setImageURI(Uri.parse("file://${friend.photoPath}"))
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    viewModel.getById(friendId)?.let { friend ->
                        viewModel.delete(friend)
                    }
                    finish()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}

package com.example.groupproject.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.groupproject.R
import com.example.groupproject.data.Friend
import com.example.groupproject.databinding.ItemFriendBinding
import java.io.File

class FriendAdapter(
    private val onItemClick: (Friend) -> Unit
) : ListAdapter<Friend, FriendAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: Friend) {
            binding.tvName.text = friend.name
            binding.tvPhone.text = friend.phoneNumber

            if (!friend.photoPath.isNullOrEmpty()) {
                val file = File(friend.photoPath)
                if (file.exists()) {
                    Glide.with(binding.root.context)
                        .load(file)
                        .circleCrop()
                        .into(binding.ivPhoto)
                } else {
                    binding.ivPhoto.setImageResource(R.drawable.ic_photo_placeholder)
                }
            } else {
                binding.ivPhoto.setImageResource(R.drawable.ic_photo_placeholder)
            }

            binding.root.setOnClickListener {
                onItemClick(friend)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }
}

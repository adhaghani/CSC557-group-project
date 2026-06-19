package com.example.groupproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupproject.adapter.FriendAdapter
import com.example.groupproject.data.Friend
import com.example.groupproject.databinding.ActivityMainBinding
import com.example.groupproject.viewmodel.FriendViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FriendViewModel
    private lateinit var adapter: FriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this)[FriendViewModel::class.java]
        setupRecyclerView()
        setupFab()
        setupSearch()

        viewModel.displayedFriends.observe(this) { friends ->
            adapter.submitList(friends)
            updateEmptyState(friends)
        }
    }

    private fun setupRecyclerView() {
        adapter = FriendAdapter { friend ->
            val intent = Intent(this, FriendDetailActivity::class.java)
            intent.putExtra("friend_id", friend.id)
            startActivity(intent)
        }

        binding.recyclerFriends.layoutManager = LinearLayoutManager(this)
        binding.recyclerFriends.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddEditFriendActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun updateEmptyState(friends: List<Friend>?) {
        if (friends.isNullOrEmpty()) {
            binding.recyclerFriends.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerFriends.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reports -> {
                startActivity(Intent(this, ReportsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
}

package com.example.groupproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.groupproject.databinding.ActivityRegisterBinding
import com.example.groupproject.viewmodel.AuthViewModel
import com.example.groupproject.viewmodel.RegisterResult

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            viewModel.register(username, email, password, confirmPassword)
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }

        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterResult.Success -> {
                    Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show()
                    viewModel.resetRegisterResult()
                    finish()
                }
                is RegisterResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetRegisterResult()
                }
                null -> {}
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !loading
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

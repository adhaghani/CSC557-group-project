package com.example.groupproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.groupproject.databinding.ActivityLoginBinding
import com.example.groupproject.viewmodel.AuthViewModel
import com.example.groupproject.viewmodel.LoginResult

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setSupportActionBar(binding.toolbar)

        // Skip to main if already logged in
        if (viewModel.isLoggedIn) {
            navigateToMain()
            return
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            viewModel.login(username, password)
        }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    navigateToMain()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetLoginResult()
                }
                null -> {}
            }
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !loading
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

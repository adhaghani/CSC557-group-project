package com.example.groupproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.groupproject.data.AuthRepository
import com.example.groupproject.util.PasswordUtils
import com.example.groupproject.util.SessionManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)
    private val sessionManager = SessionManager(application)

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val isLoggedIn: Boolean get() = sessionManager.isLoggedIn

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginResult.value = LoginResult.Error("Username and password are required")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = withContext(Dispatchers.IO) {
                    repository.getUserByUsername(username)
                }
                if (user == null || !PasswordUtils.verifyPassword(password, user.salt, user.passwordHash)) {
                    _loginResult.value = LoginResult.Error("Invalid username or password")
                } else {
                    sessionManager.isLoggedIn = true
                    sessionManager.loggedInUserId = user.id
                    sessionManager.loggedInUsername = user.username
                    _loginResult.value = LoginResult.Success
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Login error", e)
                _loginResult.value = LoginResult.Error("An error occurred. Please try again.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(username: String, email: String, password: String, confirmPassword: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _registerResult.value = RegisterResult.Error("All fields are required")
            return
        }
        if (password != confirmPassword) {
            _registerResult.value = RegisterResult.Error("Passwords do not match")
            return
        }
        if (password.length < 6) {
            _registerResult.value = RegisterResult.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usernameTaken = withContext(Dispatchers.IO) {
                    repository.isUsernameTaken(username)
                }
                if (usernameTaken) {
                    _registerResult.value = RegisterResult.Error("Username is already taken")
                    return@launch
                }
                val emailTaken = withContext(Dispatchers.IO) {
                    repository.isEmailTaken(email)
                }
                if (emailTaken) {
                    _registerResult.value = RegisterResult.Error("Email is already registered")
                    return@launch
                }
                withContext(Dispatchers.IO) {
                    repository.registerUser(username, email, password)
                }
                _registerResult.value = RegisterResult.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Register error", e)
                _registerResult.value = RegisterResult.Error("An error occurred. Please try again.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun resetLoginResult() {
        _loginResult.value = null
    }

    fun resetRegisterResult() {
        _registerResult.value = null
    }
}

sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class RegisterResult {
    object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

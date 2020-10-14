package com.aldemir.mesanews.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.aldemir.mesanews.R
import com.aldemir.mesanews.Resource
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.api.model.RequestLogin
import com.aldemir.mesanews.data.api.model.ResponseLogin
import com.aldemir.mesanews.data.repository.login.LoginRepositoryImpl
import com.aldemir.mesanews.ui.register.domain.User
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val loginRepository: LoginRepositoryImpl,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _user = MutableLiveData<User>()
    var user: LiveData<User> = _user

    private val _userCreated= MutableLiveData<Boolean>()
    var userCreated: LiveData<Boolean> = _userCreated

    private val _token = MutableLiveData<Resource<ResponseLogin>>()
    var mToken: LiveData<Resource<ResponseLogin>> = _token

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginForm: LiveData<LoginFormState> = _loginForm

    fun signIn(username: String, passwords: String) {
        val requestLogin = RequestLogin(username, passwords)

        viewModelScope.launch {
            try {
                val result = loginRepository.signIn(requestLogin)

                _token.value = (Resource.success(result))
                updateUser(username)

            }catch (error: HttpException){
                if (error.code() == 401) {
                    _token.value = (Resource.error("Verifique suas credencias e tente novamente", null))
                }else {
                    _token.value = (Resource.error("Erro no servidor, tente novamente.", null))
                }
                Log.e("facebookLogin: ", "ERROR => : ${error}")
            }
        }

    }

    private fun updateUser(email: String) {
        sessionManager.saveUserName(email)
        val user = loginRepository.getUserByEmail(email)
        if (user == null) {
            insertUser(email)
        }else {
            user.isLogged = true
            loginRepository.updateUser(user)
        }
    }

    private fun insertUser(email: String) {
        val user = User(0, "", email, true)
        loginRepository.insertUser(user)
    }

    private fun isLoggedUser(email: String) {
        val userLogged = loginRepository.getUserLogged(email, true)
        _user.value = userLogged
    }


    fun saveTokenSharedPreference(token: String){
        sessionManager.saveAuthToken(token)
    }

    fun getUserNameSharedPreference() {
        val userName: String? = sessionManager.getUserName()

        if (userName != null) {
            _userCreated.value = true
            isLoggedUser(userName)
        }else {
            _userCreated.value = false
        }
    }

    fun userNameDataChanged(username: String) {
        if (!isUserNameValid(username)) {
            LoginActivity.isUserValid = false
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else {
            LoginActivity.isUserValid = true
            _loginForm.value = LoginFormState(isLoginDataValid = true)
        }
    }

    fun passwordDataChanged(password: String) {
        if (!isPasswordValid(password)) {
            LoginActivity.isPasswordValid = false
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            LoginActivity.isPasswordValid = true
            _loginForm.value = LoginFormState(isLoginDataValid = true)

        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
package com.aldemir.mesanews.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.aldemir.mesanews.R
import com.aldemir.mesanews.Resource
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.model.RequestRegister
import com.aldemir.mesanews.data.model.ResponseLogin
import com.aldemir.mesanews.data.repository.register.RegisterRepository
import com.aldemir.mesanews.ui.register.domain.User
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(
    private val registerRepository: RegisterRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _token = MutableLiveData<Resource<ResponseLogin>>()
    var mToken: LiveData<Resource<ResponseLogin>> = _token

    private val _loginForm = MutableLiveData<RegisterFormState>()
    val loginForm: LiveData<RegisterFormState> = _loginForm

    fun signUp(name: String, username: String, passwords: String) {
        val requestRegister = RequestRegister(name, username, passwords)

        viewModelScope.launch {
            try {
                val result = registerRepository.signUp(requestRegister)

                Log.d("RegisterViewModel: ", "token => : ${result.token}")
                _token.value = (Resource.success(result))

                val user = User(0, name, username, true)
                insertUser(user)

            }catch (error: HttpException){
                if (error.code() == 422) {
                    _token.value = (Resource.error("Email já cadastrado.", null))
                }else {
                    _token.value = (Resource.error("Erro no servidor, tente novamente.", null))
                }
                Log.e("RegisterViewModel: ", "ERROR => : ${error.code()}")
            }
        }

    }

    private fun insertUser(user: User) {
        val userId = registerRepository.insertUser(user)
        sessionManager.saveUserId(userId)
        sessionManager.saveUserName(user.email)
        sessionManager.saveName(user.name)

    }

    fun saveToken(token: String){
        sessionManager.saveAuthToken(token)
    }

    fun userNameChanged(name: String) {
        if (!isNameValid(name)) {
            RegisterActivity.isNameValid = false
            _loginForm.value = RegisterFormState(usernameError = R.string.invalid_name)
        } else {
            RegisterActivity.isNameValid = true
            _loginForm.value = RegisterFormState(isLoginDataValid = true)
        }
    }

    fun userEmailChanged(email: String) {
        if (!isEmailValid(email)) {
            RegisterActivity.isEmailValid = false
            _loginForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else {
            RegisterActivity.isEmailValid = true
            _loginForm.value = RegisterFormState(isLoginDataValid = true)
        }
    }

    fun passwordDataChanged(password: String) {
        if (!isPasswordValid(password)) {
            RegisterActivity.isPasswordValid = false
            _loginForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else {
            RegisterActivity.isPasswordValid = true
            _loginForm.value = RegisterFormState(isLoginDataValid = true)

        }
    }

    fun passwordConfirmDataChanged(password: String, passwordConfirm: String) {
        if (!isPasswordConfirm(password, passwordConfirm)) {
            RegisterActivity.isPasswordConfirm = false
            _loginForm.value = RegisterFormState(passwordConfirmError = R.string.confirm_password)
        } else {
            RegisterActivity.isPasswordConfirm = true
            _loginForm.value = RegisterFormState(isLoginDataValid = true)

        }
    }

    private fun isNameValid(name: String): Boolean {
        return name.length > 5
    }

    private fun isEmailValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isPasswordConfirm(password: String, passwordConfirm: String): Boolean {
        Log.e("ConfirmarSenha", "senha1: $password senha2: $passwordConfirm = ${password == passwordConfirm}")
        return password == passwordConfirm
    }
}
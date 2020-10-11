package com.aldemir.mesanews.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.repository.register.RegisterRepository
import com.aldemir.mesanews.ui.register.domain.User

class MainViewModel(
    private val registerRepository: RegisterRepository,
    private val sessionManager: SessionManager
): ViewModel() {

    private val _userLogged = MutableLiveData<User>()
    var userLogged: LiveData<User> = _userLogged

    private val _userEmail = MutableLiveData<String>()
    var userEmail: LiveData<String> = _userEmail

    fun getUserLogged(email: String) {
        val user = registerRepository.getUserLogged(email, true)
        _userLogged.value = user
    }

    fun logout(email: String) {
        val user = registerRepository.getUserEmail(email)
        user.isLogged = false

        registerRepository.updateUser(user)
    }
    fun getUserNameSharedPreference() {
        _userEmail.value = sessionManager.getUserName()
    }

}
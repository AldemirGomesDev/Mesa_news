package com.aldemir.mesanews.data.repository.login

import com.aldemir.mesanews.data.api.model.RequestLogin
import com.aldemir.mesanews.data.api.model.ResponseLogin
import com.aldemir.mesanews.ui.register.domain.User

interface LoginRepository {

    suspend fun signIn(requestLogin: RequestLogin): ResponseLogin
    fun insertUser(user: User): Long
    fun updateUser(user: User)
    fun getUserLogged(email: String, isLogged: Boolean): User
    fun getUserByEmail(email: String): User
}
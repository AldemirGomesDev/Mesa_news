package com.aldemir.mesanews.data.repository.register

import com.aldemir.mesanews.data.model.RequestRegister
import com.aldemir.mesanews.data.model.ResponseLogin
import com.aldemir.mesanews.ui.register.domain.User

interface RegisterRepository {
    suspend fun signUp(requestRegister: RequestRegister): ResponseLogin
    fun insertUser(user: User): Long
    fun updateUser(user: User)
    fun getUserLogged(email: String, isLogged: Boolean): User
    fun getUserEmail(email: String): User
}
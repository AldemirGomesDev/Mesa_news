package com.aldemir.mesanews.data.repository.register

import com.aldemir.mesanews.data.api.model.RequestRegister
import com.aldemir.mesanews.data.api.model.ResponseLogin
import com.aldemir.mesanews.ui.register.domain.Post
import com.aldemir.mesanews.ui.register.domain.User
import io.reactivex.Single

interface RegisterRepository {
    suspend fun signUp(requestRegister: RequestRegister): ResponseLogin
    fun insertUser(user: User): Long
    fun updateUser(user: User)
    fun getUserLogged(email: String, isLogged: Boolean): User
    fun getUserEmail(email: String): User
    fun observePosts(): Single<List<Post>>
}
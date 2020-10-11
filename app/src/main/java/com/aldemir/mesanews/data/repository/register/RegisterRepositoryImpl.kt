package com.aldemir.mesanews.data.repository.register

import com.aldemir.mesanews.data.api.login.ApiService
import com.aldemir.mesanews.data.database.UserDao
import com.aldemir.mesanews.data.model.RequestRegister
import com.aldemir.mesanews.data.model.ResponseLogin
import com.aldemir.mesanews.ui.register.domain.User

class RegisterRepositoryImpl(private val apiService: ApiService, private val userDao: UserDao): RegisterRepository {
    override suspend fun signUp(requestRegister: RequestRegister): ResponseLogin {
        return apiService.sinUp(requestRegister)
    }

    override fun insertUser(user: User): Long {
       return userDao.insert(user)
    }

    override fun updateUser(user: User) {
        userDao.update(user)
    }

    override fun getUserLogged(email: String, isLogged: Boolean): User {
        return userDao.getUserLogged(email, isLogged)
    }

    override fun getUserEmail(email: String): User {
       return userDao.getUserEmail(email)
    }
}
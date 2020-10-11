package com.aldemir.mesanews.data.repository.login

import com.aldemir.mesanews.data.api.login.ApiService
import com.aldemir.mesanews.data.database.UserDao
import com.aldemir.mesanews.data.model.RequestLogin
import com.aldemir.mesanews.data.model.ResponseLogin
import com.aldemir.mesanews.ui.register.domain.User

class LoginRepositoryImpl(private val apiService: ApiService, private val userDao: UserDao):
    LoginRepository {

    override suspend fun signIn(requestLogin: RequestLogin): ResponseLogin {
       return apiService.sinIn(requestLogin)
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

    override fun getUserByEmail(email: String): User {
        return userDao.getUserEmail(email)
    }

}
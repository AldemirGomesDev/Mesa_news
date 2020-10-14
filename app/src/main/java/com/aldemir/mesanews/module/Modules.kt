package com.aldemir.mesanews.module

import android.app.Application
import androidx.room.Room
import com.aldemir.mesanews.data.api.AuthInterceptor
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.api.ApiService
import com.aldemir.mesanews.data.database.NewDao
import com.aldemir.mesanews.data.database.NewDataBase
import com.aldemir.mesanews.data.database.UserDao
import com.aldemir.mesanews.data.repository.login.LoginRepositoryImpl
import com.aldemir.mesanews.data.repository.news.NewsRepository
import com.aldemir.mesanews.data.repository.news.NewsRepositoryImpl
import com.aldemir.mesanews.data.repository.register.RegisterRepository
import com.aldemir.mesanews.data.repository.register.RegisterRepositoryImpl
import com.aldemir.mesanews.ui.feed.FeedViewModel
import com.aldemir.mesanews.ui.filter.FilterViewModel
import com.aldemir.mesanews.ui.login.LoginViewModel
import com.aldemir.mesanews.ui.main.MainViewModel
import com.aldemir.mesanews.ui.register.RegisterViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val databaseModule = module {
    fun provideDatabase(application: Application) : NewDataBase {
        return Room.databaseBuilder(application, NewDataBase::class.java, "newDataBase")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    fun provideNewDao(dataBase: NewDataBase): NewDao {
        return dataBase.newDao()
    }

    fun provideUserDao(dataBase: NewDataBase): UserDao {
        return dataBase.userDao()
    }

    single { provideDatabase(androidApplication()) }
    single { provideNewDao(get()) }
    single { provideUserDao(get()) }
}

val feedModule = module {

    factory <NewsRepository> {
        NewsRepositoryImpl(
            newApiService = get(),
            newDao = get()
        )
    }

    viewModel {
        FeedViewModel(
            newsRepository =  get(),
            sessionManager = get()
        )
    }
}

val filterModule = module {
    viewModel {
        FilterViewModel(
            newsRepository =  get(),
            sessionManager = get()
        )
    }
}

val registerModule = module {

    factory<RegisterRepository> {
        RegisterRepositoryImpl(
            apiService = get(),
            userDao = get()
        )
    }

    single {
        SessionManager(
            context = androidContext()
        )
    }

    viewModel {
        RegisterViewModel(
            registerRepository = get(),
            sessionManager = get()
        )
    }
}

val mainModule = module {

    viewModel {
        MainViewModel(
            registerRepository = get(),
            sessionManager = get()
        )
    }
}

val networkModule = module {
    factory { AuthInterceptor(androidContext()) }
    factory {
        provideOkHttpClient(
            authInterceptor =  get()
        )
    }
    factory {
        provideApiService(
            retrofit = get()
        )
    }
    single {
        provideRetrofit(
            okHttpClient = get()
        )
    }

    factory<LoginRepositoryImpl> {
        LoginRepositoryImpl(
            apiService = get(),
            userDao = get()
        )
    }

    viewModel {
        LoginViewModel(
            loginRepository = get(),
            sessionManager = get()
        )
    }
}

private const val BASE_URL = "https://mesa-news-api.herokuapp.com"

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    return OkHttpClient().newBuilder()
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor).build()
}

fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

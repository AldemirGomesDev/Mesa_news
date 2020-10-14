package com.aldemir.mesanews.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.aldemir.mesanews.KoinTestApp
import com.aldemir.mesanews.MyApplication
import com.aldemir.mesanews.data.api.SessionManager
import com.aldemir.mesanews.data.repository.register.RegisterRepository
import com.aldemir.mesanews.data.repository.register.RegisterRepositoryImpl
import com.aldemir.mesanews.module.*
import com.aldemir.mesanews.ui.register.domain.User
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.Mockito

import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@RunWith(RobolectricTestRunner :: class )
@Config(application = KoinTestApp::class , sdk = [28])
class MainViewModelTest: KoinTest {


    @get:Rule
    val rule = InstantTaskExecutorRule()


    private val mainViewModel by inject<MainViewModel>()
    private val repository: RegisterRepository by inject()


    private lateinit var registerRepository: RegisterRepository
    private lateinit var sessionManager: SessionManager

    @Mock
    lateinit var listObserver: Observer<User>


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun getUserLogged() {
        val email = "teste@gmail.com"
        mainViewModel.userLogged.observeForever(listObserver)
        mainViewModel.getUserLogged(email)
        val value = mainViewModel.userLogged.value
        Mockito.verify(listObserver).onChanged(value)

    }
}
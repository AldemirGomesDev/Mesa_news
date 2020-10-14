package com.aldemir.mesanews.ui.main

import com.aldemir.mesanews.data.api.ApiService
import com.aldemir.mesanews.data.repository.register.RegisterRepository
import com.aldemir.mesanews.data.repository.register.RegisterRepositoryImpl
import com.google.gson.Gson
import junit.framework.Assert.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class MockWebUnitTests {
    private val server: MockWebServer = MockWebServer()

    private val MOCK_WEBSERVER_PORT = 8000
    lateinit var apiService: ApiService
    private lateinit var registerRepository: RegisterRepository


    @Before
    fun init() {
        server.start(MOCK_WEBSERVER_PORT)
        apiService = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(ApiService::class.java)

        registerRepository = Mockito.mock(RegisterRepositoryImpl::class.java)
    }
    @After
    fun shutdown() {
        server.shutdown()
    }

    @Test
    fun `JsonPlaceholder APIs parse correctly`() {
        server.apply {
            enqueue(MockResponse().setBody(MockResponseFileReader("jsonplaceholder_success.json").content))
        }
        registerRepository.observePosts()
            .test()
            .awaitDone(3, TimeUnit.SECONDS)
            .assertComplete()
            .assertValueCount(1)
            .assertValue { it.size == 2 }
            .assertNoErrors()
        }
    }
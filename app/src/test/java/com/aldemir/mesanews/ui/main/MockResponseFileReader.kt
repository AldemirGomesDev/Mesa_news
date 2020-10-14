package com.aldemir.mesanews.ui.main

import java.io.InputStreamReader

class MockResponseFileReader(path: String) {
    val content: String
    init {
        val reader = InputStreamReader(this::class.java.classLoader!!.getResourceAsStream(path))
        content = reader.readText()
        reader.close()
    }

}
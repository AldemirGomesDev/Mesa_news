package com.aldemir.mesanews.data.model

import com.google.gson.annotations.SerializedName

data class RequestLogin (
    var email: String,
    var password: String
)
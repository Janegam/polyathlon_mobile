package com.example.newtest
import kotlinx.serialization.Serializable

@Serializable
data class user(
    val login: String,
    val password: String

)
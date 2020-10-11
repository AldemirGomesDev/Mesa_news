package com.aldemir.mesanews.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false,
    val isLoginDataValid: Boolean = false,
    val isUserNameValid: Boolean = false
)
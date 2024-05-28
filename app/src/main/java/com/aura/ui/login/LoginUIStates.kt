package com.aura.ui.login

data class LoginUIStates(
    val isLoading: Boolean = false,
    val bAccessGranted: Boolean = false,
    val sErrorMessage: String? = null,
    val bEmpty : Boolean = true
)

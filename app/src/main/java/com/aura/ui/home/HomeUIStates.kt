package com.aura.ui.home

import com.aura.model.ModelResponseAccount

data class HomeUIStates (

    val mainAccount : ModelResponseAccount? = null,

    val isViewLoading: Boolean = false,

    val errorMessage: String? = null

)

package com.aura.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.network.ResultBankAPI
import com.aura.repository.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

//import com.aura.repository

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {


    /**
     * MutableStateFlow : Il s'agit d'une implémentation de  StateFlow  qui permet de représenter un flux de données modifiable.
     * Dans ce cas, on crée une instance de  MutableStateFlow
     */
    private val _uiState = MutableStateFlow(LoginUIStates())

    /**
     * StateFlow est une classe du framework Kotlin Flow qui émet une séquence de valeurs et garantit qu'un observateur reçoit toujours la dernière valeur émise.
     * Dans cet exemple, on expose un  StateFlow  en lecture seule à partir du  MutableStateFlow  créé précédemment.
     * C'est cet objet qui va être utilisé par l'activty pour collecter les LoginUIStates
     */
    val uiState: StateFlow<LoginUIStates> = _uiState.asStateFlow()

    /**
     * When the button 'Connexion' is usable
     */
    fun bCheckButtonConnexionClickable(sLoginP: String, sPasswordP: String): Boolean {
        return sLoginP.isNotEmpty() && sPasswordP.isNotEmpty()
    }

    fun login(sLoginP: String, sPasswordP: String) {

        // onEach : Ce bloc est appelé chaque fois que de nouvelles données sont émises par le flow
        dataRepository.login(sLoginP, sPasswordP).onEach { resultAPI ->

            // En fonction du résultat de l'API
            when (resultAPI) {
                // Echec
                is ResultBankAPI.Failure ->
                    _uiState.update { currentState ->
                        currentState.copy( // crée une nouvelle instance de l'état avec les valeurs spécifiées
                            // Syntaxe par paramètres nommés
                            isLoading = false,
                            bAccessGranted = false,
                            sErrorMessage = resultAPI.message,
                            bCallback = true
                        )
                    }
                // En chargement
                ResultBankAPI.Loading ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            // Syntaxe par paramètres nommés
                            isLoading = true,
                            bAccessGranted = false,
                            sErrorMessage = null,
                            bCallback = true
                        )
                    }
                // Succès
                is ResultBankAPI.Success -> {

                    _uiState.update { currentState ->
                        currentState.copy(
                            // Syntaxe par paramètres nommés
                            isLoading = false,
                            bAccessGranted = resultAPI.value.granted,
                            sErrorMessage = "",
                            bCallback = true
                        )
                    }

                }


            }

        }
            .launchIn(viewModelScope) //  Cette partie indique que la coroutine doit être lancée dans le scope du ViewModel. Cela garantit que la coroutine est annulée lorsque le ViewModel est détruit, évitant les fuites de mémoire.


    }

}
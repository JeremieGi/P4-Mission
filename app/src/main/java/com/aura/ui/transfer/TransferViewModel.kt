package com.aura.ui.transfer

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

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    /**
     * MutableStateFlow : Il s'agit d'une implémentation de  StateFlow  qui permet de représenter un flux de données modifiable.
     * Dans ce cas, on crée une instance de  MutableStateFlow
     */
    private val _uiState = MutableStateFlow(TransferUIStates())

    /**
     * StateFlow est une classe du framework Kotlin Flow qui émet une séquence de valeurs et garantit qu'un observateur reçoit toujours la dernière valeur émise.
     * Dans cet exemple, on expose un  StateFlow  en lecture seule à partir du  MutableStateFlow  créé précédemment.
     * C'est cet objet qui va être utilisé par l'activity pour collecter les TransferUIStates
     */
    val uiState: StateFlow<TransferUIStates> = _uiState.asStateFlow()

    fun bCheckButtonTransfertClickable(sRecipientP: String, dAmoutP: Double): Boolean {
        return sRecipientP.isNotEmpty() && dAmoutP!=0.0
    }

    /**
     * Appelle l'API permettant d'effectuer des virements (transfert d'argent)
     */
    fun transfer(sUserSenderP: String, sUserRecipientP: String, dAmountP : Double) {

        // onEach : Ce bloc est appelé chaque fois que de nouvelles données sont émises par le flow
        dataRepository.transfer(sUserSenderP,sUserRecipientP,dAmountP).onEach { resultAPI ->

            // En fonction du résultat de l'API
            when (resultAPI) {
                // Echec
                is ResultBankAPI.Failure ->
                    _uiState.update { currentState ->
                        currentState.copy( // crée une nouvelle instance de l'état avec les valeurs spécifiées
                            // Syntaxe par paramètres nommés
                            isLoading = false,
                            bTransfertOK = false,
                            sErrorMessage = resultAPI.message
                        )
                    }
                // En chargement
                ResultBankAPI.Loading ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            // Syntaxe par paramètres nommés
                            isLoading = true,
                            bTransfertOK = false,
                            sErrorMessage = null
                        )
                    }
                // Succès
                is ResultBankAPI.Success -> {

                    _uiState.update { currentState ->
                        currentState.copy(
                            // Syntaxe par paramètres nommés
                            isLoading = false,
                            bTransfertOK = resultAPI.value.bResult,
                            sErrorMessage = ""
                        )
                    }

                }


            }
        }.launchIn(viewModelScope) //  Cette partie indique que la coroutine doit être lancée dans le scope du ViewModel. Cela garantit que la coroutine est annulée lorsque le ViewModel est détruit, évitant les fuites de mémoire.

    }


}

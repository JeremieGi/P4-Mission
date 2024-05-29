package com.aura.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.model.ModelResponseAccount
import com.aura.network.ResultBankAPI
import com.aura.repository.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {

    /**
     * MutableStateFlow : Il s'agit d'une implémentation de  StateFlow  qui permet de représenter un flux de données modifiable.
     * Dans ce cas, on crée une instance de  MutableStateFlow
     */
    private val _uiState = MutableStateFlow(HomeUIStates())

    /**
     * StateFlow est une classe du framework Kotlin Flow qui émet une séquence de valeurs et garantit qu'un observateur reçoit toujours la dernière valeur émise.
     * Dans cet exemple, on expose un  StateFlow  en lecture seule à partir du  MutableStateFlow  créé précédemment.
     */
    val uiState: StateFlow<HomeUIStates> = _uiState //.asStateFlow() TODO : commenté car faisait un appel fantôme dans collect


    fun getMainAccount(sUserIDP: String) {

        dataRepository.accounts(sUserIDP).onEach { resultAPI ->

            // En fonction du résultat de l'API
            when (resultAPI) {

                // Echec
                is ResultBankAPI.Failure -> _uiState.update { currentState ->
                    currentState.copy(
                        // Syntaxe par paramètres nommés
                        isViewLoading = false,
                        errorMessage = resultAPI.message.toString()
                    )
                }
                // En chargement
                ResultBankAPI.Loading -> _uiState.update { currentState ->
                    currentState.copy(
                        // Syntaxe par paramètres nommés
                        isViewLoading = true,
                        errorMessage = ""
                    )
                }
                // Succès
                is ResultBankAPI.Success -> _uiState.update { currentState ->

                    // Récupération du compte principal
                    val mainAccount = getMainAccount(resultAPI.value)
                    if (mainAccount==null){
                        currentState.copy(
                            // Syntaxe par paramètres nommés
                            isViewLoading = false,
                            errorMessage = "No main account find"
                        )
                    }
                    else{
                        currentState.copy(
                            // Syntaxe par paramètres nommés
                            mainAccount = mainAccount,
                            isViewLoading = false,
                            errorMessage = ""
                        )
                    }


                }
            }
        }.launchIn(viewModelScope) //  Cette partie indique que la coroutine doit être lancée dans le scope du ViewModel. Cela garantit que la coroutine est annulée lorsque le ViewModel est détruit, évitant les fuites de mémoire.



    }

    /**
     *  Récupération du compte principal
     *  @return : null si pas de compte principal
     */
    private fun getMainAccount(listAccounts: List<ModelResponseAccount>): ModelResponseAccount? {

        // Parcours de la liste des comptes à la recherche du compte principal
        listAccounts.forEach {
            if (it.bMainAccount) return it
        }

        return null
    }

}
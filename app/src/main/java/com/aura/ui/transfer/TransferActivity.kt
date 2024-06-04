package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.BuildConfig
import com.aura.databinding.ActivityTransferBinding
import com.aura.R
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity()
{

  /**
   * The binding for the transfer layout.
   */
  private lateinit var binding: ActivityTransferBinding

  private val viewModel: TransferViewModel by viewModels()


  // ID du user passé en paramètre de l'activity
  companion object {
    const val PARAM_TRANSFERACTIVITY_IDUSER = "PARAM_ID_USER"
  }

  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)

    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Récupération du user (via le paramètre de l'activity)
    viewModel.sIDCurrentUser = intent.getStringExtra(PARAM_TRANSFERACTIVITY_IDUSER).toString()
    // Display the ID of user
    title = "Aura - User ${viewModel.sIDCurrentUser}"

    binding.buttonTransfer.isEnabled = false
    binding.tvErrorMessageTransfer.isVisible = false

    // Execution de code uniquement en debug ?
    if (BuildConfig.DEBUG) {
        binding.edtRecipient.setText("5678")
        binding.edtAmount.setText("10")
        binding.buttonTransfer.isEnabled = true
    }

    // Listeners des champs de saisie Destinataire et Montant
    binding.edtRecipient.addTextChangedListener(object : TextWatcher {

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        enableOrDesableTranfertButton()
      }

      override fun afterTextChanged(s: Editable?) {

      }
    })

    binding.edtAmount.addTextChangedListener(object : TextWatcher {

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        enableOrDesableTranfertButton()
      }

      override fun afterTextChanged(s: Editable?) {

      }
    })


    // Bouton Faire le Virement
    binding.buttonTransfer.setOnClickListener {

      binding.progressbarTransferLoading.visibility = View.VISIBLE

      // Lance l'appel API de façon asynchrone
      viewModel.transfer(binding.edtRecipient.text.toString(),dGetAmount())

    }


    lifecycleScope.launch { // Utilise le lifecycleScope de l'activité pour lancer une coroutine.

      /**
       * Cette fonction du lifecycleScope assure que le bloc de code à l'intérieur est exécuté tant que le cycle de vie de l'activité est dans l'état STARTED.
       *  Cela garantit que les opérations de collecte (écoute) sur le  StateFlow  sont actives lorsque l'activité est visible à l'utilisateur.
       */
      repeatOnLifecycle(Lifecycle.State.STARTED) {

        /**
         * Utilise la fonction collect sur le StateFlow uiState du ViewModel pour écouter les mises à jour de l'état de l'interface utilisateur de manière réactive.
         * En théorie, un flow peut émettre plusieurs valeurs à la suite.
         */
        viewModel.uiState.collect {

          // it est ici de type LoginUIStates


          if (!it.isLoading && (it.sErrorMessage==null) && !it.bTransfertOK) {
            // Pourquoi au lancement en debug je breake ici : un LoginUIStates vide est envoyé
            // Du coup j'utilise ce test pour gérer le problème
          }
          else{

            binding.progressbarTransferLoading.isVisible = false
            binding.tvErrorMessageTransfer.isVisible = false

            if (it.isLoading){
              //T012 - Manage the loading state on the transfer screen
              binding.progressbarTransferLoading.isVisible = true
              binding.buttonTransfer.isEnabled = false
              //delay(5*1000) // 5 s to test
            }
            else{

              binding.buttonTransfer.isEnabled = true

              // Vérification qu'il n'y ait pas d'erreur
              if (it.sErrorMessage?.isNotBlank() == true) {

                // T014 - Manage the error state on the transfer screen
                Snackbar.make(binding.root, it.sErrorMessage, Snackbar.LENGTH_LONG)
                  .show()

                binding.tvErrorMessageTransfer.text = it.sErrorMessage
                binding.tvErrorMessageTransfer.isVisible = true


              }
              else{

                // Transfert réalisé
                if (it.bTransfertOK){

                  // Ferme l'activity, en indiquant à l'activity appelante le succès du transfert
                  setResult(Activity.RESULT_OK)
                  finish()

                }
                else{
                  // Erreur lors du transfert

                  binding.tvErrorMessageTransfer.text = getString(R.string.impossible_transfer)
                  binding.tvErrorMessageTransfer.isVisible = true

                }


              }

            }


          }



        }

      }
    }
  }


  /**
   * Check inputs
   */
  private fun enableOrDesableTranfertButton() {

    // T010 - Add business rules on the transfer screen

    binding.buttonTransfer.isEnabled =
      viewModel.bCheckButtonTransfertClickable(binding.edtRecipient.text.toString(), dGetAmount())


  }

  /**
   * Factorise le code de récupération d'un Double à partir du montant saisi
   */
  private fun dGetAmount() : Double{
    return binding.edtAmount.text.toString().toDoubleOrNull() ?: 0.0
  }

}

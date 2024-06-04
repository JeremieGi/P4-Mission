package com.aura.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
/**
 * The home activity for the app.
 */
class HomeActivity : AppCompatActivity()
{

  /**
   * The binding for the home layout.
   */
  private lateinit var binding: ActivityHomeBinding

  private val viewModel: HomeViewModel by viewModels() // délégation


  // ID du user passé en paramètre de l'activity
  companion object {
    const val PARAM_HOMEACTIVITY_IDUSER = "PARAM_ID_USER"
  }

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

      // T013 - Manage the success state on the transfer screen

      // Si transfert a été réalisé avec succès, on recharge la balance
      if (result.resultCode == Activity.RESULT_OK) {

        viewModel.getMainAccount()

        Snackbar.make(binding.root, getString(R.string.transfer_completed), Snackbar.LENGTH_LONG)
          .show()

      }

    }

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Récupération du user (via le paramètre de l'activity)
    viewModel.sIDCurrentUser = intent.getStringExtra(PARAM_HOMEACTIVITY_IDUSER).toString()
    // Display the ID of user
    title = "Aura - User ${viewModel.sIDCurrentUser}"

    binding.tvErrorMessage.isVisible = false
    binding.buttonTryAgain.isVisible = false

    // Chargement du compte principal
    viewModel.getMainAccount()

    // Utilise le lifecycleScope de l'activité pour collecter les résultats du WS
    lifecycleScope.launch {

      repeatOnLifecycle(Lifecycle.State.STARTED) {

        viewModel.uiState.collect {
          // it est ici de type HomeUIStates

          binding.buttonToTransfer.isEnabled = false
          binding.tvErrorMessage.isVisible = false
          binding.buttonTryAgain.isVisible = false

          // Chargement en cours
          if (it.isViewLoading){
              // T007 - Affichage de la progressBar pendant le chargement
              binding.progressbarLoadingBalance.isVisible = true
              //delay(5*1000)
          }
          else{

              binding.progressbarLoadingBalance.isVisible = false

              // Vérification qu'il n'y ait pas d'erreur réseau
              if (it.errorMessage?.isNotBlank() == true) {

                // T009 - Manage the error state on the home screen

//                Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_LONG)
//                  .show()

                binding.tvErrorMessage.text = it.errorMessage
                binding.tvErrorMessage.isVisible = true
                binding.buttonTryAgain.isVisible = true


              } else {

                  // T008 - Manage the success state on the homescreen

                  // Récupération du compte principal
                  val mainAccount = it.mainAccount

                  // Si pas de compte principal
                  if (mainAccount == null) {
                      binding.tvBalance.text = getString(R.string.no_main_account)

                  } else {
                      // Cas normal, un compte principal existe
                      binding.tvBalance.text = String.format("%.2f", mainAccount.dBalance)
                      binding.buttonToTransfer.isEnabled = true
                  }

              }
          }


        }

      }

    }


    // LISTENER

    binding.buttonToTransfer.setOnClickListener {

      // Passage de l'ID du user courant en paramètre
      val intent = Intent(this@HomeActivity, TransferActivity::class.java)
      intent.putExtra(TransferActivity.PARAM_TRANSFERACTIVITY_IDUSER,viewModel.sIDCurrentUser)
      startTransferActivityForResult.launch(intent) // Voir la variable qui définie la callback

    }

    binding.buttonTryAgain.setOnClickListener {
      // Retry a connexion
      viewModel.getMainAccount()
    }


  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean
  {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return when (item.itemId)
    {
      R.id.disconnect ->
      {
        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        finish()
        true
      }
      else            -> super.onOptionsItemSelected(item)
    }
  }

}

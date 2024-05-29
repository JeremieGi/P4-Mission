package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aura.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
//import com.aura.BuildConfig

@AndroidEntryPoint
/**
 * The login activity for the app.
 */
class LoginActivity : AppCompatActivity()
{

  /**
   * The binding for the login layout.
   */
  private lateinit var binding: ActivityLoginBinding

  // Utilisation de la délégation 'by viewModels()' pour obtenir une instance de ViewModel
  private val viewModel: LoginViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val progressbarLoading = binding.progressbarLoading

    binding.btnlogin.isEnabled = false

    // TODO : Execution de code uniquement en debug ?
    //if (BuildConfig.DEBUG) {
      binding.edtLogin.setText("1234")
      binding.edtPassword.setText("p@sswOrd")
      binding.btnlogin.isEnabled = true
    //}


    // LISTENERS


    // Saisie du login et du mot de passe

    binding.edtLogin.addTextChangedListener(object : TextWatcher {

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        enableOrDesableConnexionButton()
      }

      override fun afterTextChanged(s: Editable?) {

      }
    })

    binding.edtPassword.addTextChangedListener(object : TextWatcher {

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
          enableOrDesableConnexionButton()
      }

      override fun afterTextChanged(s: Editable?) {

      }
    })


    // Connexion button
    binding.btnlogin.setOnClickListener {

      progressbarLoading.visibility = View.VISIBLE

      // Lance l'appel API de façon asynchrone
      viewModel.login(binding.edtLogin.text.toString(), binding.edtPassword.text.toString())

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

          binding.progressbarLoading.isVisible = false

          if (!it.isLoading && (it.sErrorMessage==null) && !it.bAccessGranted) {
              // TODO : Pourquoi au lancement en debug je breake ici : un LoginUIStates vide est envoyé
              // Du coup j'utilise ce test pour gérer le problème
          }
          else{

            if (it.isLoading){
              // T003 - Affichage de la progressBar pendant le chargement
              binding.progressbarLoading.isVisible = true
              // T003 - Manage the loading state on the login screen
              // T003 - The login button is disabled while the credentials are being checked
              binding.btnlogin.isEnabled = false

              //delay(5*1000) // Test  of T003
            }
            else{

              binding.btnlogin.isEnabled = true

              // Vérification qu'il n'y ait pas d'erreur
              if (it.sErrorMessage?.isNotBlank() == true) {

                // T005 - Manage the error state on the login screen
                Snackbar.make(binding.root, it.sErrorMessage, Snackbar.LENGTH_LONG)
                  .show()

              }
              else{

                // Accès autorisé
                if (it.bAccessGranted){

                  // T004 - Manage the success state on the login screen
                  // Ouverture de la fenêtre d'accueil
                  val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                  // passage en paramètre à HomeActivity de l'utilisateur loggué
                  intent.putExtra(HomeActivity.PARAM_HOMEACTIVITY_IDUSER,binding.edtLogin.text.toString())
                  startActivity(intent)

                  finish()

                }
                else{
                  // Access denied

                  Snackbar.make(/* view = */ binding.root, /* text = */
                    getString(R.string.access_denied), /* duration = */
                    Snackbar.LENGTH_LONG)
                    .show()

                  // t005 - The login button is enabled when the credentials are incorrectly checked.
                  binding.btnlogin.isEnabled = false

                }


              }

            }


          }



        }

      }
    }

  }

  /**
   * - The login button is enabled if the identifier field and the password field are both not empty.
   * - The login button is disabled if the identifier field or the password field is empty.
   */
  fun enableOrDesableConnexionButton(){

    // T001 - Add business rules on the login screen

    binding.btnlogin.isEnabled =
      viewModel.bCheckButtonConnexionClickable(binding.edtLogin.text.toString(), binding.edtPassword.text.toString())

  }

}

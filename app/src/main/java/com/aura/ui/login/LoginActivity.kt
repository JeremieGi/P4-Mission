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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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

      // Lance l'appel API de façon asynchrose
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

          // TODO : Pourquoi au lancement en debug je breake ici : un LoginUIStates vide semble envoyé

          // T003 - Affichage de la progressBar pendant le chargement
          binding.progressbarLoading.isVisible = it.isLoading

          // T003 - Manage the loading state on the login screen
          binding.btnlogin.isEnabled = !it.isLoading // T003 - The login button is disabled while the credentials are being checked
          //if (it.isLoading) delay(5*1000) // Test  of T003


          // Vérification qu'il n'y ait pas d'erreur
          if (it.sErrorMessage?.isNotBlank() == true) {
            Snackbar.make(binding.root, it.sErrorMessage, Snackbar.LENGTH_LONG)
              .show()
          }
          else{

            // Accès autorisé
            if (it.bAccessGranted){

              // Ouverture de la fenêtre d'accueil
              val intent = Intent(this@LoginActivity, HomeActivity::class.java)
              startActivity(intent)

              finish()

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

package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.aura.databinding.ActivityTransferBinding
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint

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

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)



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

    binding.buttonTransfer.isEnabled = false

    binding.buttonTransfer.setOnClickListener {

      binding.progressbarTransferLoading.visibility = View.VISIBLE

      setResult(Activity.RESULT_OK)
      finish()

    }
  }

  /**
   * Check inputs
   */
  private fun enableOrDesableTranfertButton() {

    // T010 - Add business rules on the transfer screen

    val dAmount = binding.edtAmount.text.toString().toDoubleOrNull() ?: 0.0

    binding.buttonTransfer.isEnabled =
      viewModel.bCheckButtonTransfertClickable(binding.edtRecipient.text.toString(), dAmount)


  }

}

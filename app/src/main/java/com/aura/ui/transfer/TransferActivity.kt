package com.aura.ui.transfer

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.aura.databinding.ActivityTransferBinding

/**
 * The transfer activity for the app.
 */
class TransferActivity : AppCompatActivity()
{

  /**
   * The binding for the transfer layout.
   */
  private lateinit var binding: ActivityTransferBinding

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityTransferBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val edtRecipient = binding.edtRecipient
    val edtAmount = binding.edtAmount
    val buttonTransfer = binding.buttonTransfer
    val progressbarTransferLoading = binding.progressbarTransferLoading

    buttonTransfer.setOnClickListener {
      progressbarTransferLoading.visibility = View.VISIBLE

      setResult(Activity.RESULT_OK)
      finish()
    }
  }

}

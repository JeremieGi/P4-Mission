package com.aura.ui.transfer

import androidx.lifecycle.ViewModel
import com.aura.repository.BankRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val dataRepository: BankRepository
) : ViewModel() {
    fun bCheckButtonTransfertClickable(sRecipientP: String, dAmoutP: Double): Boolean {
        return sRecipientP.isNotEmpty() && dAmoutP!=0.0
    }


}

package com.aura.model.di

import com.aura.network.APIClient
import com.aura.repository.BankRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Constructeur du repository
     * TODO : Utilité de faire 2 Modules pour les DI ?
     */
    @Provides
    @Singleton
    fun provideBankRepository(dataClient: APIClient): BankRepository {
        return BankRepository(dataClient)
    }

}
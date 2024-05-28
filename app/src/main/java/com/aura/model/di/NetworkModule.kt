package com.aura.model.di

import com.aura.network.APIClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    // Provides a singleton instance of Retrofit for network communication
    /**
     * Cette fonction fournit une instance unique de Retrofit pour la communication réseau.
     * Elle utilise Moshi comme convertisseur JSON,
     * avec l'ajout du KotlinJsonAdapterFactory pour une meilleure compatibilité avec les classes Kotlin.
     * La configuration d'OkHttpClient est extraite dans une fonction séparée pour plus de clarté ;
     */
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            // TODO : Erreur au lancement du serveur API dans intelIJ mais le serveur répond.
            .baseUrl("http://10.0.2.2:8080/") // Si vous lancez l’application Android depuis un émulateur, utilisez l’URL http://10.0.2.2:8080 pour consommer les routes de l’API.
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .client(provideOkHttpClient()) // Uses a separate function for OkHttpClient configuration
            .build()
    }

    // Provides a singleton instance of APIClient using Retrofit
    /**
     * Cette fonction fournit une instance unique de APIClient utilisant Retrofit. Elle dépend de l'instance de Retrofit fournie par provideRetrofit
     */
    @Singleton
    @Provides
    fun provideAPIClient(retrofit: Retrofit): APIClient { // WeatherClient = service de l'API météo (Interface)
        return retrofit.create(APIClient::class.java)
    }

    // Private function to configure OkHttpClient with an interceptor for logging
    /**
     * Cette fonction configure et fournit une instance d'OkHttpClient avec un intercepteur pour les logs. Elle est utilisée dans provideRetrofit.
     * L'utilisation de l'intercepteur de logging est utile pour le débogage et pour comprendre ce qui se passe sous le capot lors de la communication HTTP.
     */
    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            // Les intercepteurs sont très utiles pour effectuer un traitement systématique sur chaque requête envoyée
            // .setLevel(HttpLoggingInterceptor.Level.BODY) configure le niveau de logging de l'intercepteur. Level.BODY signifie que le corps des requêtes et des réponses sera logué, en plus des en-têtes et des autres informations.
        }.build()
    }


}
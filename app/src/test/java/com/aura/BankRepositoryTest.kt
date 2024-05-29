package com.aura

import com.aura.network.APIClient
import com.aura.network.APIResponseLogin
import com.aura.network.LoginPostValue
import com.aura.network.ResultBankAPI
import com.aura.repository.BankRepository
import io.mockk.coEvery
import org.junit.Before
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class BankRepositoryTest {

    private lateinit var cutRepToTest : BankRepository //Class Under Test
    private lateinit var dataService: APIClient

    // Peu importe le login / password
    private val sLogin = "1234"
    private val sPwd = "pwd"

    @Before
    fun createRepositoryWithMockedAPI() {
        // Création du repo avec un Mock en paramètre
        dataService = mockk()
        cutRepToTest = BankRepository(dataService)
    }

    @Test
    fun `Login_Correct`() = runTest {

        /*
         Réponse attendue :
         {
            "granted": true
           }
         */

        val etalonResponse = APIResponseLogin(true)

        val postValues = LoginPostValue(sLogin,sPwd)

        /**
         * Nous demandons à notre Mock de simuler un appel réseau
         * renvoyant la réponse que nous avons directement préparée (  etalonResponse  ).
         * La méthode  coEvery  est utilisée avec le préfixe co indiquant que le code s'exécute dans un contexte de coroutine,
         * et le mot-clé Every spécifiant que cette réponse sera retournée à chaque invocation de la fonction dataService.login
         */
        coEvery {
            dataService.login(postValues)
        } returns Response.success(etalonResponse)


        //when => Test réel de la fonction
        val flowTest = run {
            cutRepToTest.login(sLogin,sPwd).toList()
        }

        //then => Vérification

        // coVerify  et  run {}  font partie de la bibliothèque MockK et permettent de vérifier les appels de fonctions sur des mocks dans des coroutines.
        // Par exemple,
        //
        // coVerify : s'assure que la fonction  du mock  dataService  a été appelée avec les arguments spécifiés.
        coVerify {
            dataService.login(any())
        }

        // On attend 2 valeurs dans le flow
        Assert.assertEquals(2, flowTest.size)
        // Première valeur => Loading
        assertEquals(ResultBankAPI.Loading, flowTest[0])
        // Deuxième valeur => La réponse avec succès correspondant à ce qu'on attend openForecastResponse
        assertEquals(ResultBankAPI.Success(etalonResponse.toDomainModel()), flowTest[1])

    }

}
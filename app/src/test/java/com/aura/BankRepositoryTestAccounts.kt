package com.aura

import com.aura.network.APIClient
import com.aura.network.APIResponseAccount
import com.aura.network.ResultBankAPI
import com.aura.repository.BankRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Test l'API qui renvoie la liste des comptes d'un utilisateur
 */
class BankRepositoryTestAccounts {

    private lateinit var cutRepToTest : BankRepository //Class Under Test
    private lateinit var dataService: APIClient

    // Peu importe le login (API mockée)
    private val sUserTest = "UserTest"

    @Before
    fun createRepositoryWithMockedAPI() {
        // Création du repo avec un Mock en paramètre
        dataService = mockk()
        cutRepToTest = BankRepository(dataService)
    }

    /**
     * Cas d'utilisation normal : utilisateur connu avec des comptes
     */
    @Test
    fun testUserKnow() = runTest {

        val etalonResponse = listOf(
            APIResponseAccount( sAccountID = "1", bMainAccount=true, dBalance=100.0),
            APIResponseAccount( sAccountID = "2", bMainAccount=false, dBalance=1.0)
        )

        coEvery {
            dataService.accounts(sUserTest)
        } returns Response.success(etalonResponse)


        //when => Test réel de la fonction
        val flowTest = run {
            cutRepToTest.accounts(sUserTest).toList()
        }

        // coVerify : s'assure que la fonction  du mock  dataService  a été appelée avec les arguments spécifiés.
        coVerify {
            dataService.accounts(any())
        }

        // On attend 2 valeurs dans le flow
        assertEquals(2, flowTest.size)

        // Première valeur => Loading
        assertEquals(ResultBankAPI.Loading, flowTest[0])
        // Deuxième valeur => La réponse avec succès -> 2 comptes

        val res = ResultBankAPI.Success(APIResponseAccount.toListDomainModel(etalonResponse))
        assertEquals(res, flowTest[1])

    }


    /**
     * Cas d'utilisation anormal : utilisateur inconnu => le repository renvoie une liste vide
     */
    @Test
    fun testUserUnknowReturnEmptyResult() = runTest {

        // Liste vide
        val etalonResponse : List<APIResponseAccount> = listOf()

        coEvery {
            dataService.accounts(sUserTest)
        } returns Response.success(etalonResponse)


        //when => Test réel de la fonction
        val flowTest = run {
            cutRepToTest.accounts(sUserTest).toList()
        }

        // coVerify : s'assure que la fonction  du mock  dataService  a été appelée avec les arguments spécifiés.
        coVerify {
            dataService.accounts(any())
        }

        // On attend 2 valeurs dans le flow
        assertEquals(2, flowTest.size)

        // Première valeur => Loading
        assertEquals(ResultBankAPI.Loading, flowTest[0])
        // Deuxième valeur => La réponse avec succès -> 2 comptes

        val res = ResultBankAPI.Success(APIResponseAccount.toListDomainModel(etalonResponse))
        assertEquals(res, flowTest[1])

    }

    /**
     * Problème réseau
     */
    @Test
    fun testNetworkProblem() = runTest {

        /**
         * Simule une erreur 404
         */

        val errorResponseBody = "Error 404 message".toResponseBody("text/plain".toMediaType())

        // Le mock renverra une erreur 404
        coEvery {
            dataService.accounts(sUserTest)
        } returns Response.error(404, errorResponseBody)

        //when => Test réel de la fonction
        val flowTest = run {
            cutRepToTest.accounts(sUserTest).toList()
        }

        //then => Vérification
        coVerify {
            dataService.accounts(sUserTest)
        }

        // On attend 2 valeurs dans le flow
        assertEquals(2, flowTest.size)
        // Première valeur => Loading
        assertEquals(ResultBankAPI.Loading, flowTest[0])
        // Deuxième valeur => Erreur
        assert(flowTest[1] is ResultBankAPI.Failure)

    }

}
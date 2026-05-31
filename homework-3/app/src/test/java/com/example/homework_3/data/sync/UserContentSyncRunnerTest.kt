package com.example.homework_3.data.sync

import com.example.homework_3.MainDispatcherRule
import com.example.homework_3.data.CharactersRepository
import com.example.homework_3.data.local.FavoriteCharacterDao
import com.example.homework_3.data.local.FavoriteCharacterEntity
import com.example.homework_3.data.local.RecentViewDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserContentSyncRunnerTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `404 on one id - continues syncing others`() = runTest {
        val repository = mockk<CharactersRepository>()
        val favoriteDao = mockk<FavoriteCharacterDao>()
        val recentViewDao = mockk<RecentViewDao>()

        coEvery { favoriteDao.getAll() } returns listOf(fav("1"), fav("2"))
        coEvery { recentViewDao.getRecentCharacterIds(50) } returns emptyList()

        coEvery { repository.refreshCharacterById("1", force = false) } throws httpException(404)
        coEvery { repository.refreshCharacterById("2", force = false) } returns Unit

        val result = UserContentSyncRunner(repository, favoriteDao, recentViewDao).syncUserContent()

        assertEquals(listOf("2"), result.updatedIds)
        assertEquals(listOf("1"), result.skippedNotFoundIds)
        assertFalse(result.shouldRetry)
        coVerify(exactly = 1) { repository.refreshCharacterById("1", force = false) }
        coVerify(exactly = 1) { repository.refreshCharacterById("2", force = false) }
    }

    @Test
    fun `network error - marks retry and still processes remaining ids`() = runTest {
        val repository = mockk<CharactersRepository>()
        val favoriteDao = mockk<FavoriteCharacterDao>()
        val recentViewDao = mockk<RecentViewDao>()

        coEvery { favoriteDao.getAll() } returns listOf(fav("1"), fav("2"))
        coEvery { recentViewDao.getRecentCharacterIds(50) } returns emptyList()

        coEvery { repository.refreshCharacterById("1", force = false) } throws IOException("offline")
        coEvery { repository.refreshCharacterById("2", force = false) } returns Unit

        val result = UserContentSyncRunner(repository, favoriteDao, recentViewDao).syncUserContent()

        assertEquals(listOf("2"), result.updatedIds)
        assertTrue(result.shouldRetry)
    }

    @Test
    fun `merges favorite and recent ids without duplicates`() = runTest {
        val repository = mockk<CharactersRepository>(relaxed = true)
        val favoriteDao = mockk<FavoriteCharacterDao>()
        val recentViewDao = mockk<RecentViewDao>()

        coEvery { favoriteDao.getAll() } returns listOf(fav("1"))
        coEvery { recentViewDao.getRecentCharacterIds(50) } returns listOf("1", "3")

        UserContentSyncRunner(repository, favoriteDao, recentViewDao).syncUserContent()

        coVerify(exactly = 1) { repository.refreshCharacterById("1", force = false) }
        coVerify(exactly = 1) { repository.refreshCharacterById("3", force = false) }
        coVerify(exactly = 2) { repository.refreshCharacterById(any(), force = false) }
    }

    private fun fav(id: String) = FavoriteCharacterEntity(
        id = id,
        name = "Name $id",
        height = "",
        mass = "",
        hairColor = "",
        skinColor = "",
        eyeColor = "",
        birthYear = "",
        gender = "",
        homeworld = "",
        films = "",
        species = "",
        vehicles = "",
        starships = "",
        created = "",
        edited = "",
        url = "",
        addedAt = 0L,
    )

    private fun httpException(code: Int): HttpException =
        HttpException(Response.error<String>(code, okhttp3.ResponseBody.create(null, "")))
}

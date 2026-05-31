package com.example.homework_3.data.recent

import com.example.homework_3.MainDispatcherRule
import com.example.homework_3.data.local.RecentViewDao
import com.example.homework_3.model.Character
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecentRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `addView upserts and enforces retention and limit`() = runTest {
        val dao = mockk<RecentViewDao>(relaxed = true)
        val repo = RecentRepositoryImpl(dao)

        repo.addView(sampleCharacter(id = "1", name = "Luke"))

        coVerify(exactly = 1) { dao.upsert(match { it.characterId == "1" && it.characterName == "Luke" }) }
        coVerify(exactly = 1) { dao.deleteOlderThan(any()) }
        coVerify(exactly = 1) { dao.trimToLimit(RecentRetentionPolicy.MAX_ENTRIES) }
    }

    @Test
    fun `clear delegates to dao`() = runTest {
        val dao = mockk<RecentViewDao>(relaxed = true)
        val repo = RecentRepositoryImpl(dao)

        repo.clear()

        coVerify(exactly = 1) { dao.clear() }
    }

    private fun sampleCharacter(id: String, name: String): Character =
        Character(
            id = id,
            name = name,
            height = "unknown",
            mass = "unknown",
            hairColor = "unknown",
            skinColor = "unknown",
            eyeColor = "unknown",
            birthYear = "unknown",
            gender = "unknown",
            homeworld = "",
            created = "",
            edited = "",
            url = "people/$id/"
        )
}


package com.example.homework_3.data

import com.example.homework_3.MainDispatcherRule
import com.example.homework_3.data.local.CachedCharacterDao
import com.example.homework_3.data.local.FavoriteCharacterDao
import com.example.homework_3.data.remote.CharacterDto
import com.example.homework_3.data.remote.SwapiApi
import com.example.homework_3.data.remote.SwapiResponse
import com.example.homework_3.data.settings.CacheTtlPreset
import com.example.homework_3.data.settings.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersRepositoryImplRefreshTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `stale cache - refresh writes new entities to Room`() = runTest {
        val api = mockk<SwapiApi>()
        val favoritesDao = mockk<FavoriteCharacterDao>(relaxed = true)
        val cachedDao = mockk<CachedCharacterDao>(relaxed = true)

        val settingsRepository = object : SettingsRepository {
            override val themeMode: Flow<com.example.homework_3.data.settings.ThemeMode> = flowOf(com.example.homework_3.data.settings.ThemeMode.SYSTEM)
            override suspend fun setThemeMode(mode: com.example.homework_3.data.settings.ThemeMode) = Unit

            override val cacheTtlPreset: Flow<CacheTtlPreset> = flowOf(CacheTtlPreset.ONE_HOUR)
            override suspend fun setCacheTtlPreset(preset: CacheTtlPreset) = Unit

            override val isBackgroundRefreshEnabled: Flow<Boolean> = flowOf(false)
            override suspend fun setBackgroundRefreshEnabled(enabled: Boolean) = Unit

            override val isBackgroundRefreshWifiOnly: Flow<Boolean> = flowOf(false)
            override suspend fun setBackgroundRefreshWifiOnly(wifiOnly: Boolean) = Unit

            override val backgroundRefreshInterval: Flow<com.example.homework_3.data.settings.BackgroundRefreshInterval> =
                flowOf(com.example.homework_3.data.settings.BackgroundRefreshInterval.SIX_HOURS)
            override suspend fun setBackgroundRefreshInterval(interval: com.example.homework_3.data.settings.BackgroundRefreshInterval) = Unit

            override val lastBackgroundRefreshSuccessAt: Flow<Long?> = flowOf(null)
            override suspend fun setLastBackgroundRefreshSuccessAt(timestampMs: Long) = Unit
        }

        val staleUpdatedAt = System.currentTimeMillis() - 10L * 60L * 60L * 1000L
        coEvery { cachedDao.getMaxUpdatedAt() } returns staleUpdatedAt

        coEvery { api.getCharacters(page = any(), search = "lu") } returns SwapiResponse(
            count = 1,
            next = null,
            previous = null,
            results = listOf(CharacterDto(name = "Luke", url = "people/1/"))
        )

        val repo = CharactersRepositoryImpl(
            api = api,
            favoriteDao = favoritesDao,
            cachedDao = cachedDao,
            settingsRepository = settingsRepository
        )

        repo.refreshCharacters(query = "lu", force = false)

        coVerify(exactly = 1) {
            cachedDao.upsertAll(match { list ->
                list.size == 1 && list[0].id == "1" && list[0].name == "Luke"
            })
        }
    }
}


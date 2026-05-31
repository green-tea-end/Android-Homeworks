package com.example.homework_3.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RecentViewDaoRoomTest {

    private lateinit var db: CharacterDatabase
    private lateinit var dao: RecentViewDao

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, CharacterDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.recentViewDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun observeRecent_sortsByViewedAtDesc() = runTest {
        dao.upsert(RecentViewEntity(characterId = "1", characterName = "Luke", viewedAt = 100))
        dao.upsert(RecentViewEntity(characterId = "2", characterName = "Leia", viewedAt = 300))
        dao.upsert(RecentViewEntity(characterId = "3", characterName = "Han", viewedAt = 200))

        val list = dao.observeRecent().first()
        assertEquals(listOf("2", "3", "1"), list.map { it.characterId })
    }

    @Test
    fun trimToLimit_keepsNewestN() = runTest {
        for (i in 1..5) {
            dao.upsert(RecentViewEntity(characterId = i.toString(), characterName = "C$i", viewedAt = i.toLong()))
        }

        dao.trimToLimit(2)

        val list = dao.observeRecent().first()
        assertEquals(listOf("5", "4"), list.map { it.characterId })
    }
}


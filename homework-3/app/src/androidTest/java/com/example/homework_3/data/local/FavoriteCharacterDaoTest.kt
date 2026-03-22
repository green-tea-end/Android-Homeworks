package com.example.homework_3.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteCharacterDaoTest {

    private lateinit var database: CharacterDatabase
    private lateinit var dao: FavoriteCharacterDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            CharacterDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.favoriteCharacterDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun `insert_AndGetAll_returnsItemsOrderedByAddedAtDesc`() = runTest {
        // given
        val oldItem = FavoriteCharacterEntity(
            id = "1",
            name = "Luke Skywalker",
            gender = "male",
            birthYear = "19BBY",
            height = "172",
            mass = "77",
            url = "https://swapi.dev/api/people/1/",
            addedAt = 1000
        )

        val newItem = FavoriteCharacterEntity(
            id = "2",
            name = "Darth Vader",
            gender = "male",
            birthYear = "41.9BBY",
            height = "202",
            mass = "136",
            url = "https://swapi.dev/api/people/4/",
            addedAt = 2000
        )

        // when
        dao.insert(oldItem)
        dao.insert(newItem)
        val result = dao.getAll()

        // then
        assertEquals(listOf(newItem, oldItem), result)
    }
}
package com.example.commission_task.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface SwapiApi {

    @GET("people/")
    suspend fun getCharacters(
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null
    ): SwapiResponse

    @GET
    suspend fun getCharacterByUrl(
        @Url url: String
    ): CharacterDto
}

data class SwapiResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<CharacterDto>
)

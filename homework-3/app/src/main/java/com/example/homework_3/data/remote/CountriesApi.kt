package com.example.homework_3.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CountriesApi {
    // Оставляем только рабочие методы:
    @GET("v3.1/name/{name}")
    suspend fun searchCountries(
        @Path("name") name: String,
        @Query("fields") fields: String = "name,flags,region,subregion,population,area,capital,currencies,languages"
    ): List<CountryResponse>

    @GET("v3.1/region/{region}")
    suspend fun getCountriesByRegion(
        @Path("region") region: String,
        @Query("fields") fields: String = "name,flags,region,subregion,population,area,capital,currencies,languages"
    ): List<CountryResponse>
}
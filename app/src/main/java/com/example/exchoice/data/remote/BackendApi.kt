package com.example.exchoice.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class RegistryResponse(
    val status: String,
    val valid_from: String?,
    val valid_to: String?,
    val regulation: String?,
    val holder: String?,
    val checked_at: String,
    val source_link: String?
)

data class RuleSetResponse(val version: String, val effective_date: String)
data class CatalogDiffResponse(val version: String, val items: List<Map<String, Any?>>)

interface BackendApi {
    @GET("registry/check")
    suspend fun checkRegistry(
        @Query("number") number: String,
        @Query("type") type: String
    ): RegistryResponse

    @GET("content/ruleset/latest")
    suspend fun latestRuleSet(): RuleSetResponse

    @GET("content/catalog/diff")
    suspend fun catalogDiff(@Query("since") since: String): CatalogDiffResponse
}

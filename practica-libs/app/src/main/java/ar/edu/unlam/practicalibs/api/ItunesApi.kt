package ar.edu.unlam.practicalibs.api

import ar.edu.unlam.practicalibs.entity.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ItunesApi {

    @GET("search")
    fun search(@Query("term") term: String): Call<SearchResult>
}
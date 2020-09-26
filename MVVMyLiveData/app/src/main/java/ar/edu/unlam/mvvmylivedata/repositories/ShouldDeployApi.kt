package ar.edu.unlam.mvvmylivedata.repositories

import ar.edu.unlam.mvvmylivedata.model.ShouldDeployModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ShouldDeployApi {

    @GET("api")
    fun get(@Query("tz") timeZone: String): Call<ShouldDeployModel>
}
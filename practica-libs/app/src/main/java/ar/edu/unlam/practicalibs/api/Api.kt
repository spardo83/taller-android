package ar.edu.unlam.practicalibs.api

import ar.edu.unlam.practicalibs.entity.SearchResult
import com.google.gson.Gson
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Api {

    private fun getApi(): ItunesApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()

        return retrofit.create(ItunesApi::class.java)
    }

    fun search(terms: String, callback: Callback<SearchResult>) {
        getApi().search(terms.replace(" ", "+")).enqueue(callback)
    }
}
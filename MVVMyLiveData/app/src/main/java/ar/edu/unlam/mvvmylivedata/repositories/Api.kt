package ar.edu.unlam.mvvmylivedata.repositories

import ar.edu.unlam.mvvmylivedata.model.ShouldDeployModel
import com.google.gson.Gson
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Api() {

    private fun getAPI(): ShouldDeployApi {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .baseUrl("https://shouldideploy.today/")
            .build()
        return retrofit.create(ShouldDeployApi::class.java)
    }

    fun get(timeZone: String, callback: Callback<ShouldDeployModel>) {
        getAPI().get(timeZone).enqueue(callback)
    }

}
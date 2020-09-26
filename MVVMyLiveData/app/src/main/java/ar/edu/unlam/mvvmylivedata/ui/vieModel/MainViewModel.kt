package ar.edu.unlam.mvvmylivedata.ui.vieModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ar.edu.unlam.mvvmylivedata.repositories.Api
import ar.edu.unlam.mvvmylivedata.model.ShouldDeployModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MainViewModel : ViewModel() {

    val model = MutableLiveData<ShouldDeployModel>()

    fun fetchApiData(timeZone: String) {

        Api().get(timeZone, object : Callback<ShouldDeployModel> {
            override fun onResponse(
                call: Call<ShouldDeployModel>,
                response: Response<ShouldDeployModel>
            ) {
                if (response.isSuccessful) {
                    model.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<ShouldDeployModel>, t: Throwable) {
                throw t
            }
        })
    }
}


class BadRequestException : Exception()
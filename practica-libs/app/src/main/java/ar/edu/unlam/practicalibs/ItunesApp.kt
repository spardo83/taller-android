package ar.edu.unlam.practicalibs

import android.app.Application

class ItunesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ItunesApp
    }
}
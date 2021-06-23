package net.zero.three.api.repository

import android.app.Application
import androidx.lifecycle.LiveData
import net.zero.three.api.DataSource
import net.zero.three.api.payload.Resource
import net.zero.three.api.service.NetworkOnlyResource
import net.zero.three.persistant.AppExecutors

class AuthRepository(application: Application, val appExecutors: AppExecutors) {
    private val auth = DataSource.Private.auth



    /*
    .
    Contoh hubungan repository dengan interface
    .
    fun exAmple(reqExample: ReqExample): LiveData<Resource<ResExample>> {
        return object : NetworkOnlyResource<ResExample, ResExample>(appExecutors) {
            override fun handleCallResult(item: Access?): ResExample? = item
            override fun createCall() = auth.exAmple(reqExample)
        }.asLiveData()
    }
    */
}
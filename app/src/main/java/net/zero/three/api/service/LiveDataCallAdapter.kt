package net.zero.three.api.service

import androidx.lifecycle.LiveData
import net.zero.three.api.payload.ApiResponse
import net.zero.three.api.payload.ApiWrapper
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<ApiWrapper<R>, LiveData<ApiResponse<R>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<ApiWrapper<R>>): LiveData<ApiResponse<R>> {
        return object : LiveData<ApiResponse<R>>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<ApiWrapper<R>> {

                        override fun onResponse(call: Call<ApiWrapper<R>>, response: Response<ApiWrapper<R>>) {
                            postValue(ApiResponse.create(response))
                        }

                        override fun onFailure(call: Call<ApiWrapper<R>>, throwable: Throwable) {
                            postValue(ApiResponse.create(throwable))
                        }
                    })
                }
            }
        }
    }
}
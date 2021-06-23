package net.zero.three.api.service

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import net.zero.three.api.payload.*
import net.zero.three.persistant.AppExecutors
import net.zero.three.constants.Constants

abstract class NetworkOnlyResource<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        appExecutors.mainThread().execute {
            result.value = Resource.loading(null)
        }
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            if(response != null) {
                when(response){
                    is ApiSuccessResponse -> {
                        appExecutors.diskIO().execute {
                            val res = handleCallResult(processResponse(response.body))
                            appExecutors.mainThread().execute {
                                setValue(Resource.success(res))
                            }
                        }
                    }
                    is ApiErrorResponse -> {
                        onFetchFailed()
                        setValue(Resource.error(response.errorMessage, response.code,null))
                    }
                    is ApiEmptyResponse -> {
                        setValue(Resource.success(null))
                    }
                }
            } else {
                onFetchFailed()
                setValue(Resource.error("Oops, there is problem when processing data.", Constants.ERROR_FAILED_CONNECT,null))
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(data: RequestType) = data

    @WorkerThread
    protected abstract fun handleCallResult(item: RequestType?): ResultType?

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}
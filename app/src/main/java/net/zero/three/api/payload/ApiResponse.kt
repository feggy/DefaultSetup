package net.zero.three.api.payload

import net.zero.three.constants.Constants.Companion.ERROR_FAILED_CONNECT
import net.zero.three.constants.Constants.Companion.ERROR_JSON_PARSE
import net.zero.three.constants.Constants.Companion.ERROR_REQUEST_NOT_VALID
import retrofit2.Response

open class ApiResponse<T> {
    companion object {

        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(ERROR_FAILED_CONNECT, "Something is wrong, check your internet connection")
        }

        fun <T> create(response: Response<ApiWrapper<T>>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body?.status == "SUCCESS") {
                    val data = body.data
                    return if (data == null)
                        ApiEmptyResponse()
                    else
                        ApiSuccessResponse(data)
                } else {
                    val error = body?.error

                    val code = error?.get(0)?.code
                    val message = error?.get(0)?.message

                    ApiErrorResponse(
                        code ?: ERROR_REQUEST_NOT_VALID,
                        message ?: "Request not valid"
                    )
                }
            } else {
                ApiErrorResponse(
                    ERROR_JSON_PARSE,
                    "Json parse error"
                )
            }
        }
    }
}

data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()
class ApiEmptyResponse<T> : ApiResponse<T>()
data class ApiErrorResponse<T>(val code: String, val errorMessage: String) : ApiResponse<T>()
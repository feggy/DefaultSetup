package net.zero.three.api.payload

class Resource<T> private constructor(
    val status: Status,
    val code: String?,
    val data: T?,
    val message: String) {

    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, "00", data, "")
        }

        fun <T> error(msg: String, code: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, code, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, null, data, "")
        }
    }
}
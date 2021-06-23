package net.zero.three.api

import net.zero.three.api.private_interface.AuthInterface
import net.zero.three.api.service.ApiConverterFactory
import net.zero.three.api.service.LiveDataCallAdapterFactory
import net.zero.three.constants.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

class DataSource {

    companion object {
        private val logging: HttpLoggingInterceptor by lazy {
            val l = HttpLoggingInterceptor { message -> Timber.tag("_ZEROTHREE_").e(message) }
            l.level = HttpLoggingInterceptor.Level.BODY
            return@lazy l
        }

        private val interceptLogin: HttpLoggingInterceptor by lazy {
            val l = HttpLoggingInterceptor {
                try {
                    val jsonObj = JSONObject(it)
                    Timber.tag("interceptLogin").e(jsonObj.toString())
                    Timber.tag("interceptLogin").e(jsonObj.getBoolean("status").toString())
                    if (!jsonObj.getBoolean("status")) {
                        val error = jsonObj.getJSONArray("error").getJSONObject(0)
                        val code: String = error.getString("code")
                        Timber.tag("interceptLogin").e(error.toString())
                        Timber.tag("interceptLogin").e(code)
                    }
                } catch (ex: Exception) {

                }
            }
            l.level = HttpLoggingInterceptor.Level.BODY
            return@lazy l
        }

        val private: Retrofit by lazy {
            val client = OkHttpClient.Builder()
            client.connectTimeout(Constants.TIME_OUT_REQUEST.toLong(), TimeUnit.MILLISECONDS)
            client.addInterceptor { chain ->

                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    /*.addHeader("Access-Key", SessionManager.instance.accessToken)
                    .addHeader("User-Agent", USER_AGENT)
                    .addHeader("language", SessionManager.instance.changeLanguage)*/
                    .build()
                chain.proceed(request)
            }
            client.addInterceptor(logging)
            client.addInterceptor(interceptLogin)
            client
                .readTimeout(Constants.TIME_OUT_REQUEST.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(Constants.TIME_OUT_REQUEST.toLong(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)

            Retrofit.Builder()
                .baseUrl(net.zero.three.BuildConfig.BASE_URL)
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addConverterFactory(ApiConverterFactory())
                .client(client.build())
                .build()
        }
    }

    open class Private {
        companion object {
            val auth: AuthInterface by lazy {
                private.create(AuthInterface::class.java)
            }

        }
    }
}
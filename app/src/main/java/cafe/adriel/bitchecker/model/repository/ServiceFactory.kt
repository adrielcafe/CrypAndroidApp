package cafe.adriel.bitchecker.model.repository

import cafe.adriel.bitchecker.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.reflect.KClass

object ServiceFactory {

    fun <T: Any> newInstance(baseUrl: String, serviceClass: KClass<T>): T =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getClient())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(serviceClass.java)

    private fun getClient() =
            OkHttpClient.Builder()
                    .addInterceptor(getLogging())
                    .build()

    private fun getLogging() =
            HttpLoggingInterceptor().setLevel(
                    if(BuildConfig.RELEASE) HttpLoggingInterceptor.Level.NONE
                    else HttpLoggingInterceptor.Level.BASIC)

}
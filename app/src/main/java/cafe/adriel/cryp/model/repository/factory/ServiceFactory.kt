package cafe.adriel.cryp.model.repository.factory

import cafe.adriel.cryp.BuildConfig
import cafe.adriel.cryp.model.repository.adapter.BigDecimalAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object ServiceFactory {

    inline fun <reified T> newInstance(baseUrl: String) : T =
            Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getClient())
                    .addConverterFactory(MoshiConverterFactory.create(getJsonConverter()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(T::class.java)

    fun getClient() : OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(getLogging())
                    .build()

    fun getJsonConverter() : Moshi =
            Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .add(BigDecimalAdapter())
                    .build()

    private fun getLogging() =
            HttpLoggingInterceptor().setLevel(
                    if(BuildConfig.RELEASE) HttpLoggingInterceptor.Level.NONE
                    else HttpLoggingInterceptor.Level.BODY)

}
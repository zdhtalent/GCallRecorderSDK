package com.geely.callrecord.network

import com.geely.brokerkotlintest.intercepor.LoggingInterceptor
import com.geely.callrecord.content.CallRecordContent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author zdh
 * date 2019/11/20.
 */
object ServiceCreator {

    const val BASE_URL = "https://bomt-dev.test.geely.com/bomt-app/advice/save"

    private val httpClient = OkHttpClient.Builder()

    private val builder = Retrofit.Builder()
        .baseUrl(CallRecordContent.BASE_URL)
        .client(
            httpClient
                .addInterceptor(
                    LoggingInterceptor.Builder()
                        .loggable(true)
                        .request()
                        .requestTag("Request")
                        .response()
                        .responseTag("Reponse")
                        .build()
                )
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit = builder.build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

}
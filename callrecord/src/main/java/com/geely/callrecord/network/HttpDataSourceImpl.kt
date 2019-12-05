package com.geely.callrecord.network

import android.content.Intent
import com.geely.callrecord.bean.BaseResultBean
import com.geely.callrecord.network.api.ApiService
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author zdh
 * date 2019/12/4.
 */
class HttpDataSourceImpl : HttpDataSource {

    private val netService = ServiceCreator.create(ApiService::class.java)

    override suspend fun uploadRecordFiles(files: List<MultipartBody.Part?>?): BaseResultBean =
        netService.uploadRecordFiles(files).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else continuation.resumeWithException(RuntimeException("response body is null"))
                }
            })
        }
    }

}
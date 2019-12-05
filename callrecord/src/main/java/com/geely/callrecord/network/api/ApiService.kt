package com.geely.callrecord.network.api

import com.geely.callrecord.bean.BaseResultBean
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @author zdh
 * date 2019/12/4.
 */
interface ApiService {
    //意见反馈接口
    @Multipart
    @POST("advice/save")
    fun uploadRecordFiles(
        @Part files: List<MultipartBody.Part?>?
    ): Call<BaseResultBean>
}
package com.geely.callrecord.network

import com.geely.callrecord.bean.BaseResultBean
import okhttp3.MultipartBody

/**
 * @author zdh
 * date 2019/12/4.
 */
interface HttpDataSource {
    suspend fun uploadRecordFiles(
        files: List<MultipartBody.Part?>?
    ): BaseResultBean
}
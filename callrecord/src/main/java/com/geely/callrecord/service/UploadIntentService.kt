package com.geely.callrecord.service

import android.app.IntentService
import android.content.Intent
import com.geely.callrecord.network.HttpDataSourceImpl
import com.geely.callrecord.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.geely.callrecord.bean.BaseResultBean
import com.geely.callrecord.helper.LogUtils
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * @author zdh
 * date 2019/12/4.
 */
class UploadIntentService : IntentService("UploadIntentService") {
    companion object {
        val TAG = UploadIntentService::class.java.simpleName
    }

    var dirPath = ""
    lateinit var result: BaseResultBean

    override fun onHandleIntent(intent: Intent?) {
        intent?.getStringExtra("dirPath").let {
            dirPath = it ?: ""
        }
        val files = FileUtils.getFilesFromDir(dirPath)
        if (!files.isNullOrEmpty()) {
            val files_upload = ArrayList<MultipartBody.Part>()
            val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)//表单类型

            files.forEach {
                val requestFile = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                files_upload.add(MultipartBody.Part.createFormData("files", it.name, requestFile))
                val requestBody = it.asRequestBody("image".toMediaTypeOrNull())
                builder.addFormDataPart("file", it.name, requestBody)
            }
            val parts: List<MultipartBody.Part>? = builder.build().parts
            runBlocking {
                result = uploadRecordFiles(parts)
            }
            result.let {
                if (it.status == 200){
                    LogUtils.d(TAG, "文件上传成功!")
                    //删除目录下所有音频文件
                    val file = File(dirPath)
                    FileUtils.deleteFile(file)
                }else{
                    LogUtils.d(TAG, "文件上传失败!")
                }
            }


        }

//        CoroutineScope().launch {
//
//        }
//        val result = uploadRecordFiles(parts)


    }

    private suspend fun uploadRecordFiles(
        files: List<MultipartBody.Part?>?
    ) = withContext(Dispatchers.IO) {
        val result = HttpDataSourceImpl().uploadRecordFiles(files)
        result
    }


}
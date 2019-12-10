package com.geely.callrecord.service

import android.app.IntentService
import android.content.Intent
import com.geely.callrecord.CallRecord
import com.geely.callrecord.network.HttpDataSourceImpl
import com.geely.callrecord.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.geely.callrecord.bean.BaseResultBean
import com.geely.callrecord.content.CallRecordContent
import com.geely.callrecord.helper.LogUtils
import com.geely.callrecord.helper.PrefsHelper
import com.geely.callrecord.utils.copyTo
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
    private var result: BaseResultBean? = null

    override fun onHandleIntent(intent: Intent?) {

        val base_url = PrefsHelper.readPrefString(this, CallRecord.PREF_BASE_URL)
        CallRecordContent.BASE_URL = base_url ?: ""
        LogUtils.d(TAG, "url=${CallRecordContent.BASE_URL}")

        if (!base_url.isNullOrEmpty()) {
            intent?.getStringExtra("dirPath").let {
                dirPath = it ?: ""
            }
            val files = FileUtils.getFilesFromDir(dirPath)
            if (!files.isNullOrEmpty()) {
                val files_upload = ArrayList<MultipartBody.Part>()
                val builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)//表单类型

                files.forEach {
                    val file = File("${dirPath}/copy_${it.name}")
                    runBlocking {
                        it.copyTo(file)
                    }
                    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    files_upload.add(
                        MultipartBody.Part.createFormData(
                            "files",
                            it.name,
                            requestFile
                        )
                    )
                    val requestBody = file.asRequestBody("audio".toMediaTypeOrNull())
                    builder.addFormDataPart("file", it.name, requestBody)
//                FileUtils.deleteFile(file)
                }
                val parts: List<MultipartBody.Part>? = builder.build().parts
                runBlocking {
                    try {
                        result = uploadRecordFiles(parts)
                    } catch (e: Throwable) {
//                        LogUtils.d(TAG,"ERROR=${e.message.toString()}")
                        val file = File(dirPath)
                        FileUtils.deleteCopyFile(file)
                    }
                }
//                LogUtils.d(TAG,"获取result")
                result.let {
                    if (it?.status == 200) {
                        LogUtils.d(TAG, "文件上传成功!")
                        //删除目录下所有音频文件
                        val file = File(dirPath)
                        FileUtils.deleteFile(file)
                    } else {
                        LogUtils.d(TAG, "文件上传失败!")
                        //删除目录下所有拷贝音频文件
                        val file = File(dirPath)
                        FileUtils.deleteCopyFile(file)
                    }
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
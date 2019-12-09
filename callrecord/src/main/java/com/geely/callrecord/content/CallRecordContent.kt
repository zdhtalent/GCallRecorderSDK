package com.geely.callrecord.content

import android.Manifest

/**
 * @author zdh
 * date 2019/12/5.
 */
object CallRecordContent {
    var BASE_URL = ""

    /**
     * 权限组
     */
    val PERMISSIONS_GROUP = arrayOf(
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_PHONE_STATE
    )

}
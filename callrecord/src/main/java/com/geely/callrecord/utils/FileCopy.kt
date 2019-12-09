package com.geely.callrecord.utils

import java.io.File

/**
 * @author zdh
 * date 2019/12/6.
 */

//文件拷贝
fun File.copyTo(file: File) {
    inputStream().use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}
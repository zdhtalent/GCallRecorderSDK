package com.geely.callrecord.utils

import android.util.Log
import com.geely.callrecord.helper.LogUtils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import java.util.Arrays

/**
 * @author zdh
 * date 2019/11/18.
 */
object FileUtils {
    private val TAG = FileUtils::class.java.simpleName


    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    fun deleteSimpleFile(filePath: String): Boolean {
        val file = File(filePath)
        if (file.isFile && file.exists()) {
            Log.d(TAG, "文件删除成功!")
            return file.delete()
        }
        Log.d(TAG, "文件删除失败!")
        return false
    }

    //flie：要删除的文件夹的所在位置
    fun deleteFile(file: File) {
        if (file.isDirectory) {
            val files = file.listFiles()
            for (i in files!!.indices) {
                val f = files[i]
                deleteFile(f)
            }
            //            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete()
        }
    }

    //flie：要删除的文件夹的所在位置
    fun deleteCopyFile(file: File) {
        if (file.isDirectory) {
            val files = file.listFiles()
            files?.forEach {
                if (it.name.contains("copy")){
                    it.delete()
                }
            }
        } else if (file.exists()) {
            file.delete()
        }
    }


    /**
     * 查找单个文件是否存在
     *
     * @param filePath 文件名
     * @return 文件存在返回true，否则返回false
     */
    fun existFile(filePath: String): Boolean {
        val file = File(filePath)
        return if (file.exists()) {
            true
        } else false
    }

    /**
     * 查找目录下的所有文件
     *
     * @param path 目录路径
     * @return 文件路径列表
     */
    fun getFilesFromDir(path: String): List<File>? {
        val file = File(path)
        val files = file.listFiles()
        if (files == null) {
            Log.e("error", "空目录")
            return null
        }
        return Arrays.asList(*files)
    }


    /**
     * 查找目录下的所有文件路径
     *
     * @param path 目录路径
     * @return 文件路径列表
     */
    fun getFilesAllName(path: String): List<String>? {
        val file = File(path)
        val files = file.listFiles()
        if (files == null) {
            LogUtils.d(TAG, "空目录")
            return null
        }
        val s = ArrayList<String>()
        for (i in files.indices) {
            LogUtils.d(TAG, files[i].absolutePath)
            s.add(files[i].absolutePath)
        }
        return s
    }


    /**
     * 判断目录是否为空目录
     *
     * @param path 目录路径
     * @return 文件路径列表
     */
    fun isDirEmpty(path: String): Boolean {
        val file = File(path)
        val files = file.listFiles() ?: return true
        return false
    }

    /**
     * 获取文件SHA1
     *
     * @param file 文件对象
     * @return 返回sha1串
     */
    fun getFileSha1(file: File): String {
        var input: FileInputStream? = null
        try {
            input = FileInputStream(file)
            val digest = MessageDigest.getInstance("SHA-1")
            val buffer = ByteArray(1024 * 1024 * 10)

            var len = 0
            len = input.read(buffer)
            while (len > 0) {
                digest.update(buffer, 0, len)
            }
            var sha1 = BigInteger(1, digest.digest()).toString(16)
            val length = 40 - sha1.length
            if (length > 0) {
                for (i in 0 until length) {
                    sha1 = "0$sha1"
                }
            }
            return sha1
        } catch (e: IOException) {
            Log.d(TAG, e.message + "")
            //            System.out.println(e);
        } catch (e: NoSuchAlgorithmException) {
            //            System.out.println(e);
            Log.d(TAG, e.message + "")
        } finally {
            try {
                input?.close()
            } catch (e: IOException) {
                //                System.out.println(e);
                Log.d(TAG, e.message + "")
            }

        }

        return ""
    }


}

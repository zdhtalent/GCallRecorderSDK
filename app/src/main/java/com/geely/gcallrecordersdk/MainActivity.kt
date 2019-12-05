package com.geely.gcallrecordersdk

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.geely.callrecord.CallRecord
import com.geely.callrecord.content.CallRecordContent
import com.geely.callrecord.helper.LogUtils
import com.geely.callrecord.service.UploadIntentService
import com.geely.callrecord.utils.FileUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var rxPermissions: RxPermissions
    private lateinit var disposable: Disposable
    private lateinit var callRecord: CallRecord
    lateinit var tm: TelephonyManager
    var dirPath = ""
    val phone = "18352975185"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rxPermissions = RxPermissions(this)
        requestPermissions()

        tm = getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE)

        callRecord = CallRecord.Builder(this)
            .setLogEnable(true) //正式上线是关闭，默认为false
            .setRecordFileName("record")//设置文件名
            .setRecordDirName("callrecord") //设置目录名
            .setRecordDirPath(getExternalFilesDir(null)?.path) // optional & default value 设置文件存储路径
            .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // optional & default value
            .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB) // optional & default value
            .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION) // optional & default value
            .setShowSeed(true) // optional & default value ->Ex: RecordFileName_incoming.amr || RecordFileName_outgoing.amr
            .build()

        callRecord.startCallReceiver()

        dirPath = getExternalFilesDir(null)?.absolutePath + "/callrecord"

        Log.d(TAG, dirPath)

        findViewById<Button>(R.id.button).setOnClickListener {
            if (!lacksPermission(this, android.Manifest.permission.CALL_PHONE)) {
                val intent = Intent()
                    .setAction(Intent.ACTION_CALL)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setData(Uri.parse("tel:$phone"))
                startActivity(intent)
            } else {
                Toast.makeText(this, "缺少拨打电话的权限!", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.button2).setOnClickListener {
            FileUtils.getFilesAllName(dirPath)
        }

        findViewById<Button>(R.id.button3).setOnClickListener {
            val file = File(dirPath)
            FileUtils.deleteFile(file)
        }

    }

    private fun lacksPermission(mContexts: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            mContexts,
            permission
        ) == PackageManager.PERMISSION_DENIED
    }

    private fun requestPermissions() {
        disposable = rxPermissions.requestEach(*CallRecordContent.PERMISSIONS_GROUP)
            .subscribe { permission ->
                LogUtils.d(TAG, "权限名称:${permission.name}申请结果:${permission.granted}")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
        callRecord.stopCallReceiver()
    }

    private var listener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String) {
            super.onCallStateChanged(state, phoneNumber)
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.d(TAG, "挂断电话")
                    if (!FileUtils.isDirEmpty(dirPath)) {
                        Log.d(TAG, "执行文件上传service")
                        //启动intentservice上传录音文件
                        val serviceIntent =
                            Intent(this@MainActivity, UploadIntentService::class.java)
                        serviceIntent.putExtra("dirPath", dirPath)
                        startService(serviceIntent)
                    }
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> Log.d(TAG, "接听电话")
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.d(TAG, "响铃：来电号码$phoneNumber")
                }
            }
        }
    }


}

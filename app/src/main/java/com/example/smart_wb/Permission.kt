package com.example.smart_wb

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_permission.*


/**2021-06-01
joker
다른 앱 위에 표시되는 앱 권한 받기*/


class Permission : AppCompatActivity() {


    // 권한 정의
    private val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)

        checkPermission()
    }


    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) { // 체크
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )

                pbt.setOnClickListener {
                    startActivityForResult(
                        intent,
                        ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE
                    )
                }

            } else {
                startActivity(Intent(this, Permission2::class.java))
                Log.d("Permission.kt", "이미 권한 허용한 사용자 FragmentMainTimer로 이동")
                finish()
            }
        } else { //마시멜로우 이하일 경우 권한 자동 부여됨
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리
                Log.d("퍼미션", "퍼미션 동의 못 얻었을 경우")
                startActivity(Intent(this, Permission::class.java))
                finish()
            } else {
                Log.d("Permission.kt", "퍼미션 동의 얻었을 경우")
                startActivity(Intent(this, Permission2::class.java))
                Log.d("Permission.kt", "Permission2로 이동")
                finish()
            }
        }
    }

}


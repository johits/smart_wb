package com.smartlock.smart_wb.View_Controller.Activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartlock.smart_wb.Model.Shared.GuideShowCheckShared
import com.smartlock.smart_wb.R
import kotlinx.android.synthetic.main.activity_privacy_consent.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


/**2021-06-27
joker
개인정보처리방침 동의서*/

class PrivacyConsent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_consent)

        GuideShowCheckShared.getPrivacy(this) //동의 여부 불러오기
        if(GuideShowCheckShared.getPrivacy(this)){
            //화면 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        privacy.text = readTxt()
        privacy_ok.setOnClickListener {
            if(privacy_check.isChecked){
                GuideShowCheckShared.setPrivacy(this,true)
                //화면 이동
                startActivity(Intent(this, MainActivity::class.java))
                val toast = Toast.makeText(this, "반갑습니다! 스마트락을 시작합니다.", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0)
                toast.show()
                finish()
            }else{
                val toast = Toast.makeText(this, "개인정보처리방침 약관에 동의 후 이용 가능합니다.", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_VERTICAL,0,600)
                toast.show()

            }
        }
    }

    private fun readTxt(): String? {
        var data: String? = null
        val inputStream: InputStream = resources.openRawResource(R.raw.privacy)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var i: Int
        try {
            i = inputStream.read()
            while (i != -1) {
                byteArrayOutputStream.write(i)
                i = inputStream.read()
            }
            val charset: Charset = Charsets.UTF_8
            data = String(byteArrayOutputStream.toByteArray(), charset)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return data
    }

}
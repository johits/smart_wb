package com.example.smart_wb.SQLite

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * 2021-06-06 yama 타이머 테이블 SQLiteOpenHelper
 * */
class TimerDbHelper
    (
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    //테이블생성
    override fun onCreate(db: SQLiteDatabase) {
        var sql: String = "CREATE TABLE if not exists timer (" +
                "id INTEGER primary key autoincrement," +
                "date TEXT," +
                "time TEXT," +
                "settingTime INTEGER," +
                "success INTEGER DEFAULT 0," +
                "flower INTEGER DEFAULT 0);";

        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql: String = "DROP TABLE if exists timer"

        db.execSQL(sql)
        onCreate(db)
    }

    //스크린타임 성공시 success=1 로 업데이트, 받은꽃 갯수 추가
    fun upDate(date: String, time: String, flower:Int) {
        val db: SQLiteDatabase = writableDatabase
        val sql = "UPDATE timer SET success=1, flower=${flower} WHERE date='${date}' and time='${time}';"
        db.execSQL(sql)
        db.close()
    }

    //스크린타임 시작시 데이터 인서트
    fun insert(date: String, time: String, settingTime: Int) {
        val db: SQLiteDatabase = writableDatabase
        val sql =
            "INSERT INTO timer(date, time, settingTime) VALUES('${date}', '${time}', ${settingTime});"
        db.execSQL(sql)
        db.close()
    }

    //데이터 삭제 사용할 일 없음?
    fun delete(date: String, time: String) {
        val db: SQLiteDatabase = writableDatabase
        val sql = "DELETE FROM timer WHERE date='${date}' and time='${time}';"
        db.execSQL(sql)
        db.close()
    }

    //셀렉트 데이터 모두 불러오기
    fun select(): ArrayList<TimerData> {
        val result = arrayListOf<TimerData>()
        val db: SQLiteDatabase = writableDatabase

        var cursor: Cursor = db.rawQuery("SELECT * FROM timer", null)
        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(0) //pk
            val date: String = cursor.getString(1) //ex 2021-11-11
            val time: String = cursor.getString(2) //ex 11:11:00
            val settingTime: Int = cursor.getInt(3)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장
            val success: Int = cursor.getInt(4) //디폴트가 0 = 실패, 1 = 성공
            val flower:Int =cursor.getInt(5) //디폴트가 0
            var data: TimerData = TimerData(id, date, time, settingTime, success, flower)
            result?.add(data)
        }
        db.close()
        return result
    }



    //동일한 날짜 데이터 불러오기
    fun select(date:String):ArrayList<TimerData>{
        Log.d("cal", date)
        val result = arrayListOf<TimerData>()
        val db: SQLiteDatabase = writableDatabase
        val sql = "SELECT * FROM timer WHERE date='"+ date +"';"

        val cursor:Cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(0)
            val date: String = cursor.getString(1)
            val time: String = cursor.getString(2)
            val settingTime: Int = cursor.getInt(3)
            val success: Int = cursor.getInt(4)
            val flower:Int =cursor.getInt(5)
            var data: TimerData = TimerData(id, date, time, settingTime, success, flower)
            result?.add(data)

        }
        db.close()
        return result
    }
}
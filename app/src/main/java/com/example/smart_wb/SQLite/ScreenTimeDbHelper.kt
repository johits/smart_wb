package com.example.smart_wb.SQLite

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
* 2021-06-14 yama screenTime 테이블 SQLiteOpenHelper
 * timer 테이블 구조 변경
 *
* */
class ScreenTimeDbHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        var sql: String = "CREATE TABLE if not exists screenTime (" +
                "id INTEGER primary key autoincrement," +
                "year INTEGER," +
                "month INTEGER," +
                "day INTEGER," +
                "time TEXT," +
                "settingTime INTEGER," +
                "success INTEGER DEFAULT 0," +
                "flower INTEGER DEFAULT 0);";

       db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql: String = "DROP TABLE if exists screenTime"

        db.execSQL(sql)
        onCreate(db)
    }

    //스크린타임 시작시 데이터 인서트
    fun insert(year: Int, month:Int, day:Int, time: String, settingTime: Int) {
        val db: SQLiteDatabase = writableDatabase
        val sql = "INSERT INTO screenTIme(year, month, day, time, settingTime) " +
                    "VALUES('${year}', '${month}', '${day}', '${time}', ${settingTime});"
        db.execSQL(sql)
        db.close()
    }

    //차트 더미데이터 인서트용
    fun chartInsert(year: Int, month:Int, day:Int, time: String, settingTime: Int) {
        val db: SQLiteDatabase = writableDatabase
        val sql = "INSERT INTO screenTIme(year, month, day, time, settingTime, success) " +
                "VALUES('${year}', '${month}', '${day}', '${time}', ${settingTime}, 1);"
        db.execSQL(sql)
        db.close()
    }

    //셀렉트 데이터 모두 불러오기
    fun select(): ArrayList<ScreenTimeData> {
        val result = arrayListOf<ScreenTimeData>()
        val db: SQLiteDatabase = writableDatabase
        val sql ="SELECT * FROM screenTime;"

        var cursor: Cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(0) //pk
            val year: Int = cursor.getInt(1)
            val month: Int = cursor.getInt(3)
            val day: Int = cursor.getInt(4)
            val time: String = cursor.getString(5) //ex 11:11:00
            val settingTime: Int = cursor.getInt(5)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장
            val success: Int = cursor.getInt(6) //디폴트가 0 = 실패, 1 = 성공
            val flower:Int =cursor.getInt(7) //디폴트가 0
            var data: ScreenTimeData = ScreenTimeData(id, year, month, day, time, settingTime, success, flower)
            result?.add(data)
        }
        db.close()
        return result
    }

    //차트 데이터 불러오기 성공한 데이터 만
    //같은 날짜면 시간 합친다.
    fun weekSelect(){
        val result = arrayListOf<TimerData>()
        val db: SQLiteDatabase = writableDatabase
        val sql = "SELECT year, month, day sum(settingTime) FROM screenTime" +
                " WHERE success=1 group by date;"

        val cursor: Cursor = db.rawQuery(sql, null)
        while(cursor.moveToNext()){
            val year:Int = cursor.getInt(0)
            val month:Int = cursor.getInt(1)
            val day:Int = cursor.getInt(2)
            val settingTime: Int = cursor.getInt(1)

            Log.d("tag", "year:$year , month:$month ," +
                    " day:$day, settingTime:$settingTime")
        }
    }
}
package com.example.smart_wb.SQLite

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * 2021-06-06 yama 꽃 테이블 SQLiteOpenHelper
 * */
class FLowerDbHelper (
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version){

    override fun onCreate(db: SQLiteDatabase) {
        var sql:String = "CREATE TABLE if not exists flower (" +
                "id INTEGER PRIMARY KEY autoincrement," +
                "date TEXT," +
                "time TEXT," +
                "getCount INTEGER" +
                ");"

        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldViersion: Int, newVersion: Int) {
        val sql: String = "DROP TABLE if exists flower"

        db.execSQL(sql)
        onCreate(db)
    }

    //업데이트 사용할 일 없다?
    fun upDate(date: String, time: String) {

    }

    //인서트, 꽃을 받거나 사용했을 때
    fun insert(date:String, time:String, getCount:Int) {
        var db: SQLiteDatabase = writableDatabase
        var sql ="INSERT INTO flower(date, time, getCount) VALUES('${date}', '${time}', ${getCount});"
        db.execSQL(sql)
//        db.close()
    }

    //데이터 삭제 사용할 일 없음?
    fun delete(date: String, time: String) {
        var db: SQLiteDatabase = writableDatabase
        var sql ="DELETE FROM flower WHERE date='${date}' and time='${time}';"
        db.execSQL(sql)
    }
    //셀렉트 데이터 불러오기
    fun select():ArrayList<FlowerData> {
        var result = arrayListOf<FlowerData>()
        var db: SQLiteDatabase = writableDatabase

        var cursor: Cursor = db.rawQuery("SELECT * FROM flower", null)
        while (cursor.moveToNext()){
            val id:Int=cursor.getInt(0)
            val date:String=cursor.getString(1)
            val time:String=cursor.getString(2)
            val getCount=cursor.getInt(3)

            var data:FlowerData= FlowerData(id,date,time,getCount)
            result?.add(data)
        }
        return result
    }
}
package com.example.smart_wb.Model.SQLite

import android.annotation.SuppressLint
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
    companion object{
        const val TAG = "ScreenTimerDbHelper"
    }
    //테이블생성
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

    //버전관리용//현재사용안함
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val sql: String = "DROP TABLE if exists screenTime"

        db.execSQL(sql)
        onCreate(db)
    }

    //스크린타임 시작시 데이터 인서트
    fun insert(year: Int, month:Int, day:Int, time: String, settingTime: Int) {
        val db: SQLiteDatabase = writableDatabase
        Log.d("chart", "$year , $month , $day , $time , $settingTime")
        val sql = "INSERT INTO screenTime(year, month, day, time, settingTime) " +
                    "VALUES(${year}, ${month}, ${day}, '${time}', ${settingTime});"
        db.execSQL(sql)
        db.close()

        Log.d(TAG, "insert: 저장이 되냐")
    }

    //스크린타임 성공시 데이터 업데이트
    fun update(flower:Int ){
        val db: SQLiteDatabase = writableDatabase
//        val sql="UPDATE screenTime SET success=1, flower=$flower " +
//                "WHERE year=$year and month=$month and day=$day and time=$time and settingTime"
        //마지막행 불러와서 업데이트 한다.
        val sql = "UPDATE screenTime set success = 1, flower=$flower WHERE id = (SELECT MAX(id) FROM screenTime);"

        db.execSQL(sql)
        db.close()
    }

    //차트 더미데이터 인서트용
    fun chartInsert(year: Int, month:Int, day:Int, time: String, settingTime: Int) {
        val db: SQLiteDatabase = writableDatabase
        Log.d("chart", "$year , $month , $day , $time , $settingTime")
        val sql = "INSERT INTO screenTime(year, month, day, time, settingTime, success) " +
                "VALUES(${year}, ${month}, ${day}, '${time}', ${settingTime}, 1);"
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
            val month: Int = cursor.getInt(2)
            val day: Int = cursor.getInt(3)
            val time: String = cursor.getString(4) //ex 11:11:00
            val settingTime: Int = cursor.getInt(5)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장
            val success: Int = cursor.getInt(6) //디폴트가 0 = 실패, 1 = 성공
            val flower:Int =cursor.getInt(7) //디폴트가 0
            var data: ScreenTimeData = ScreenTimeData(id, year, month, day, time, settingTime, success, flower)
            result?.add(data)
//            Log.d("chart", "id:${data.id} , year:${data.year} , month:${data.month} , day:${data.day} , time:${data.time} , settingTime:${data.settingTime} , success:${data.success} , flower:${data.flower}")
        }
        db.close()
        return result
    }

    //달력 데이터 불러오기//도전 기록이 있는 날짜에 점찍기용
    @SuppressLint("Recycle")
    fun calendarSelect():ArrayList<ScreenTimeData>{
        val result = arrayListOf<ScreenTimeData>()
        val db:SQLiteDatabase = writableDatabase
        //distinct 중복값 제거
        val sql = "SELECT distinct year, month, day FROM screenTime;"

        var cursor:Cursor = db.rawQuery(sql, null)
        while (cursor.moveToNext()){
            val year:Int = cursor.getInt(0)
            val month:Int = cursor.getInt(1)
            val day:Int = cursor.getInt(2)
            val data = ScreenTimeData(null,year,month,day,null,null,null,null)
            Log.d("chart", "year:$year , month:$month , day:$day")
            result.add(data)
//            val day:Int = cursor.getInt(0)
//            Log.d("cal", "day:$day")
        }
        db.close()
        return result
    }

    //달력 데이터 불러오기//선택한 날짜 데이터
    @SuppressLint("Recycle")
    fun calendarSelect(inputYear: Int, inputMonth: Int, inputDay: Int):ArrayList<ScreenTimeData>{
        val result = arrayListOf<ScreenTimeData>()
        val db:SQLiteDatabase = writableDatabase
        val sql = "SELECT * FROM screenTime where year=$inputYear and month=$inputMonth and day=$inputDay;"

        val cursor:Cursor = db.rawQuery(sql,null)
        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(0) //pk
            val year: Int = cursor.getInt(1)
            val month: Int = cursor.getInt(2)
            val day: Int = cursor.getInt(3)
            val time: String = cursor.getString(4) //ex 11:11:00
            val settingTime: Int = cursor.getInt(5)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장
            val success: Int = cursor.getInt(6) //디폴트가 0 = 실패, 1 = 성공
            val flower:Int =cursor.getInt(7) //디폴트가 0
            val data: ScreenTimeData = ScreenTimeData(id, year, month, day, time, settingTime, success, flower)
            result.add(data)
            //Log.d("chart", "id:${data.id} , year:${data.year} , month:${data.month} , day:${data.day} , time:${data.time} , settingTime:${data.settingTime} , success:${data.success} , flower:${data.flower}")
        }
        db.close()
        return result
    }

    //첫번쨰 행 가져오기
    fun firstRow():ArrayList<ScreenTimeData>{
        val result = arrayListOf<ScreenTimeData>()
        val db :SQLiteDatabase = writableDatabase
        val sql = "SELECT * FROM screenTime WHERE success=1 ORDER BY ROWID LIMIT 1;"

        val cursor:Cursor = db.rawQuery(sql,null)
        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(0) //pk
            val year: Int = cursor.getInt(1)
            val month: Int = cursor.getInt(2)
            val day: Int = cursor.getInt(3)
            val time: String = cursor.getString(4) //ex 11:11:00
            val settingTime: Int = cursor.getInt(5)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장
            val success: Int = cursor.getInt(6) //디폴트가 0 = 실패, 1 = 성공
            val flower:Int =cursor.getInt(7) //디폴트가 0
            val data: ScreenTimeData = ScreenTimeData(id, year, month, day, time, settingTime, success, flower)
            result.add(data)
            Log.d("첫번째 행 가져오기 // chart", "id:${data.id} , year:${data.year} , month:${data.month} , day:${data.day} , time:${data.time} , settingTime:${data.settingTime} , success:${data.success} , flower:${data.flower}")
        }
        db.close()
        return result
    }

    //마지막 행 가져오기
    fun lastRow():ArrayList<ScreenTimeData>{
        val result = arrayListOf<ScreenTimeData>()
        val db :SQLiteDatabase = writableDatabase
        val sql = "SELECT * FROM screenTime WHERE success=1 ORDER BY ROWID DESC LIMIT 1;"

        val cursor:Cursor = db.rawQuery(sql,null)
        while (cursor.moveToNext()) {
            val id: Int = cursor.getInt(0) //pk
            val year: Int = cursor.getInt(1)
            val month: Int = cursor.getInt(2)
            val day: Int = cursor.getInt(3)
            val time: String = cursor.getString(4) //ex 11:11:00
            val settingTime: Int = cursor.getInt(5)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장
            val success: Int = cursor.getInt(6) //디폴트가 0 = 실패, 1 = 성공
            val flower:Int =cursor.getInt(7) //디폴트가 0
            val data: ScreenTimeData = ScreenTimeData(id, year, month, day, time, settingTime, success, flower)
            result.add(data)
            Log.d("마지막행 가져오기 //chart", "id:${data.id} , year:${data.year} , month:${data.month} , day:${data.day} , time:${data.time} , settingTime:${data.settingTime} , success:${data.success} , flower:${data.flower}")
        }
        db.close()
        return result
    }


/**    2021-06-16
    joker
    막대 그래프 주, 월, 년 데이터 불러오기 메서드*/




    //년도 그래프 데이터 모두 불러오기 (월별로)
    fun year(y: Int): ArrayList<ScreenTimeData> {
        val result = arrayListOf<ScreenTimeData>()
        val db: SQLiteDatabase = writableDatabase

        val sql ="SELECT year, month, sum(settingTime) FROM screenTime WHERE success='1' and year=$y group by year,month;"

        val cursor = writableDatabase.rawQuery(sql, null)


        while (cursor.moveToNext()) {
//                val id: Int = cursor.getInt(0) //pk
                val year: Int = cursor.getInt(0)
                val month: Int = cursor.getInt(1)
//                val day: Int = cursor.getInt(3)
//                val time: String = cursor.getString(4) //ex 11:11:00
                val settingTime: Int =
                    cursor.getInt(2)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장
//                val success: Int = cursor.getInt(3) //디폴트가 0 = 실패, 1 = 성공
//                val flower: Int = cursor.getInt(7) //디폴트가 0

                var data: ScreenTimeData =
                    ScreenTimeData(null, year, month, null, null, settingTime, null, null)
                result?.add(data)
//                Log.d(
//                    "성공한 연도만 가지고 오기",
//                    "id:${data.id} , year:${data.year} , month:${data.month} , day:${data.day} , time:${data.time} , settingTime:${data.settingTime} , success:${data.success} , flower:${data.flower}"
//                )
            }
        db.close()
        return result
    }



    //월별 그래프 데이터 모두 불러오기 (일별로)
    fun month(y: Int, m:Int): ArrayList<ScreenTimeData> {
        val result = arrayListOf<ScreenTimeData>()
        val db: SQLiteDatabase = writableDatabase
        val sql ="SELECT day, sum(settingTime) FROM screenTime WHERE success='1' and year=$y and month=$m group by day;"
        val cursor = writableDatabase.rawQuery(sql, null)
        while (cursor.moveToNext()) {
                val day: Int = cursor.getInt(0)
            val settingTime: Int =
                cursor.getInt(1)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장

            var data: ScreenTimeData =
                ScreenTimeData(null, null, null, day, null, settingTime, null, null)
            result?.add(data)
//                Log.d(
//                    "성공한 월만 가지고 오기",
//                    "id:${data.id} , year:${data.year} , month:${data.month} , day:${data.day} , time:${data.time} , settingTime:${data.settingTime} , success:${data.success} , flower:${data.flower}"
//                )
        }
        db.close()
        return result
    }


    //주 단위 그래프 데이터 모두 불러오기
    @SuppressLint("Recycle")
    fun week(y: Int, m:Int, s:Int, e:Int): ArrayList<ScreenTimeData> { // y = year, m = month, s = start(시작날짜)), e = end(끝날짜)
        val result = arrayListOf<ScreenTimeData>()
        val db: SQLiteDatabase = writableDatabase

//        Log.d(TAG, "주데이터: y:$y m:$m s $s e $e")
        val sql ="SELECT day, sum(settingTime) FROM screenTime WHERE success='1' and year=$y and month=$m and day>=$s and day<=$e group by day;"

        val cursor = writableDatabase.rawQuery(sql, null)
//        Log.d(TAG, "week: ${cursor.count}")
        while (cursor.moveToNext()) {
            val day: Int = cursor.getInt(0)
            val settingTime: Int =
                cursor.getInt(1)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장

            var data: ScreenTimeData =
                ScreenTimeData(null, null, null, day, null, settingTime, null, null)
            result?.add(data)
//            Log.d(
//                "일주일만 가지고 오기",
//                "id:${data.id} , year:${data.year} , month:${data.month} , day:${data.day} , time:${data.time} , settingTime:${data.settingTime} , success:${data.success} , flower:${data.flower}"
//            )
        }
        db.close()
        return result
    }

    //주 단위 그래프 데이터 모두 불러오기
    @SuppressLint("Recycle")
    fun sMonthweek(y: Int, m:Int, s:Int, ld: Int): ArrayList<ScreenTimeData> { // y = year, m = month, s = start(시작날짜)), e = end(끝날짜)

        val result = arrayListOf<ScreenTimeData>()
        val db: SQLiteDatabase = writableDatabase

        Log.d(TAG, "sMonthweek: y:$y m:$m s $s e $ld")
        val sql ="SELECT day, sum(settingTime) FROM screenTime WHERE success='1' and year=$y and month=$m and day>=$s and day<=$ld group by day;"

        val cursor = writableDatabase.rawQuery(sql, null)
        while (cursor.moveToNext()) {
            val day: Int = cursor.getInt(0)
            val settingTime: Int =
                cursor.getInt(1)//초로 저장된다. ex 설정시간 1시간이면 -> 1*3600(sec)-> 3600 으로 저장

            var data: ScreenTimeData =
                ScreenTimeData(null, null, null, day, null, settingTime, null, null)
            result?.add(data)
            Log.d(
                "시작날짜에 대한 데이터",
                "id:${data.id} , year:${data.year} , month:${data.month} , day:${data.day} , time:${data.time} , settingTime:${data.settingTime} , success:${data.success} , flower:${data.flower}"
            )
        }
        db.close()
        return result
    }


    //월간 단위로 불러오기 성공한 데이터 만
    //같은 날짜면 설정시간 합친다.
    @SuppressLint("Recycle")
    fun monthSelect(year: Int, month: Int){
        val result = arrayListOf<ScreenTimeData>()
        val db: SQLiteDatabase = writableDatabase
        val sql = "SELECT  year, month, day, sum(settingTime) FROM screenTime" +
                " WHERE success=1 and year=$year and month=$month group by day;"

        val cursor: Cursor = db.rawQuery(sql, null)
        while(cursor.moveToNext()) {
            val year: Int = cursor.getInt(0)
            val month: Int = cursor.getInt(1)
            val day: Int = cursor.getInt(2)
            val settingTime: Int = cursor.getInt(3)

            Log.d(
                "chart", "year:$year , month:$month ," +
                        " day:$day, settingTime:$settingTime"
            )
        }
    }

    //년기준 월별 총 성공시간 데이터터 불러기
    fun yearSelect(year: Int){
        val result = arrayListOf<ScreenTimeData>()
        val db: SQLiteDatabase = writableDatabase
        val sql = "SELECT  year, month, sum(settingTime) FROM screenTime" +
                " WHERE success=1 and year=$year  group by month;"

        val cursor: Cursor = db.rawQuery(sql, null)
        while(cursor.moveToNext()) {
            val year: Int = cursor.getInt(0)
            val month: Int = cursor.getInt(1)
            val settingTime: Int = cursor.getInt(2)

            Log.d(
                "chart", "year:$year , month:$month , settingTime:$settingTime"
            )
        }
    }
}
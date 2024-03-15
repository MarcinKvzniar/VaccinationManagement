package com.example.vaccinationmanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.sql.DriverManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testDB: TestFreeSqlServer = TestFreeSqlServer
        val urlSB = StringBuilder("jdbc:mysql://")
        urlSB.append("sql11.freesqldatabase.com:3306/")
        urlSB.append("sql11691372") // database name
        urlSB.append("useUnicode=true&characterEncoding=utf-8")
        urlSB.append("sql11691372") // your user name
        urlSB.append("&password=NCgm9iT6hR") // generate password
        urlSB.append("&serverTimezone=CET")
        val connectionUrl = urlSB.toString()
        val conn = DriverManager.getConnection(connectionUrl)

        testDB.addNewRecord(
            conn,
            "John",
            "USA",
            "1995-10-10",
            "Rossignol",
            "Rossignol")

        testDB.retrieveData(conn, "USA")

    }


}
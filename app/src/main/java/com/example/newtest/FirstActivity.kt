package com.example.newtest

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess


class FirstActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private  val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_TOKEN = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_NoActionBar)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if(prefs.contains(APP_PREFERENCES_TOKEN)) {
            val mainPage = Intent (this, MainPage::class.java)
            startActivity(mainPage)
        }
        else{
            val logPage = Intent(this, LoginPage::class.java)
            val regPage = Intent(this, RegisterPage::class.java)
            val register: TextView = findViewById(R.id.imageButton2)
            val login: TextView = findViewById(R.id.imageButton3)
            register.setOnClickListener {
                startActivity(regPage)
            }
            login.setOnClickListener {
                startActivity(logPage)
            }

        }

    }
    override fun onBackPressed(){
        AlertDialog.Builder(this)
            .setTitle("Выход")
            .setMessage("Вы правда хотите выйти из приложения?")
            .setPositiveButton("Да") { dialog, whichButton ->
                this.finish()
                exitProcess(0)
            }
            .setNegativeButton("Отмена") { dialog, whichButton ->

            }
            .show()
    }


}


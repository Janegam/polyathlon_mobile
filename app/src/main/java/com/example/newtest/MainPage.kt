package com.example.newtest

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.system.exitProcess


class MainPage : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private  val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_TOKEN = "token"
    private val APP_PREFERENCES_COMPID = "compID"
    private val APP_PREFERENCES_COMNAME = "compName"
    private val APP_PREFERENCES_COMYEAR = "year"


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_DayNight_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        var viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main) {
                if (savedInstanceState == null) {
                    if(prefs.getString(APP_PREFERENCES_COMPID, "") == ""){
                        val editor = prefs.edit()
                        editor.putString(APP_PREFERENCES_COMPID, competition("CompetitionID")).apply()
                        editor.putString(APP_PREFERENCES_COMNAME, competition("CompetitionKindName")).apply()
                        editor.putString(APP_PREFERENCES_COMYEAR, competition("YearID")).apply()
                    }
            val fragment = FirstFragment()
                    fragment.arguments =
                        bundleOf("token" to prefs.getString(APP_PREFERENCES_TOKEN, ""), "compID" to prefs.getString(APP_PREFERENCES_COMPID, ""), "compName" to prefs.getString(APP_PREFERENCES_COMNAME, ""), "year" to prefs.getString(APP_PREFERENCES_COMYEAR, ""))
                    supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment, fragment.javaClass.simpleName)
                        .commit()
        }
            }
        }
    }
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.firstPage -> {
                var viewModelJob = Job()
                val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
                uiScope.launch {
                    withContext(Dispatchers.Main) {
                        val fragment = FirstFragment()
                        fragment.arguments =
                            bundleOf("token" to prefs.getString(APP_PREFERENCES_TOKEN, ""), "compID" to prefs.getString(APP_PREFERENCES_COMPID, ""), "compName" to prefs.getString(APP_PREFERENCES_COMNAME, ""), "year" to prefs.getString(APP_PREFERENCES_COMYEAR, ""))
                        supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment, fragment.javaClass.simpleName)
                            .commit()

                    }
                }
                return@OnNavigationItemSelectedListener true

            }
            R.id.secondPage -> {
                val fragment = SecondFragment()
                fragment.arguments = bundleOf("token" to prefs.getString(APP_PREFERENCES_TOKEN, ""))
                supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment, fragment.javaClass.simpleName)
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.thirdPage -> {
                var viewModelJob = Job()
                val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
                val fragment = ThirdFragment()
                uiScope.launch {
                    withContext(Dispatchers.Main) {
                        println(competition("CompetitionID"))
                        fragment.arguments =
                            bundleOf("token" to prefs.getString(APP_PREFERENCES_TOKEN, ""), "compID" to prefs.getString(APP_PREFERENCES_COMPID, ""), "compID" to prefs.getString(APP_PREFERENCES_COMPID, ""), "compName" to prefs.getString(APP_PREFERENCES_COMNAME, ""), "year" to prefs.getString(APP_PREFERENCES_COMYEAR, ""))
                                supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment, fragment.javaClass.simpleName)
                            .commit()

                    }
                }

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    private suspend fun competition(name: String): String {
        val urlpath = url()
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        var response = post(prefs.getString(APP_PREFERENCES_TOKEN, "").toString(), urlpath.story)
        println(response)
        val story = JSONObject(response)
        val arr = story.getJSONArray("story")
        return arr.getJSONObject(0).getString(name)
    }
    private suspend fun post(Postbody: Any, urlpost: String): String {
        return withContext(Dispatchers.IO) {
            val client = HttpClient(CIO) {
                install(JsonFeature) {
                    serializer = GsonSerializer() {
                        setPrettyPrinting()
                        disableHtmlEscaping()
                    }
                }
            }
            val urlpath = url()
            return@withContext client.post<String>(urlpath.server + urlpath.api + urlpost) {
                contentType(ContentType.Application.Json)
                body = Postbody
            }

        }.toString()
    }

    override fun onBackPressed(){
            AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы правда хотите выйти из приложения?")
                .setPositiveButton("Да") { dialog, whichButton ->
                    super.onBackPressed()
                    finish()
                    exitProcess(0)
                }
                .setNegativeButton("Отмена") { dialog, whichButton ->

                }
                .show()
    }

}
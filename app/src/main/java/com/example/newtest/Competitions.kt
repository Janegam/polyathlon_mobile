package com.example.newtest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.championship_fragment.*
import kotlinx.coroutines.*
import org.json.JSONObject

class Competitions : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private  val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_COMPID = "compID"
    private val APP_PREFERENCES_COMNAME = "compName"
    private val APP_PREFERENCES_COMYEAR = "year"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competitions)
        main()
    }
    private fun <T : Any?> redirect(activity: Class<T>){
        val newPage = Intent(this, activity)
        startActivity(newPage)
    }
    private fun add(arr: JSONObject, i: Int){
        val row = TableRow(this)
        val textView = TextView(this)
        val params =
            TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT
            )
        textView.text = (i + 1).toString() + ". " + arr.getString("YearID") + ": " + arr.getString("CompetitionName")
        row.setBackgroundResource(R.drawable.table_button_selector)
        params.setMargins(25, 10, 0, 0)
        val myCustomFont: Typeface? = ResourcesCompat.getFont(this, R.font.roboto_medium)
        textView.textSize = 18f
        textView.typeface = myCustomFont
        textView.setTextColor(resources.getColor(R.color.colorCommon))
        textView.layoutParams = params
        row.addView(textView)
        PlaceTable.addView(row, i)
        row.setOnClickListener {
            prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(APP_PREFERENCES_COMPID, arr.getString("CompetitionID")).apply()
            editor.putString(APP_PREFERENCES_COMNAME, arr.getString("CompetitionKindName")).apply()
            editor.putString(APP_PREFERENCES_COMYEAR, arr.getString("YearID")).apply()
            redirect(MainPage::class.java)
        }

    }

    private fun main(){
        var viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main){
                val urlpath = url()
                val story = JSONObject(get(urlpath.competitions))
                println(story)
                val arr = story.getJSONArray("arr")
                textFirst.text = "1. " + arr.getJSONObject(0).getString("YearID") + ": " + arr.getJSONObject(0).getString("CompetitionName")
                first.setOnClickListener {
                    prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.putString(APP_PREFERENCES_COMPID, arr.getJSONObject(0).getString("CompetitionID")).apply()
                    editor.putString(APP_PREFERENCES_COMNAME, arr.getJSONObject(0).getString("CompetitionKindName")).apply()
                    editor.putString(APP_PREFERENCES_COMYEAR, arr.getJSONObject(0).getString("YearID")).apply()
                    redirect(MainPage::class.java)
                }
                for (i in 1 until arr.length()) {
                    add(arr.getJSONObject(i), i)
                }
            }
        }
    }
    private suspend fun  get (urlpost: String): String{
        val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = GsonSerializer() {
                    setPrettyPrinting()
                    disableHtmlEscaping()
                }
            }
        }
        val urlpath = url()
        return client.post<String>(urlpath.server + urlpath.api + urlpost) {
            contentType(ContentType.Application.Json)
        }
    }
}
package com.example.newtest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.RadioButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.register.*
import org.json.JSONObject
import java.util.*
import kotlinx.android.synthetic.main.championship_fragment.*
import java.math.BigInteger
import java.security.MessageDigest


class RegisterPage : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private val APP_PREFERENCES_TOKEN = "token"
    private val APP_PREFERENCES = "mysettings"
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        back.setOnClickListener {
            super.onBackPressed()
        }
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        main()
        singin.setOnClickListener {
            register()
        }
        val calendarView = CalendarView(this)
        date_text.setOnClickListener {
            info.removeViewInLayout(calendarView)
            info.addView(calendarView)
        }
        val calendar = Calendar.getInstance()
        calendar.set(
            2000,
            0,
            1
        )

        calendarView.date = calendar.timeInMillis
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat("dd.MM.yyyy")
            val selectedDates: String = sdf.format(Date(year - 1900, month, dayOfMonth))
            date_text.text = selectedDates
            info.removeViewInLayout(calendarView)
        }
    }
    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    private fun register() {
        var userInfo = Profile("","","","","","","","","","","","","","","","","","")
        val urlPath = url()
        println(lastpass3.text.toString().md5())
        if(nikname3.text.toString().isEmpty()){

            nikname3.setHintTextColor(resources.getColor(R.color.colorAccent))
            nikname3.hint = "Логин обязателен для заполнения!"
        } else userInfo.nikname = nikname3.text.toString()
        if (lastpass3.text.toString().isEmpty()){
            lastpass3.setHintTextColor(resources.getColor(R.color.colorAccent))
            lastpass3.hint = "Пароль обязателен для заполнения!"
        } else {
            if (lastpass3.text.toString() != newpass3.text.toString()) {
                lastpass3.setHintTextColor(resources.getColor(R.color.colorAccent))
                newpass3.setHintTextColor(resources.getColor(R.color.colorAccent))
                lastpass3.hint = "Пароли не совпадают!"
                newpass3.hint = "Пароли не совпадают!"
                lastpass3.text.clear()
                newpass3.text.clear()
            } else {
                println(lastpass3.text.toString().md5())
                userInfo.password = lastpass3.text.toString().md5()
            }
        }
        when(val checkedRadioButtonId = sexgroup.checkedRadioButtonId){
                -1 -> {
                    sex.setTextColor(resources.getColor(R.color.colorAccent))
                    sex.text = "Выберите пол!"
                }
                else -> {
                    val selectedRadioButton = findViewById<RadioButton>(checkedRadioButtonId)
                    sex.text = ""
                    if(selectedRadioButton.text == "Ж") {
                        userInfo.sexID = "1"
                    } else userInfo.sexID = "2"
                }
        }
        if (last_name3.text.toString().isEmpty()) {
            last_name3.setHintTextColor(resources.getColor(R.color.colorAccent))
            last_name3.hint = "Фамилия обязательна!"
        }
        if (first_name3.text.toString().isEmpty()) {
            first_name3.setHintTextColor(resources.getColor(R.color.colorAccent))
            first_name3.hint = "Имя обязательно для заполнения!"
        } else if (last_name3.text.toString().isEmpty()) {
            last_name3.setHintTextColor(resources.getColor(R.color.colorAccent))
            last_name3.hint = "Фамилия обязательна!"
        } else userInfo.fio = first_name3.text.toString() + " " + last_name3.text.toString()
        if (date_text.text.isEmpty()) {
            date_text.setHintTextColor(resources.getColor(R.color.colorAccent))
            date_text.hint = "Выберите дату рождения!"
        } else userInfo.birthday = date_text.text.toString()
        userInfo.clubID = clubs3.selectedItemId.toString()
        userInfo.cityID = regions3.selectedItemId.toString()+1
        userInfo.categoryID = category3.selectedItemId.toString()
        var viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val res = post(userInfo, urlPath.register)
                println(res)
                withContext(Dispatchers.Main){
                if (res == "0") {
                    nikname3.setHintTextColor(resources.getColor(R.color.colorAccent))
                    nikname3.hint = "Этот логин уже занят!"
                    nikname3.text.clear()
                } else if (res == "200") {
                    /*val user = user(userInfo.nikname, userInfo.password)
                        val result = JSONObject(post(user, urlPath.login))
                        if (result.getString("token").isNotEmpty()) {
                            println(result)
                            token = result.getString("token")
                            val editor = prefs.edit()
                            editor.putString(APP_PREFERENCES_TOKEN, token).apply()
                            redirect(MainPage::class.java)
                    }*/
                }
            }
            }
        }

    }

    private fun feel (response: JSONObject, spinner: Spinner, firstItem: String){
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        val arr = response.getJSONArray("arr")
        if (firstItem.isNotEmpty()) {
            adapter.add(firstItem)
        }
        for (i in 0 until arr.length()) {
            adapter.add(arr.getJSONObject(i).getString("name"))
        }
        adapter.setDropDownViewResource(R.layout.spinner_drop_item)
        spinner.adapter = adapter
    }
    private fun <T : Any?> redirect(activity: Class<T>){
        val newPage = Intent(this, activity)
        startActivity(newPage)
    }
    fun main() {

        var viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            val urlpath = url()
            val clubresp = JSONObject(get(urlpath.clubs))
            val regionresp = JSONObject(get(urlpath.regions))
            val categoryresp = JSONObject(get(urlpath.category))
            feel (clubresp, clubs3, "Нет клуба")
            feel (regionresp, regions3, "")
            feel (categoryresp, category3, "Нет разряда")
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
}





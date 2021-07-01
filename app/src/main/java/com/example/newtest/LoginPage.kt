package com.example.newtest

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.*
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.system.exitProcess


class LoginPage : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private val APP_PREFERENCES_TOKEN = "token"
    private val APP_PREFERENCES = "mysettings"
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        main()
    }
    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

    private fun redirect(token: String){
        val profilePage = Intent(this, MainPage::class.java)
        profilePage.putExtra("token", token)
        startActivity(profilePage)
    }

    fun main(){
            var login: String
            var password: String
                imageButton2.setOnClickListener {
                    if (imageButton3.text.isNotEmpty() && imageButton4.text.isNotEmpty()) {
                        textView3.text = ""
                        login = imageButton3.text.toString()
                        password = imageButton4.text.toString()
                        password = password.md5()
                        println(password)
                        var viewModelJob = Job()
                        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
                        uiScope.launch {
                            withContext(Dispatchers.IO) {
                                val res = post(login, password)
                                println(res)
                                if (res != "0") {
                                 val result = JSONObject(res)
                                println(password)
                                if (result.getString("token").isNotEmpty()) {
                                    println(result)
                                    token = result.getString("token")
                                    val editor = prefs.edit()
                                    editor.putString(APP_PREFERENCES_TOKEN, token).apply()
                                    redirect(token)
                                } else{
                                    withContext(Dispatchers.Main) {
                                        println(post(login, password))
                                        textView3.text = "Ошибка сервера."
                                    }
                                }
                                }else {
                                    withContext(Dispatchers.Main) {
                                        println(post(login, password))
                                        textView3.text = "Неверный логин или пароль!"
                                    }
                                }
                            }

                        }
                    } else {
                            textView3.text = "Введите логин и пароль!"
                    }

                }
            }
    private suspend fun post(login: String, password: String):String{
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


            return@withContext client.post<String>(urlpath.server + urlpath.api + urlpath.login) {
                contentType(ContentType.Application.Json)
                body = user(login, password)
            }
            client.close()
        }.toString()
        }

    }



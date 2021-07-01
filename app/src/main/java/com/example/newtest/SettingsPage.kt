package com.example.newtest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.profile_edit.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.coroutines.*
import org.json.JSONObject
import android.widget.Spinner
import android.widget.Toast
import java.math.BigInteger
import java.security.MessageDigest


class SettingsPage : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private  val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_TOKEN = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_NoActionBar)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.profile_edit)
        prefs = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        val token = prefs.getString(APP_PREFERENCES_TOKEN, "").toString()
        exit.setOnClickListener {
            exit(token)
        }
        newavatar.setOnClickListener{
            changePhotoUser()
        }
        close.setOnClickListener{
            super.onBackPressed()
        }
        main(token)
    }

    private fun main(token: String) {

        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main) {
                val urlpath = url()
                val info = post(token,urlpath.profile)
                println(info)
                val gson = Gson()
                val userInfo = gson.fromJson(info, Profile::class.java)
                if (userInfo.avatar.isNotEmpty() && userInfo.avatar != "0") {
                    download(userInfo.avatar)
                }
                val list = userInfo.fio.split(" ")
                first_name.setText(list[0])
                last_name.setText(list[1])
                println(get(urlpath.clubs))
                val clubresp = JSONObject(get(urlpath.clubs))
                val regionrersp = JSONObject(get(urlpath.regions))
                feel (clubresp, clubs)
                feel (regionrersp, regions)
                clubs.setSelection(userInfo.clubID.toInt() - 1)
                regions.setSelection(userInfo.cityID.toInt() - 1)
                date.setText(userInfo.birthday)
                nikname.setText(userInfo.nikname)
                save.setOnClickListener {
                    save(token, userInfo)
                }

            }
        }
    }
    private fun feel (response: JSONObject, spinner: Spinner){
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        val arr = response.getJSONArray("arr")
        for (i in 0 until arr.length()) {
            adapter.add(arr.getJSONObject(i).getString("ID") + ". "+ arr.getJSONObject(i).getString("name"))
        }

        adapter.setDropDownViewResource(R.layout.spinner_drop_item)
        spinner.adapter = adapter
    }

    private fun download(avatarPath: String) {
        val urlpath = url()
        Picasso.with(this).load(urlpath.server + urlpath.avatar + avatarPath).into(newavatar)
    }

    private fun changePhotoUser() {
       CropImage.activity()
           .setAspectRatio(1,1)
           .setRequestedSize(600, 600)
           .setCropShape(CropImageView.CropShape.OVAL)
           .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            val uri = CropImage.getActivityResult(data).uri
            val viewModelJob = Job()
            val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
            uiScope.launch {
                withContext(Dispatchers.Main) {
                    upload(uri)
                }
            }

        }
    }

    private suspend fun upload(uri: Uri):String {
        return withContext(Dispatchers.IO) {
            val client = HttpClient(CIO) {

            }
            val urlpath = url()
            return@withContext client.post<String>(urlpath.server + urlpath.avatar) {
                contentType(ContentType.Image.Any)
                body = uri
            }

        }.toString()
    }

    private fun <T : Any?> redirect(activity: Class<T>){
        val newPage = Intent(this, activity)
        startActivity(newPage)
    }
    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }
    private fun save (token: String, userInfo: Profile){
        val urlpath = url()
        println(userInfo.password)
        println(lastpass.text.toString().md5())
        if (newpass.text.toString().isNotEmpty()) {
            if (userInfo.password == lastpass.text.toString().md5()) {
                userInfo.password = newpass.text.toString().md5()
            } else {
                Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show()
            }
        }
            userInfo.fio = first_name.text.toString() + " " + last_name.text.toString()
            var list = clubs.selectedItem.toString().split(". ")
            userInfo.clubID = list[0]
            userInfo.club = list[1]
            val list2 = regions.selectedItem.toString().split(". ")
            userInfo.city = list2[1]
            userInfo.cityID = list2[0]
            userInfo.birthday = date.text.toString()
            userInfo.nikname = nikname.text.toString()
            println(userInfo.password)
        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main) {
                println(post(userInfo, urlpath.edit))
                redirect(MainPage::class.java)

            }

        }
    }
    private fun exit (token: String) {
        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main) {
                if(token.isNotEmpty()) {
                        val urlPath = url()
                        val response = post(prefs.getString(APP_PREFERENCES_TOKEN, "").toString(), urlPath.delsession)
                        println(response)
                        val editor = prefs.edit()
                        editor.remove(APP_PREFERENCES_TOKEN).apply()
                        redirect(FirstActivity::class.java)
                        finish()
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
package com.example.newtest



import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.coroutines.*
import com.squareup.picasso.Picasso
import org.json.JSONObject


class SecondFragment : Fragment() {

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            token = arguments?.getString(TOKEN)
            println(token)
            if(token.toString().isNotEmpty()){
                main(token)
            }


        }

    }


    fun main (token: String?) {
        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main) {
                settings.setOnClickListener {
                    val settings = Intent (context, SettingsPage::class.java)
                    startActivity(settings)
                }
            val urlpath = url()
            val info = post(token.toString(), urlpath.profile)
        println(info)
        val gson = Gson()
        val userInfo = gson.fromJson(info, Profile::class.java)
        FIO.text = userInfo.fio
        if (userInfo.club.isNotEmpty() && userInfo.club != "0") {
            club.text = userInfo.city + "\n" + userInfo.club
        } else club.text = userInfo.city + "\nНет клуба"
        Competitions.text = userInfo.countCompetitions
        Wins.text = userInfo.countWins
        if (userInfo.category.isNotEmpty() && userInfo.category != "0") {
            Category.text = userInfo.category
        } else Category.text = "-"
        AgeGroup.text = userInfo.ageGroup
        if (userInfo.avatar.isNotEmpty() && userInfo.avatar != "0") {
            download(userInfo.avatar)
        }
        val response = post(token.toString(), urlpath.story)
        val story = JSONObject(response)
        val arr = story.getJSONArray("story")
        text0.text = arr.getJSONObject(0).getString("CompetitionName")
        for (i in 1 until arr.length()) {
            val row = TableRow(context)
            val textView = TextView(context)
            textView.text = arr.getJSONObject(i).getString("CompetitionName")
            row.setBackgroundResource(R.drawable.ic_table_row)
            val params = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
            params.setMargins(25, 10, 0, 0)
            textView.textSize = 18f
            val myCustomFont: Typeface? = ResourcesCompat.getFont(context!!, R.font.roboto_medium)
            textView.typeface = myCustomFont
            textView.setTextColor(resources.getColor(R.color.colorCommon))
            textView.layoutParams = params
            row.addView(textView)
            StoryTable.addView(row, i)
        }
            }

        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.profile_fragment, container, false)
    }


    companion object {
        private const val TOKEN = "token"

        @JvmStatic
        fun newInstance(token: String?) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(TOKEN, token)
                }
            }

    }

    private fun download(avatarPath: String) {
        val urlpath = url()
        Picasso.with(context).load(urlpath.server + urlpath.avatar + avatarPath).into(avatar)
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


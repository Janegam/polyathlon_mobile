package com.example.newtest

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.championship_fragment.*
import kotlinx.android.synthetic.main.competitions_fragment.*
import kotlinx.coroutines.*
import org.json.JSONObject


class FirstFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private  val APP_PREFERENCES = "mysettings"
    private val APP_PREFERENCES_COMPID = "compID"
    private val APP_PREFERENCES_COMNAME = "compName"
    private val APP_PREFERENCES_COMYEAR = "year"

    private var token: String? = null
    private var compID: String? = null
    private var compName: String? = null
    private var year: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            token = arguments?.getString(TOKEN)
            println(token)
            compID = arguments?.getString(COMP)
            compName = arguments?.getString(COMPETITION)
            year = arguments?.getString(YEAR)
            println(compName)
           main(token, compID, compName, year)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.competitions_fragment, container, false)
    }

    companion object {
        private const val TOKEN = "token"
        private const val COMP = "compID"
        private const val COMPETITION = "compName"
        private const val YEAR = "year"

        @JvmStatic
        fun newInstance(token: String, compID: String, compName: String, year: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(TOKEN, token)
                    putString(COMP, compID)
                    putString(COMPETITION, compName)
                    putString(YEAR, year)
                }
            }
    }

    private fun <T : Any?> redirect(activity: Class<T>, json: String){
        val newPage = Intent(context, activity)
        println(json)
        newPage.putExtra("json", json)
        startActivity(newPage)
    }
    fun main(token: String?, compID: String?, compName: String?, year: String?) {
        var viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main) {

                compKind.text = compName.toString()
                yearView.text = year.toString()
                compKind.setOnClickListener {
                    redirect(Competitions::class.java, "")
                }
                val urlpath = url()
                var postbody = comp(token.toString(), compID.toString())
                println(postbody)
                val response = post(postbody, urlpath.exercises)
                println(response)
                val story = JSONObject(response)
                val arr = story.getJSONArray("story")
                runner.setImageResource(R.drawable.runner)
                ex.setImageResource(R.drawable.ex)
                exes.setImageResource(R.drawable.exes)
                shooter.setImageResource(R.drawable.shoot)
                swim.setImageResource(R.drawable.swim)
                ski.setImageResource(R.drawable.skies)
                thrower.setImageResource(R.drawable.thrower)
                if (arr.length()!=0){
                    if (arr.getJSONObject(0).getString("SexID") == "1") {
                        ex.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_29)
                        exes.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_28)
                    } else {
                        exes.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_29)
                        ex.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_28)
                    }
                for (i in 0 until arr.length()) {
                    when (arr.getJSONObject(i).getString("ExerciseKindName")){
                        "Бег" ->{
                            runner.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_29)
                            runner.setOnClickListener {
                                redirect(ExercisePage::class.java, arr.getJSONObject(i).toString())
                            }
                        }
                        "Стрельба" ->{
                            shooter.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_29)
                            shooter.setOnClickListener {
                                redirect(ExercisePage::class.java, arr.getJSONObject(i).toString())
                            }
                        }
                        "Плавание" ->{
                            swim.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_29)
                            swim.setOnClickListener {
                                redirect(ExercisePage::class.java, arr.getJSONObject(i).toString())
                            }
                        }
                        "Лыжные гонки" ->{
                            ski.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_29)
                            ski.setOnClickListener {
                                redirect(ExercisePage::class.java, arr.getJSONObject(i).toString())
                            }
                        }
                        "Метание" ->{
                            thrower.foreground = ContextCompat.getDrawable(context!!, R.drawable.ic_ellipse_29)
                            thrower.setOnClickListener {
                                redirect(ExercisePage::class.java, arr.getJSONObject(i).toString())
                            }
                        }
                        "Силовая гимнастика" ->{
                            if (arr.getJSONObject(0).getString("SexID") == "1"){
                                ex.setOnClickListener {
                                    redirect(ExercisePage::class.java, arr.getJSONObject(i).toString())
                                }
                            } else {
                                exes.setOnClickListener {
                                    redirect(ExercisePage::class.java, arr.getJSONObject(i).toString())
                                }
                            }


                        }
                    }

                    }
                }
            }
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


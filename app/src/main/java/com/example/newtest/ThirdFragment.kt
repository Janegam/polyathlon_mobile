package com.example.newtest

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.android.synthetic.main.championship_fragment.*
import kotlinx.android.synthetic.main.competitions_fragment.*
import kotlinx.coroutines.*
import org.json.JSONObject



class ThirdFragment : Fragment() {

    private var token: String? = null
    private var compID: String? = null
    private var compName: String? = null
    private var year: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            token = arguments?.getString(TOKEN)
            compID = arguments?.getString(COMP)
            compName = arguments?.getString(COMPETITION)
            year = arguments?.getString(YEAR)
            println(compID)
            println(token)
            main(token, compID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.championship_fragment, container, false)
    }

    companion object {
        private const val TOKEN = "token"
        private const val COMP = "compID"
        private const val COMPETITION = "compName"
        private const val YEAR = "year"


        @JvmStatic
        fun newInstance(token: String, compID: String, compName: String, year: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(TOKEN, token)
                    putString(COMP, compID)
                    putString(COMPETITION, compName)
                    putString(YEAR, year)
                }
            }
    }
    fun main(token: String?, compID: String?) {
        var viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main) {
                kind.text = compName.toString()
                yearView2.text = year.toString()
                val urlpath = url()
                var postbody = comp(token.toString(), compID.toString())
                println(postbody)
                val absolute = post(postbody, urlpath.absolute)
                val agegroup = post(postbody, urlpath.agegroup)
                val club = post(postbody, urlpath.club)
                val region = post(postbody, urlpath.region)

                    println(absolute)
                    table(absolute)
                    radiogroup.setOnCheckedChangeListener { _, checkedId ->
                        when (checkedId) {
                            R.id.radioButton -> {
                                championship.text = "Абсолютное первенство"
                                table(absolute)
                            }
                            R.id.radioButton2 -> {
                                championship.text = "Региональное первенство"
                                table(region)
                            }
                            R.id.radioButton3 -> {
                                championship.text = "Клубное первенство"
                                table(club)
                            }
                            R.id.radioButton4 -> {
                                championship.text = "Возрастная группа"
                                table(agegroup)
                            }
                        }


                }
            }
        }
    }
    fun table (json: String){
        var viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        uiScope.launch {
            withContext(Dispatchers.Main) {
                val story = JSONObject(json)
                val arr = story.getJSONArray("story")
                if(arr.length()!=0) {
                    PlaceTable.removeAllViews()
                    val firstRow = TableRow(context)
                    val bottomRow = TableRow(context)
                    val textFirst = TextView(context)
                    firstRow.setBackgroundResource(R.drawable.ic_table_up)
                    bottomRow.setBackgroundResource(R.drawable.ic_table_bottom)
                    val params =
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
                    textFirst.textSize = 18f
                    val myCustomFont: Typeface? = ResourcesCompat.getFont(context!!, R.font.roboto_medium)
                    textFirst.typeface = myCustomFont
                    textFirst.setTextColor(resources.getColor(R.color.colorCommon))
                    textFirst.layoutParams = params
                    textFirst.text = "1. " + arr.getJSONObject(0).getString("name") + " (баллы: " + arr.getJSONObject(0)
                        .getString("balls") + ")"
                    firstRow.addView(textFirst)
                    PlaceTable.addView(firstRow, params)
                    PlaceTable.addView(bottomRow, params)
                    for (i in 1 until arr.length()) {
                        val row = TableRow(context)
                        val textView = TextView(context)
                        textView.text = (i + 1).toString() + ". " + arr.getJSONObject(i)
                            .getString("name") + " (баллы: " + arr.getJSONObject(i).getString("balls") + ")"
                        row.setBackgroundResource(R.drawable.ic_table_row)
                        params.setMargins(25, 10, 0, 0)
                        textView.textSize = 18f
                        textView.typeface = myCustomFont
                        textView.setTextColor(resources.getColor(R.color.colorCommon))
                        textView.layoutParams = params
                        row.addView(textView)
                        PlaceTable.addView(row, i)
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


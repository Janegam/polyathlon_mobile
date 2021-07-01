package com.example.newtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.ex_page.*
import org.json.JSONObject

class ExercisePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_AppCompat_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ex_page)
        val arguments = intent.extras
        val json = arguments?.getString("json")
        back.setOnClickListener {
            val mainPage = Intent(this, MainPage::class.java)
            startActivity(mainPage)
        }
        if (json!!.isNotEmpty()){
            val arr = JSONObject(json)
            exesView.text = arr.getString("ExerciseName")
            Category.text = arr.getString("Result")
            Wins.text = arr.getString("Balls")
            AgeGroup.text = arr.getString("AgeGroupName")
            println(arr)
            when (arr.getString("ExerciseKindName")){
                "Бег" ->{
                    Competitions.text = arr.getString("ExerciseKindName")
                    exImgView.setImageResource(R.drawable.runner)
                }
                "Стрельба" ->{
                    Competitions.text = arr.getString("ExerciseKindName")
                    exImgView.setImageResource(R.drawable.shoot)
                }
                "Плавание" ->{
                    Competitions.text = arr.getString("ExerciseKindName")
                    exImgView.setImageResource(R.drawable.swim)
                }
                "Лыжные гонки" ->{
                    Competitions.text = arr.getString("ExerciseKindName")
                    exImgView.setImageResource(R.drawable.skies)
                }
                "Метание" ->{
                    Competitions.text = arr.getString("ExerciseKindName")
                    exImgView.setImageResource(R.drawable.thrower)
                }
                "Силовая гимнастика" ->{
                    Competitions.text = "Сил. гимнастика"
                    if(arr.getString("SexID")=="1"){
                        exImgView.setImageResource(R.drawable.ex)
                    } else exImgView.setImageResource(R.drawable.exes)
                }
            }
        }
    }
}
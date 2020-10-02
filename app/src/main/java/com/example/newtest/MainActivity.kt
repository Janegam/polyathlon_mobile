package com.example.newtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.ContentView
import androidx.constraintlayout.widget.Constraints
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.card.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card)
    }

}

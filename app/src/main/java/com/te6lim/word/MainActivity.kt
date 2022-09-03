package com.te6lim.word

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = findViewById<MaterialToolbar>(R.id.toolbar)

        setSupportActionBar(actionBar)
        supportActionBar?.title = null
    }
}
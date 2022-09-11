package com.te6lim.word

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class WordApplication : Application() {

    var darkThemeActive = false
        set(value) {
            field = value
            if (value) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
}
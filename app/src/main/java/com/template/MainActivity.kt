package com.template

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var prefSettings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefSettings = applicationContext?.getSharedPreferences(
            WebActivity.APP_SETTINGS, Context.MODE_PRIVATE
        ) as SharedPreferences
        editor = prefSettings.edit()

        putIntPref(2)
    }

    private fun putIntPref(numActivity: Int) {
        editor.putInt(LoadingActivity.KEY_INT, numActivity)
        editor.commit()
    }
}
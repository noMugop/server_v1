package com.template

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.browser.customtabs.CustomTabsIntent
import com.template.databinding.ActivityWebBinding
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.system.exitProcess

class WebActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityWebBinding.inflate(layoutInflater)
    }

    private lateinit var prefSettings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        prefSettings = applicationContext?.getSharedPreferences(
            APP_SETTINGS, Context.MODE_PRIVATE
        ) as SharedPreferences
        editor = prefSettings.edit()

        binding.btnStart.setOnClickListener {
            try {
                sharedUrl = prefSettings.getString(LoadingActivity.KEY_URL, null) as String
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Пустая ссылка", Toast.LENGTH_SHORT).show()
            }
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(this, Uri.parse(sharedUrl))
        }
        putIntPref(1)
        LoadingActivity.sharedActivity = prefSettings.getInt(LoadingActivity.KEY_INT, 0)
        println("DONE_DONE ${LoadingActivity.sharedActivity}")
    }

    private fun putIntPref(numActivity: Int) {
        editor.putInt(LoadingActivity.KEY_INT, numActivity)
        editor.commit()
    }

    companion object {

        const val APP_SETTINGS = "Settings"
        private var sharedUrl: String = ""
    }

    override fun onDestroy() {
        moveTaskToBack(true)
        finish()
        super.onDestroy()

        exitProcess(0);
    }
}
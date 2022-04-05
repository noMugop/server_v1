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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        prefSettings = applicationContext?.getSharedPreferences(
            APP_SETTINGS, Context.MODE_PRIVATE
        ) as SharedPreferences

        binding.btnStart.setOnClickListener {
            try {
                sharedUrl = prefSettings.getString(LoadingActivity.KEY_URL, null) as String
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Пустая ссылка", Toast.LENGTH_SHORT).show()
            }
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(this, Uri.parse(sharedUrl))
        }
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
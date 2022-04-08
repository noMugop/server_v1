package com.template

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.PrecomputedText
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.template.databinding.ActivityLoadingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess


class LoadingActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    lateinit var intentMainActivity: Intent
    lateinit var binding: ActivityLoadingBinding
    lateinit var customTabsIntent: CustomTabsIntent
    lateinit var apiService: ApiService

    private lateinit var prefSettings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var dbDatabase: FirebaseDatabase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        sharedActivity = prefSettings.getInt(KEY_INT, 0)

        when (sharedActivity) {
            1 -> {
                sharedUrl = prefSettings.getString(KEY_URL, null) as String
                customTabsIntent.launchUrl(applicationContext, Uri.parse(URL))
            }
            2 -> {
                startActivity(intentMainActivity)
                binding.progressBar.visibility = View.GONE
            }
            else -> {
                generateRequest()
            }
        }
    }

    private fun init() {

        binding = ActivityLoadingBinding.inflate(layoutInflater)
        intentMainActivity = Intent(this, MainActivity::class.java)
        customTabsIntent = CustomTabsIntent
            .Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        prefSettings = applicationContext?.getSharedPreferences(
            APP_SETTINGS, Context.MODE_PRIVATE
        ) as SharedPreferences
        editor = prefSettings.edit()

        dbDatabase = FirebaseDatabase.getInstance()
        firebaseAnalytics = Firebase.analytics
        FirebaseMessaging.getInstance()
    }

    private fun generateRequest() {
        dbDatabase.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val value: DataSnapshot = snapshot
                val data: HashMap<String, HashMap<String, String>> =
                    value.value as HashMap<String, HashMap<String, String>>
                link = data["db"]?.get("link") as String
                println("RESULT_LINK $link")
                if (!link.isNullOrEmpty()) {
                    val uniqueID = UUID.randomUUID().toString()
                    val tz = TimeZone.getDefault()

                    launch {
                        apiService = ApiFactory.getInstance(link)
                        URL = apiService.getURL(uuid = uniqueID, timezone = tz.id)
                        putStringIntoPref(URL)

                        customTabsIntent.launchUrl(applicationContext, Uri.parse(URL))
                    }

                    putIntPref(1)
                    binding.progressBar.visibility = View.GONE
                } else {
                    startActivity(intentMainActivity)
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun putIntPref(numActivity: Int) {
        editor.putInt(KEY_INT, numActivity)
        editor.commit()
    }

    private fun putStringIntoPref(url: String) {
        editor.putString(KEY_URL, url)
        editor.commit()
    }

    companion object {

        private var link = ""
        private var URL = ""
        var KEY_URL = "Url"
        var KEY_INT = "Activity"
        var sharedActivity = 0
        var sharedUrl: String = ""
        const val APP_SETTINGS = "Settings"
    }

    override fun onDestroy() {
        moveTaskToBack(true)
        finish()
        super.onDestroy()

        exitProcess(0);
    }
}

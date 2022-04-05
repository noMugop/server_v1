package com.template

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.template.WebActivity.Companion.sharedUrl
import com.template.databinding.ActivityLoadingBinding
import com.template.databinding.ActivityWebBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.collections.isNullOrEmpty as isNullOrEmpty1


class LoadingActivity : AppCompatActivity() {

    lateinit var intentMainActivity: Intent

    lateinit var intentWebActivity: Intent

    lateinit var binding: ActivityLoadingBinding

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
                startActivity(intentWebActivity)
                binding.progressBar.visibility = View.GONE
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
        intentWebActivity = Intent(this, WebActivity::class.java)
        intentMainActivity = Intent(this, MainActivity::class.java)

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
                val link = data["db"]?.get("link")
                if (!link.isNullOrEmpty()) {
                    val uniqueID = UUID.randomUUID().toString()
                    val tz = TimeZone.getDefault()
                    val urlValue =
                        URLValue(
                            url = "$link"
                                    + "/?packageid=com.template&usserid="
                                    + uniqueID
                                    + "&getz="
                                    + tz.id
                                    + "&getr=utm_source=google-play&utm_medium=organic"
                        )
                    URL = urlValue.url
                    putStringIntoPref(URL)
                    startActivity(intentWebActivity)
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

    private fun putStringIntoPref(url: String) {
        editor.putString(KEY_URL, url)
        editor.commit()
    }

    companion object {

        private var URL = ""
        var KEY_URL = "Url"
        var KEY_INT = "Activity"
        var sharedActivity = 0
        const val APP_SETTINGS = "Settings"
    }
}

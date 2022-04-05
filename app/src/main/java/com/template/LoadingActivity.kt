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
import com.template.databinding.ActivityLoadingBinding
import com.template.databinding.ActivityWebBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.coroutines.CoroutineContext


class LoadingActivity : AppCompatActivity() {

    private val intentMainActivity by lazy {
        Intent(this, MainActivity::class.java)
    }

    private val intentWebActivity by lazy {
        Intent(this, WebActivity::class.java)
    }

    private val binding by lazy {
        ActivityLoadingBinding.inflate(layoutInflater)
    }

    private lateinit var prefSettings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var dbDatabase: FirebaseDatabase
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        init()

        generateRequest()
    }

    private fun init() {

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
            }

            override fun onCancelled(error: DatabaseError) {
                startActivity(intentMainActivity)
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
        private var sharedUrl = ""
        const val APP_SETTINGS = "Settings"

    }
}

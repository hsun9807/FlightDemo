package com.example.flightdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.airbnb.lottie.LottieAnimationView
import com.example.flightdemo.fragment.ExchangeFragment
import com.example.flightdemo.fragment.FlightFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var loadingView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "航班資訊"
        loadingView = findViewById<LottieAnimationView>(R.id.loadingView)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // 預設載入首頁 Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FlightFragment())
            .commit()

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            val transaction = supportFragmentManager.beginTransaction()

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (currentFragment !is FlightFragment) {
                        transaction.setCustomAnimations(
                            R.anim.slide_in_left,  // 新進來的
                            R.anim.slide_out_right // 原本的出去
                        )
                        transaction.replace(R.id.fragment_container, FlightFragment())
                            .commit()
                        toolbar.title = "航班資訊"
                    }
                    true
                }
                R.id.nav_exchange -> {
                    if (currentFragment !is ExchangeFragment) {
                        transaction.setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        transaction.replace(R.id.fragment_container, ExchangeFragment())
                            .commit()
                        toolbar.title = "匯率"
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showLoading() {
        loadingView.visibility = VISIBLE
    }

    fun hideLoading() {
        loadingView.visibility = GONE
    }
}


package com.getsense

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.getsense.fragment.BehaviourFragment


class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var behaviourFragment: BehaviourFragment
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        behaviourFragment = BehaviourFragment()

        bottomNavigationView = findViewById(R.id.navigation)
        loadFragment(behaviourFragment)

        bottomNavigationView.setOnItemSelectedListener {

            try {
                when (it.itemId) {
                    R.id.home -> {
                        loadFragment(behaviourFragment)
                        true
                    }

                    R.id.docs -> {
                        openWebsite("http://pro.getsense.co/")
                        true
                    }

                    R.id.policy -> {
                        openWebsite("http://pro.getsense.co/")
                        true
                    }

                    else -> {
                        R.id.support
                        openWebsite("http://pro.getsense.co/")
                        true
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
           // loadFragment(homeFragment)
        } else{
           // loadFragment(homeFragment)
        }
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
    }
    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }

}

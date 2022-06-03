package com.osman.studentqr.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.osman.studentqr.R
import com.osman.studentqr.presentation.fragment.authentication.login.LoginFragment
import com.osman.studentqr.presentation.fragment.authentication.register.RegisterFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val btnLogin = findViewById<TextView>(R.id.btnLogin)
        val btnSignup = findViewById<TextView>(R.id.btnSignup)

        btnLogin.setOnClickListener(this@AuthenticationActivity)
        btnSignup.setOnClickListener(this@AuthenticationActivity)

        // default fragment
        btnLogin.callOnClick()
    }

    override fun onClick(clickedView: View?) {

        lateinit var fragment: Fragment
        when (clickedView!!.id) {
            R.id.btnLogin -> {

                fragment = LoginFragment()
                findViewById<View>(R.id.btnSignup).setBackgroundResource(R.drawable.button_background)
            }
            else -> {
                fragment = RegisterFragment()
                findViewById<View>(R.id.btnLogin).setBackgroundResource(R.drawable.button_background)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()

        clickedView.setBackgroundResource(R.drawable.button_background_selected)
    }

    fun startMainActivity(isStudent: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isStudent", isStudent)
        startActivity(intent)
        finish()
    }

}
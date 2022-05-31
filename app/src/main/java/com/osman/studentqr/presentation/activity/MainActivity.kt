package com.osman.studentqr.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.osman.studentqr.R
import com.osman.studentqr.presentation.fragment.teacher.TeacherFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container_view, fragment)
            .addToBackStack("")
            .commit()
    }
}
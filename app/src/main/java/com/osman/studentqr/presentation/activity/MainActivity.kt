package com.osman.studentqr.presentation.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.osman.studentqr.R
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.presentation.fragment.qr_scanner.QrScannerFragment
import com.osman.studentqr.presentation.fragment.student.StudentFragment
import com.osman.studentqr.presentation.fragment.teacher.TeacherFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var lesson: Lesson? = Lesson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isStudent = intent.extras?.getBoolean("isStudent")
        isStudent?.let {
            if (it) {
                val fragment = StudentFragment()
                loadFragmentWithoutBackStack(fragment)
            } else {
                val fragment = TeacherFragment()
                loadFragmentWithoutBackStack(fragment)
            }
        }

    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container_view, fragment)
            .addToBackStack("")
            .commit()
    }

    fun loadFragmentWithoutBackStack(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container_view, fragment)
            .commit()
    }

    fun openQrFragment() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        loadFragment(QrScannerFragment())
                    }

                    if (report.deniedPermissionResponses.size != 0 && !report.isAnyPermissionPermanentlyDenied) {
                        openQrFragment()
                    }

                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(
                    baseContext,
                    "Hata! ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onSameThread()
            .check()
    }

    private fun showSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Kamera İzini Gerekiyor")
        builder.setMessage("Kamera izinleri gerekiyor. Ayarları açmak ister misiniz?")
        builder.setPositiveButton(
            "Ayarlara git"
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, 111)
        }
        builder.setCancelable(false)
        builder.show()
    }

}
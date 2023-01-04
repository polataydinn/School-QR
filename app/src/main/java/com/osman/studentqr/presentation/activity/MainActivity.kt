package com.osman.studentqr.presentation.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.osman.studentqr.R
import com.osman.studentqr.common.AppPermission
import com.osman.studentqr.common.AppPermission.Companion.permissionGranted
import com.osman.studentqr.common.AppPermission.Companion.requestPermission
import com.osman.studentqr.common.FileHandler
import com.osman.studentqr.data.model.Lesson
import com.osman.studentqr.data.model.ReportData
import com.osman.studentqr.data.model.TeacherLesson
import com.osman.studentqr.presentation.fragment.qr_scanner.QrScannerFragment
import com.osman.studentqr.presentation.fragment.student.StudentFragment
import com.osman.studentqr.presentation.fragment.teacher.TeacherFragment
import com.osman.studentqr.util.PdfCreator
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var lesson: Lesson? = Lesson()
    var lessonPosition: Int = 0
    var teacherLesson: TeacherLesson? = TeacherLesson()
    var position: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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
        if (!permissionGranted(this)) requestPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppPermission.REQUEST_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission(this)
                toastErrorMessage("Permission should be allowed")
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

    fun createPdf(listOfReport: List<ReportData>, lessonName: String) {
        val onError: (Exception) -> Unit = { toastErrorMessage(it.message.toString()) }
        val onFinish: (File) -> Unit = { openFile(it) }

        val pdfService = PdfCreator()
        pdfService.createUserTable(
            data = listOfReport,
            lessonName = lessonName,
            onFinish = onFinish,
            onError = onError
        )
    }

    private fun openFile(file: File) {
        val path = FileHandler().getPathFromUri(this, file.toUri())
        val pdfFile = File(path)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfFile.toUri(), "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            toastErrorMessage("Can't read pdf file")
        }
    }

    private fun toastErrorMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

}
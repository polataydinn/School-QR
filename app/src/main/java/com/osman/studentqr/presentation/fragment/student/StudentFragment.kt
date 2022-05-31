package com.osman.studentqr.presentation.fragment.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.databinding.FragmentStudentBinding
import com.osman.studentqr.presentation.binding_adapter.BindingFragment

class StudentFragment : BindingFragment<FragmentStudentBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentStudentBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
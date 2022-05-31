package com.osman.studentqr.presentation.fragment.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.osman.studentqr.databinding.FragmentTeacherBinding
import com.osman.studentqr.presentation.binding_adapter.BindingFragment

class TeacherFragment : BindingFragment<FragmentTeacherBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentTeacherBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
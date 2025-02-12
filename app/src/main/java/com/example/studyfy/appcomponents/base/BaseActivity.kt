package com.example.studyfy.appcomponents.base

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.studyfy.R

abstract class BaseActivity<T : ViewDataBinding>(@LayoutRes val layoutId: Int) :
    AppCompatActivity(), BaseControllerFunctionsImpl {
    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        addObservers()
        setUpClicks()
        onInitialized()
    }
}

package com.example.studyfy

import android.app.Application
import com.example.studyfy.modules.db.CloudinaryService

class StudyfyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CloudinaryService.init(this)
    }
}

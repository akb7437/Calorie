package com.becker.calorie


import android.app.Application

class CalorieCounterApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CalorieRepository.initialize(this)
    }
}
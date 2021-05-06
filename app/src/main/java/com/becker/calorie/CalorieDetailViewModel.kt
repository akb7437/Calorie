package com.becker.calorie


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CalorieDetailViewModel : ViewModel() {

    private val calorieRepository = CalorieRepository.get()
    private val calorieIdLiveData = MutableLiveData<UUID>()

    val calorieLiveData: LiveData<Calorie?> =
            Transformations.switchMap(calorieIdLiveData) { calorieId ->
                calorieRepository.getCalorie(calorieId)
            }

    fun loadCalorie(calorieId: UUID) {
        calorieIdLiveData.value = calorieId
    }

    fun saveCalorie(calorie: Calorie) {
        calorieRepository.updateCrime(calorie)
    }
}
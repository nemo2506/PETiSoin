package com.openclassrooms.petisoin.data.repository

import com.openclassrooms.petisoin.data.dao.AnimalDao
import com.openclassrooms.petisoin.data.entity.Animal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnimalsRepository @Inject constructor(private val animalDao: AnimalDao) {
    suspend fun getAnimalById(id: Long): Animal = animalDao.getAnimalById(id)
    fun getAllAnimals(): Flow<List<Animal>> = animalDao.getAllAnimals()
    suspend fun addAnimal(animal: Animal) {
        animalDao.addAnimal(animal)
    }
    suspend fun deleteAnimal(animal: Animal) {
        animalDao.deleteAnimal(animal)
    }
}
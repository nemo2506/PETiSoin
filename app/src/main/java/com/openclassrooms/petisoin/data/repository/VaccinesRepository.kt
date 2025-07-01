package com.openclassrooms.petisoin.data.repository

import com.openclassrooms.petisoin.data.dao.VaccineDao
import com.openclassrooms.petisoin.data.entity.Vaccine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VaccinesRepository @Inject constructor(private val vaccineDao: VaccineDao)
{

  fun getAllVaccines(animalId: Long): Flow<List<Vaccine>> =
    vaccineDao.getAllVaccinesByAnimalId(animalId)

}
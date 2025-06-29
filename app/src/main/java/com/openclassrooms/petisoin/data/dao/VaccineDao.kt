package com.openclassrooms.petisoin.data.dao

import com.openclassrooms.petisoin.data.entity.Vaccine
import kotlinx.coroutines.flow.Flow

interface VaccineDao {
  
  fun getAllVaccinesByAnimalId(animalId: Long): Flow<List<Vaccine>>
  
  suspend fun addVaccine(vaccine: Vaccine)
  
  suspend fun deleteVaccine(vaccine: Vaccine)

}
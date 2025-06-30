package com.openclassrooms.petisoin.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.openclassrooms.petisoin.data.converter.Converters
import com.openclassrooms.petisoin.data.dao.AnimalDao
import com.openclassrooms.petisoin.data.dao.VaccineDao
import com.openclassrooms.petisoin.data.entity.Animal
import com.openclassrooms.petisoin.data.entity.Vaccine

@Database(entities = [Animal::class, Vaccine::class], version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class PETiSoinDatabase : RoomDatabase() {
  
  abstract fun animalDao(): AnimalDao
  
  abstract fun vaccineDao(): VaccineDao

  companion object {
    @Volatile
    private var Instance: PETiSoinDatabase? = null

    fun getDatabase(context: Context): PETiSoinDatabase {
      // if the Instance is not null, return it, otherwise create a new database instance.
      return Instance ?: synchronized(this) {
        Room.databaseBuilder(context, PETiSoinDatabase::class.java, "PETiSoinDatabase")
          .build()
          .also { Instance = it }
      }
    }
  }
}
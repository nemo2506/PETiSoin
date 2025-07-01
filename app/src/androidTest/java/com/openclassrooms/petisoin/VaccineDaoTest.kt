package com.openclassrooms.petisoin

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.openclassrooms.petisoin.data.dao.AnimalDao
import com.openclassrooms.petisoin.data.dao.VaccineDao
import com.openclassrooms.petisoin.data.database.PETiSoinDatabase
import com.openclassrooms.petisoin.data.entity.Animal
import com.openclassrooms.petisoin.data.entity.Race
import com.openclassrooms.petisoin.data.entity.Type
import com.openclassrooms.petisoin.data.entity.Vaccine
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import kotlinx.coroutines.flow.first

/**
 * Instrumented test class for testing the VaccineDao database operations.
 *
 * This test class uses an in-memory Room database to verify:
 * - Insertion and retrieval of vaccine records linked to animals.
 * - Deletion of vaccine records.
 * - Updating existing vaccine records via upsert.
 *
 * The tests use Kotlin coroutines and the Turbine library for testing Flow emissions.
 */
@RunWith(AndroidJUnit4::class)
class VaccineDaoTest {

    private lateinit var database: PETiSoinDatabase
    private lateinit var vaccineDao: VaccineDao
    private lateinit var animalDao: AnimalDao

    /**
     * Setup the in-memory database and initialize DAOs before each test.
     */
    @Before
    fun createDb() {
        database = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                PETiSoinDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        vaccineDao = database.vaccineDao()
        animalDao = database.animalDao()
    }

    /**
     * Close the database after each test to release resources.
     */
    @After
    fun closeDb() {
        database.close()
    }

    /**
     * Test inserting a vaccine linked to an animal and retrieving it by the animal's ID.
     * Verifies that the retrieved vaccine matches the inserted data.
     */
    @Test
    fun insert_vaccine_and_retrieve_by_animal_id_should_return_correct_data() = runTest {
        // Create and insert an animal
        val animal = Animal(
            type = Type.DOG,
            race = Race.GOLDEN_RETRIEVER,
            weight = 28.5,
            height = 55,
            color = "Blond"
        )
        animalDao.addAnimal(animal)

        // Create and insert a vaccine linked to the animal (assumed animal ID = 1)
        val vaccine = Vaccine(
            name = "Rage",
            injectionDate = Date(),
            animalId = 1
        )
        vaccineDao.addVaccine(vaccine)

        // Retrieve vaccines by animal ID and assert correctness
        vaccineDao.getAllVaccinesByAnimalId(1).test {
            val vaccines = awaitItem()
            assertThat(vaccines.size, `is`(1))
            assertThat(vaccines[0].name, `is`("Rage"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Test deleting a vaccine record.
     * Verifies that the vaccine is present before deletion and no longer present afterward.
     */
    @Test
    fun delete_vaccine_should_remove_it() = runTest {
        // Create and insert an animal
        val animal = Animal(
            type = Type.CAT,
            race = Race.SIEMESE,
            weight = 4.2,
            height = 22,
            color = "White"
        )
        animalDao.addAnimal(animal)

        // Create and insert a vaccine linked to the animal
        val vaccine = Vaccine(
            name = "Leucose",
            injectionDate = Date(),
            animalId = 1L
        )
        vaccineDao.addVaccine(vaccine)

        // Verify the vaccine exists before deletion
        vaccineDao.getAllVaccinesByAnimalId(1).test {
            val vaccinesBefore = awaitItem()
            assertThat(vaccinesBefore.isNotEmpty(), `is`(true))

           // Delete the vaccine
            vaccineDao.deleteVaccine(vaccinesBefore[0])
            cancelAndIgnoreRemainingEvents()
        }

        // Verify the vaccine list is empty after deletion
        vaccineDao.getAllVaccinesByAnimalId(1).test {
            val vaccinesAfter = awaitItem()
            assertThat(vaccinesAfter.isEmpty(), `is`(true))
            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * Test updating an existing vaccine record using upsert.
     * Inserts a vaccine, then inserts a modified version with the same ID and verifies update.
     */
    @Test
    fun update_vaccine_should_update_the_existing_record() = runTest {
        // Create and insert an animal
        val animal = Animal(
            type = Type.DOG,
            race = Race.GOLDEN_RETRIEVER,
            weight = 30.0,
            height = 60,
            color = "Golden"
        )
        animalDao.addAnimal(animal)

        // Insert original vaccine with forced ID = 1
        val vaccineOriginal = Vaccine(
            id = 1,
            name = "Vaccin Original",
            injectionDate = Date(),
            animalId = 1
        )
        vaccineDao.addVaccine(vaccineOriginal)

        // Verify insertion of original vaccine
        vaccineDao.getAllVaccinesByAnimalId(1).test {
            val list1 = awaitItem()
            assertThat(list1.size, `is`(1))
            assertThat(list1[0].name, `is`("Vaccin Original"))
            cancelAndIgnoreRemainingEvents()
        }

        // Insert updated vaccine with same ID and modified name
        val vaccineUpdated = vaccineOriginal.copy(name = "Vaccin Modifié")
        vaccineDao.addVaccine(vaccineUpdated)

        // Verify the vaccine record was updated
        vaccineDao.getAllVaccinesByAnimalId(1).test {
            val list2 = awaitItem()
            assertThat(list2.size, `is`(1))
            assertThat(list2[0].name, `is`("Vaccin Modifié"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
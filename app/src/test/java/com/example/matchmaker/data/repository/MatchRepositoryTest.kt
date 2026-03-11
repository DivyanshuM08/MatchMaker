package com.example.matchmaker.data.repository

import com.example.matchmaker.data.db.MatchProfileEntity
import com.example.matchmaker.data.db.ProfileStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MatchRepositoryTest {

    private lateinit var fakeDao: FakeMatchProfileDao
    private lateinit var fakeApi: FakeRandomUserApi
    private lateinit var repository: MatchRepository

    @Before
    fun setup() {
        fakeDao = FakeMatchProfileDao()
        fakeApi = FakeRandomUserApi()
        repository = MatchRepository(fakeDao, fakeApi)
    }

    @Test
    fun getAll_emitsWhatDaoHas() = runTest {
        val entity = MatchProfileEntity(
            id = "1",
            name = "Test",
            age = 28,
            city = "Sydney",
            country = "AU",
            imageUrl = "",
            education = "BS",
            religion = "X",
            occupation = "Dev",
            status = ProfileStatus.PENDING
        )
        fakeDao.insertAll(listOf(entity))

        val list = repository.getAll().first()
        assertEquals(1, list.size)
        assertEquals("1", list[0].id)
        assertEquals(ProfileStatus.PENDING, list[0].status)
    }

    @Test
    fun updateStatus_updatesEntityInDao() = runTest {
        val entity = MatchProfileEntity(
            id = "2",
            name = "Jane",
            age = 30,
            city = "Melbourne",
            country = "AU",
            imageUrl = "",
            education = "MS",
            religion = "Y",
            occupation = "Doc",
            status = ProfileStatus.PENDING
        )
        fakeDao.insertAll(listOf(entity))

        repository.updateStatus("2", ProfileStatus.ACCEPTED)

        val list = repository.getAll().first()
        assertEquals(1, list.size)
        assertEquals(ProfileStatus.ACCEPTED, list[0].status)
    }
}

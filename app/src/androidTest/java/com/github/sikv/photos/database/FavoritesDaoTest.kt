package com.github.sikv.photos.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sikv.photos.mock
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify

@Suppress("UNCHECKED_CAST")
@RunWith(AndroidJUnit4::class)
class FavoritesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var favoritesDatabase: FavoritesDatabase
    private lateinit var favoritesDao: FavoritesDao

    private val photo1 = FavoritePhotoEntity("1", "URL_1", "Source_1")
    private val photo2 = FavoritePhotoEntity("2", "URL_2", "Source_2")

    @Before
    fun setup() {
        favoritesDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context, FavoritesDatabase::class.java)
                .build()

        favoritesDao = favoritesDatabase.favoritesDao
    }

    @After
    fun tearDown() {
        favoritesDatabase.close()
    }

    @Test
    fun insertSavesData() {
        favoritesDao.insert(photo1)

        val count = favoritesDao.getCount()

        Assert.assertEquals(1, count)
    }

    @Test
    fun getAllRetrievesData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        val observer: Observer<List<FavoritePhotoEntity>> = mock()
        favoritesDao.getAll().observeForever(observer)

        val listClass = ArrayList::class.java as Class<ArrayList<FavoritePhotoEntity>>
        val argumentCaptor = ArgumentCaptor.forClass(listClass)

        verify(observer).onChanged(argumentCaptor.capture())

        Assert.assertTrue(argumentCaptor.value.containsAll(listOf(photo1, photo2)))
    }

    @Test
    fun getAllListRetrievesData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        val list = favoritesDao.getAllList()

        Assert.assertTrue(list.containsAll(listOf(photo1, photo2)))
    }

    @Test
    fun deleteDeletesCorrectData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        favoritesDao.delete(photo1)

        val observer: Observer<List<FavoritePhotoEntity>> = mock()
        favoritesDao.getAll().observeForever(observer)

        val listClass = ArrayList::class.java as Class<ArrayList<FavoritePhotoEntity>>
        val argumentCaptor = ArgumentCaptor.forClass(listClass)

        verify(observer).onChanged(argumentCaptor.capture())

        Assert.assertTrue(!argumentCaptor.value.contains(photo1))
    }

    @Test
    fun getByIdRetrievesCorrectData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        val retrievedPhoto = favoritesDao.getById(photo1.id)

        Assert.assertTrue(retrievedPhoto == photo1)
    }

    @Test
    fun getCountReturnsCorrectData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        val count = favoritesDao.getCount()

        Assert.assertEquals(2, count)
    }

    @Test
    fun deleteAllDeletesAllData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        favoritesDao.deleteAll()

        val count = favoritesDao.getCount()

        Assert.assertEquals(0, count)
    }
}
package com.github.sikv.photos.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sikv.photos.mock
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class FavoritesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var favoritesDatabase: FavoritesDatabase
    private lateinit var favoritesDao: FavoritesDao

    private val photo1 = FavoritePhotoEntity(id = "id1")
    private val photo2 = FavoritePhotoEntity(id = "id2")

    @Before
    fun setup() {
        favoritesDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context, FavoritesDatabase::class.java
        ).build()

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

    @Suppress("UNCHECKED_CAST")
    @Test
    fun getPhotosRetrievesData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        val observer: Observer<List<FavoritePhotoEntity>> = mock()

        favoritesDao.getPhotos(SimpleSQLiteQuery("SELECT * from FavoritePhoto WHERE markedAsDeleted=0"))
                .observeForever(observer)

        val listClass = ArrayList::class.java as Class<ArrayList<FavoritePhotoEntity>>
        val argumentCaptor = ArgumentCaptor.forClass(listClass)

        verify(observer).onChanged(argumentCaptor.capture())

        Assert.assertTrue(argumentCaptor.value.containsAll(listOf(photo1, photo2)))
    }

    @Test
    fun getByIdRetrievesCorrectData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        val retrievedPhoto = favoritesDao.getById(photo1.id)

        Assert.assertTrue(retrievedPhoto == photo1)
    }

    @Test
    fun getRandomRetrievesData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        val retrievedPhoto = favoritesDao.getRandom()

        Assert.assertTrue(retrievedPhoto == photo1 || retrievedPhoto == photo2)
    }

    @Test
    fun getCountReturnsCorrectData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        val count = favoritesDao.getCount()

        Assert.assertEquals(2, count)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun deleteDeletesCorrectData() {
        favoritesDao.insert(photo1)
        favoritesDao.insert(photo2)

        favoritesDao.delete(photo1)

        val observer: Observer<List<FavoritePhotoEntity>> = mock()

        favoritesDao.getPhotos(SimpleSQLiteQuery("SELECT * from FavoritePhoto WHERE markedAsDeleted=0"))
                .observeForever(observer)

        val listClass = ArrayList::class.java as Class<ArrayList<FavoritePhotoEntity>>
        val argumentCaptor = ArgumentCaptor.forClass(listClass)

        verify(observer).onChanged(argumentCaptor.capture())

        Assert.assertTrue(!argumentCaptor.value.contains(photo1))
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
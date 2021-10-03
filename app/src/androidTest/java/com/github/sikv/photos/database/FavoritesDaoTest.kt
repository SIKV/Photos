package com.github.sikv.photos.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.sikv.photos.config.DbConfig
import com.github.sikv.photos.database.dao.FavoritesDao
import com.github.sikv.photos.database.entity.FavoritePhotoEntity
import com.github.sikv.photos.enumeration.SortBy
import com.github.sikv.photos.mock
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class FavoritesDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var favoritesDb: FavoritesDb
    private lateinit var favoritesDao: FavoritesDao

    private val testPhoto1 = FavoritePhotoEntity(id = "id1")
    private val testPhoto2 = FavoritePhotoEntity(id = "id2")

    @Before
    fun setup() {
        favoritesDb = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context, FavoritesDb::class.java
        ).build()

        favoritesDao = favoritesDb.favoritesDao
    }

    @After
    fun tearDown() {
        favoritesDb.close()
    }

    @Test
    fun insert() {
        favoritesDao.insert(testPhoto1)
        val photo = favoritesDao.getById(testPhoto1.id)

        assertEquals(testPhoto1.id, photo?.id)
    }

    @Test
    fun getPhotosSortByNewest() {
        testGetPhotos(
                sortBy = SortBy.DATE_ADDED_NEWEST,
                photosList = listOf("1", "2"),
                expectedPhotosList = listOf("2", "1")
        )
    }

    @Test
    fun getPhotosSortByOldest() {
        testGetPhotos(
                sortBy = SortBy.DATE_ADDED_OLDEST,
                photosList = listOf("1", "2", "3"),
                expectedPhotosList = listOf("1", "2", "3")
        )
    }

    @Test
    fun getById() {
        favoritesDao.insert(testPhoto1)
        favoritesDao.insert(testPhoto2)

        val expectedPhoto = testPhoto2.getPhotoId()
        val actualPhoto = favoritesDao.getById(expectedPhoto)?.getPhotoId()

        assertEquals(expectedPhoto, actualPhoto)
    }

    @Test
    fun getRandom() {
        val photos = listOf(testPhoto1, testPhoto2)
        photos.forEach(favoritesDao::insert)

        val actualPhoto = favoritesDao.getRandom()?.getPhotoId()

        assertTrue(photos
                .map { it.getPhotoId() }
                .contains(actualPhoto)
        )
    }

    @Test
    fun getCount() {
        val photos = listOf(testPhoto1, testPhoto2)
        photos.forEach(favoritesDao::insert)

        val count = favoritesDao.getCount()
        assertEquals(photos.size, count)
    }

    @Test
    fun markAllAsDeleted() {
        favoritesDao.insert(testPhoto1)
        favoritesDao.insert(testPhoto2)

        favoritesDao.markAllAsDeleted()

        val markedPhotos = getAllPhotos()
        assertTrue(markedPhotos.all { it.markedAsDeleted })
    }

    @Test
    fun unmarkAllAsDeleted() {
        favoritesDao.insert(testPhoto1)
        favoritesDao.insert(testPhoto2)

        favoritesDao.markAllAsDeleted()
        favoritesDao.unmarkAllAsDeleted()

        val unmarkedPhotos = getAllPhotos()
        assertTrue(unmarkedPhotos.none { it.markedAsDeleted })
    }

    @Test
    fun delete() {
        favoritesDao.insert(testPhoto1)
        favoritesDao.insert(testPhoto2)

        val deletedPhoto = testPhoto1
        favoritesDao.delete(deletedPhoto)

        assertNull(favoritesDao.getById(deletedPhoto.getPhotoId()))
    }

    @Test
    fun deleteAll() {
        favoritesDao.insert(testPhoto1)
        favoritesDao.insert(testPhoto2)

        favoritesDao.deleteAll()
        val count = favoritesDao.getCount()

        assertEquals(0, count)
    }

    @Suppress("UNCHECKED_CAST")
    private fun testGetPhotos(
            sortBy: SortBy,
            photosList: List<String>,
            expectedPhotosList: List<String>
    ) {
        var dateAdded = 1L

        photosList.forEach { id ->
            favoritesDao.insert(
                    FavoritePhotoEntity(id = id, dateAdded = dateAdded++)
            )
        }

        val query = FavoritesDbQueryBuilder().buildGetPhotosQuery(sortBy)
        val observer: Observer<List<FavoritePhotoEntity>> = mock()
        favoritesDao.getPhotos(query).observeForever(observer)

        val listClass = ArrayList::class.java as Class<ArrayList<FavoritePhotoEntity>>
        val argumentCaptor = ArgumentCaptor.forClass(listClass)

        verify(observer).onChanged(argumentCaptor.capture())

        val actualPhotoList = argumentCaptor.value.map { it.id }
        assertArrayEquals(expectedPhotosList.toTypedArray(), actualPhotoList.toTypedArray())
    }

    @Suppress("UNCHECKED_CAST")
    private fun getAllPhotos(): List<FavoritePhotoEntity> {
        val query = SimpleSQLiteQuery("SELECT * from ${DbConfig.favoritePhotosTableName}")

        val observer: Observer<List<FavoritePhotoEntity>> = mock()
        favoritesDao.getPhotos(query).observeForever(observer)

        val listClass = ArrayList::class.java as Class<ArrayList<FavoritePhotoEntity>>
        val argumentCaptor = ArgumentCaptor.forClass(listClass)

        verify(observer).onChanged(argumentCaptor.capture())

        return argumentCaptor.value
    }
}
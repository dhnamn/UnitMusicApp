package com.example.finalsproject.repositoryTest

//import com.example.finalsproject.data.NetworkSongsCacheRepo
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mockito.`when`
//import org.mockito.kotlin.mock
//import java.io.File
//
//@ExperimentalCoroutinesApi
//class SongsCacheRepoTest {
//    private lateinit var repo: NetworkSongsCacheRepo
//    private lateinit var file: File
//
//    @Before
//    fun setup() {
//        file = mock()
//        every {file.path} return ""
////        `when`(file.path).thenReturn("/")
////        `when`(file.isDirectory).thenReturn(true)
////        `when`(file.exists()).thenReturn(true)
//        repo = NetworkSongsCacheRepo(file)
//    }
//
//    @Test
//    fun songsCacheRepoTest_get() = runBlocking {
//        val songId = 1L
//        val songFile = File(file, "song-id-${songId}")
//        songFile.createNewFile()
//
//        `when`(file.listFiles()).thenReturn(arrayOf(songFile))
//
//        val result = repo.get(songId)
//
//        Assert.assertEquals(songFile, result)
//    }
//}
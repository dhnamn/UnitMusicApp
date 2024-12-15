package com.example.finalsproject.test
//
//import androidx.activity.ComponentActivity
//import androidx.compose.ui.test.junit4.createAndroidComposeRule
//import androidx.compose.ui.test.onNodeWithText
//import com.example.finalsproject.model.Song
//import com.example.finalsproject.ui.screen.SongList
//import org.junit.Rule
//import org.junit.Test
//
//class QueueManagerScreenTest {
//    @get: Rule
//    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
//
//    private var img = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAeFBMVEX///8CAgL19fUPDw/8/Pzw8PDc3Nz39/e0tLTMzMzExMTAwMDIyMjp6env7++kpKRkZGSKioqRkZFvb29DQ0OYmJhVVVVOTk6CgoI1NTXj4+MgICDa2tpdXV0sLCygoKB6enpJSUk9PT0nJycbGxtzc3Otra1qamqzRWJdAAADs0lEQVR4nO3c2XLiQAwF0LSx8cJmHBMwECAkgf//wwlTwwDBS0s2pRZ1z3MepDLuRZLz8gIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwObFg8ksy7bRcCQdyiPE0425cowC6Yg6NZqae+lQOqzOLHcl+Z3kiXRonQhnFfmdvC+lw2tvua9J8MdEOsC2BvX5ndYc6RDb2TYmaMzek46yhbpX8CrFsXScbBOrBI2Zh9KRMiWWCRrzLR0qz9g6Qa0r6hshQ6NxX4woCZpcOly64JOUoYmkAyaz2QmvbaQDpvI2zUndGkiHTNR8WvvtTTpkopScobLlNKAnqGxPpP9If+6K0kGTlFUtmnyqqtzMGRmavnTUFJwEVe0XISvDrXTYBCNWhlPpsAnWrAwX0mET8DLcSYdN0H/6Z/j87yFvLVV1bOtxMlTVqXnnZKjqcpExEixU1b5jRoa6Ghge40VUVouqaopW6ylr7tN/pq/SIRP5H9QMY+mQqWglb2Pm0gGT+Tktw7V0wHS0N1Flf21BSPBLVRXqzC/sM1R1JL1YWic4kw6Vy/ZV3EkHymfXyVe5ypzFFudTTcWLEk1DX8pu9mXC+jP4XuFOfyeueYwzrbNCt/xoVfEGKrsw1UnuCzfFVlXVolkwWOTnR/lVpAdVrTRrQX8dD+O4/0Q/Tltaxy8HC8s3Lfr60DjTvp4bs7Jp647/rkJzbRvj+N9G3xh48L94vFN1R4wu59E09qv/bnRdHO/pqZgGx5udL5+UdyS84e3fGXNU8hjj+6m9+Sy+Dd7vR8eS+cyNipriofyI1ivS7DBIhskg2u4+Ks5xxhykw29GKUCVcf266DOGEn9JpXOoFZLG1yu8OXyl8rpI0OUUfVZzu8R7zRYq6rWjBJ1ttXGGSqs4OVtD7afVc/AEx5v0quZcESAk9GGsFK4tqG2PMvccO9zYf2toz6l7P/0jIAsbl8qNnCmvZpl0Whf2vVAad2b5ft/Vu+LMqFvXW+GFK5tiNzeKMo58C8WbzLfjRhW1uyvFPScuGbzBfFsudHHs/vkFlwOjNn7jLEIre/nr/iPXmRP5tabLm30Z+ds+63NRAvHRWsp/MOGR7tY87sR2Jn1y43yXTiP9eXC3FbYy0lW353+Gz/8eepW9zo6sxKs1z7/jP6TMduFCwa15DLiFwoliVJg96l1cZa6U9oNkmuarXpc+83SaSB/Ybvmh16VQ/l4IAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASv0Bxd0vhWtSk6MAAAAASUVORK5CYII="
//
//    private val listSongs = listOf(
//        Song(1, "Song1", "Artist1", "album1", img, "genre1", 120, "01/01/2020", 53, 43, true),
//        Song(2, "Song2", "Artist2", "album2", img, "genre2", 140, "02/02/2020", 43, 17, true),
//        Song(3, "Song3", "Artist3", "album3", img, "genre3", 130, "03/03/2020", 33, 6, false),
//        Song(4, "Song4", "Artist4", "album4", img, "genre4", 110, "04/04/2020", 33, 14, false),
//        Song(5, "Song5", "Artist5", "album5", img, "genre5", 170, "05/05/2020", 13, 3, true)
//    )
//
//    @Test
//    fun queueManagerScreenTest_verifyContent() {
//        composeTestRule.setContent {
//            SongList(
//                songs = listSongs,
//                currentIdx = 1,
//                onRemoveFromQueue = {}
//            )
//        }
//
//        composeTestRule.onNodeWithText("Song2").assertExists()
//    }
//}
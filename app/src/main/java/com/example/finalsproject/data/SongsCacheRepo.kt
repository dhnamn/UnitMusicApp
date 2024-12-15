package com.example.finalsproject.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile

interface SongsCacheRepo {
    suspend fun get(id: Long): File?
    suspend fun set(id: Long, data: ByteArray): File
}

class NetworkSongsCacheRepo(
    contextCacheDir: File
) : SongsCacheRepo {
    private val songsDir = File(contextCacheDir, "songs-dir")
        .apply { mkdir() }

    override suspend fun get(id: Long): File? {
        val file = songsDir.listFiles()?.find { it.name == fileNameOfSong(id) }
        if (file == null) {
            return null
        }
        withContext(Dispatchers.IO) {
            RandomAccessFile(file.absolutePath, "rw").use { raf ->
                raf.channel.lock().use {}
            }
        }
        return file
    }

    override suspend fun set(id: Long, data: ByteArray): File =
        File(songsDir, fileNameOfSong(id)).also { file ->
            RandomAccessFile(file.absolutePath, "rw").use { raf ->
                file.createNewFile()
                raf.channel.lock().use {
                    raf.write(data)
                }
            }
        }

    private fun fileNameOfSong(id: Long) = "song-id-${id}"
}
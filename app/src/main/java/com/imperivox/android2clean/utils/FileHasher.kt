package com.imperivox.android2clean.utils

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

object FileHasher {
    fun hashFile(file: File): String {
        return FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            val md = MessageDigest.getInstance("MD5")
            var read: Int

            while (fis.read(buffer).also { read = it } > 0) {
                md.update(buffer, 0, read)
            }

            md.digest().joinToString("") { "%02x".format(it) }
        }
    }

    fun quickHash(file: File): String {
        // Quick hash using file size and first 1KB
        return FileInputStream(file).use { fis ->
            val buffer = ByteArray(1024)
            val bytesRead = fis.read(buffer)
            val prefix = if (bytesRead > 0) {
                buffer.slice(0 until bytesRead).joinToString("") { "%02x".format(it) }
            } else ""
            "${file.length()}_$prefix"
        }
    }
}
package com.wave.hextractor.pojo

/**
 * File with digests.
 */
data class FileWithDigests(
    val name: String,
    val bytes: ByteArray,
    val md5: String,
    val sha1: String,
    val crc32: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileWithDigests

        if (name != other.name) return false
        if (md5 != other.md5) return false
        if (sha1 != other.sha1) return false
        if (crc32 != other.crc32) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + md5.hashCode()
        result = 31 * result + sha1.hashCode()
        result = 31 * result + crc32.hashCode()
        return result
    }
}

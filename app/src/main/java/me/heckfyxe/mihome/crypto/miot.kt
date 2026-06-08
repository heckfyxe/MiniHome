package me.heckfyxe.mihome.crypto

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MiotCipher(token: ByteArray) {
    companion object {
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val ALGORITHM = "AES"
    }

    private val key = MessageDigest.getInstance("MD5").digest(token)
    private val iv = MessageDigest.getInstance("MD5").digest(key + token)

    fun encrypt(payload: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, ALGORITHM), IvParameterSpec(iv))
        return cipher.doFinal(payload)
    }

    fun decrypt(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, ALGORITHM), IvParameterSpec(iv))
        return cipher.doFinal(data)
    }
}
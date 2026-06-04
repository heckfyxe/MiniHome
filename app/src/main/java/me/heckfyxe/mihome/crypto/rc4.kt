package me.heckfyxe.mihome.crypto

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

fun encryptRC4(password: String, payload: String): String {
    val cipher = Cipher.getInstance("RC4")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(Base64.decode(password), "RC4"))
    cipher.update(ByteArray(1024))
    val encrypted = cipher.doFinal(payload.toByteArray(Charsets.UTF_8))
    return Base64.encode(encrypted)
}

fun decryptRC4(password: String, payload: String): String {
    val cipher = Cipher.getInstance("RC4")
    cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(Base64.decode(password), "RC4"))
    cipher.update(ByteArray(1024))
    val decryptedBytes = cipher.doFinal(Base64.decode(payload))
    return String(decryptedBytes, Charsets.UTF_8)
}


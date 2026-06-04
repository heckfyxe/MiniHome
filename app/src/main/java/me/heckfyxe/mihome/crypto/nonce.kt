package me.heckfyxe.mihome.crypto

import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer
import java.security.SecureRandom
import kotlin.io.encoding.Base64
import kotlin.time.Clock

fun generateNonce(): String {
    val secureRandom = SecureRandom()
    val bytes = ByteArray(8)
    secureRandom.nextBytes(bytes)

    val millis = Clock.System.now().toEpochMilliseconds()
    val buffer = ByteBuffer.allocate(4).putInt((millis / 60000).toInt())

    val nonceBytes = bytes + buffer.array() as ByteArray
    return Base64.encode(nonceBytes)
}

fun signNonce(ssecurity: String, nonce: String): String {
    val message = Base64.decode(ssecurity) + Base64.decode(nonce)
    return message.toByteString().sha256().base64()
}

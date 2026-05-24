package me.heckfyxe.mihome.util

fun generateAgent(): String {
    val agentId = CharArray(13) { Char((65..69).random()) }.concatToString()
    val randomText = CharArray(18) { Char((97..122).random()) }.concatToString()
    return "$randomText-$agentId APP/com.xiaomi.mihome APPV/10.5.201"
}

fun generateDeviceId() = CharArray(6) { Char((97..122).random()) }.concatToString()
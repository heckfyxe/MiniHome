package me.heckfyxe.mihome.data.remote

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.core.writeFully
import kotlinx.coroutines.Dispatchers
import kotlinx.io.Source
import kotlinx.io.bytestring.ByteString
import kotlinx.io.readByteArray
import kotlinx.io.readByteString
import kotlinx.io.readUInt
import kotlinx.io.readUShort
import kotlinx.io.writeUInt
import kotlinx.io.writeUShort
import kotlinx.serialization.json.Json
import me.heckfyxe.mihome.crypto.MiotCipher
import me.heckfyxe.mihome.data.local.database.entities.Device
import me.heckfyxe.mihome.di.DeviceScope
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped
import timber.log.Timber
import java.security.MessageDigest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Scope(DeviceScope::class)
@Scoped
class Miot(@Provided private val device: Device, private val json: Json) {
    companion object {
        private const val PORT = 54321
    }

    private val cipher = MiotCipher(device.token.hexToByteArray())

    private var id = 0
    private var deviceTimestamp = 0.seconds

    suspend fun discover() {
        device.localIp ?: return

        val hello = "21310020ffffffffffffffffffffffffffffffffffffffffffffffffffffffff".hexToByteArray()
        SelectorManager(Dispatchers.IO).use { selectorManager ->
            aSocket(selectorManager).udp().connect(InetSocketAddress(device.localIp, PORT)) { broadcast = true }
                .use { socket ->
                    socket.send(Datagram(buildPacket { writeFully(hello) }, socket.remoteAddress))
                    val data = socket.receive().packet
                    val header = MiotMessage(data)
                    deviceTimestamp = header.duration
                    Timber.tag("MIOT").d(header.toString())
                }
        }
    }

    suspend fun send(command: String, params: List<Map<String, Any>>) {
        device.localIp ?: return
        discover()

        val request = createRequest(command, params)
        val sendTimestamp = deviceTimestamp + 1.seconds
        val data = cipher.encrypt(json.encodeToString(request).toByteArray() + 0x00)
        val message = MiotMessage.create(device, sendTimestamp, data)
        SelectorManager(Dispatchers.IO).use { selectorManager ->
            aSocket(selectorManager).udp().connect(InetSocketAddress(device.localIp, PORT)).use { socket ->
                socket.send(Datagram(message.toPacket(), socket.remoteAddress))
                val message = MiotMessage(socket.receive().packet)
                Timber.d(message.toString())
                Timber.d(message.data?.toByteArray()?.let { cipher.decrypt(it) }?.decodeToString())
            }
        }
    }

    private fun createRequest(command: String, parameters: List<Map<String, Any>>) = mapOf(
        "id" to getNewMessageId(),
        "method" to command,
        "params" to parameters,
    )

    private fun getNewMessageId(): Int {
        id++
        if (id > 9999) id = 1
        return id
    }
}

data class MiotMessage(
    val magic: UShort,
    val length: Int,
    val unknown: UInt,
    val deviceId: Long,
    val duration: Duration,
    val checksum: String,
    val data: ByteString?,
) {
    companion object {
        fun create(device: Device, duration: Duration, data: ByteArray?) = MiotMessage(
            magic = 8497u,
            length = (data?.size ?: 0) + 32,
            unknown = 0u,
            deviceId = device.id.toLong(),
            duration = duration,
            checksum = if (data?.isNotEmpty() == true)
                MessageDigest.getInstance("MD5")
                    .digest(fieldsByteArray(device.id.toLong(), duration, device.token.hexToByteArray(), data))
                    .toHexString()
            else ByteArray(16).toHexString(),
            data = data?.let(::ByteString),
        )

        private fun fieldsByteArray(deviceId: Long, duration: Duration, token: ByteArray, data: ByteArray?) =
            buildPacket {
                writeUShort(8497u)
                writeUShort((32 + (data?.size ?: 0)).toUShort())
                writeUInt(0u)
                writeUInt(deviceId.toUInt())
                writeUInt(duration.inWholeSeconds.toUInt())
                write(token)
                data?.let(::write)
            }.readByteArray()
    }

    fun toPacket() = buildPacket {
        writeUShort(magic)
        writeUShort(length.toUShort())
        writeUInt(unknown)
        writeUInt(deviceId.toUInt())
        writeUInt(duration.inWholeSeconds.toUInt())
        write(checksum.hexToByteArray())
        data?.toByteArray()?.also(::write)
    }
}

fun MiotMessage(packet: Source): MiotMessage {
    var length = 0
    return MiotMessage(
        magic = packet.readUShort(),
        length = packet.readUShort().toInt().also { length = it },
        unknown = packet.readUInt(),
        deviceId = packet.readUInt().toLong(),
        duration = packet.readUInt().toLong().seconds,
        checksum = packet.readByteArray(16).toHexString(),
        data = packet.readByteString(length - 32),
    )
}


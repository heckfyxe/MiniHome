package me.heckfyxe.mihome.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SecondsToDurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            "me.heckfyxe.mihome.SecondsToDurationSerializer",
            PrimitiveKind.LONG
        )

    override fun serialize(encoder: Encoder, value: Duration) =
        encoder.encodeLong(value.inWholeSeconds)

    override fun deserialize(decoder: Decoder) =
        decoder.decodeLong().toDuration(DurationUnit.SECONDS)
}
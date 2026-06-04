package me.heckfyxe.mihome.data.model

enum class XiaomiCountry(val iso: String) {
    China("cn"),
    Germany("de"),
    US("us"),
    Russia("ru"),
    Taiwan("tw"),
    Singapore("sg"),
    India("in"),
    I2("i2");

    override fun toString() = iso
}
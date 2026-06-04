package me.heckfyxe.mihome.data.repository

import me.heckfyxe.mihome.data.local.database.dao.AccountDao
import me.heckfyxe.mihome.data.model.XiaomiCountry
import me.heckfyxe.mihome.data.remote.XiaomiApi
import me.heckfyxe.mihome.di.UserScope
import org.koin.core.annotation.Scope
import org.koin.core.annotation.Scoped

@Scope(UserScope::class)
@Scoped
class HomeRepository(private val xiaomiApi: XiaomiApi, private val accountDao: AccountDao) {
    suspend fun getHomes() = xiaomiApi.getHomes(XiaomiCountry.Germany)

    suspend fun logout() {
        accountDao.delete()
    }
}
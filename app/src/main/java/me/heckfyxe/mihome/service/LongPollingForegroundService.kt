package me.heckfyxe.mihome.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.heckfyxe.mihome.MainActivity
import me.heckfyxe.mihome.R
import me.heckfyxe.mihome.data.repository.AuthRepository
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class LongPollingForegroundService : Service() {
    companion object {
        private const val CHANNEL_POLLING_SERVICE =
            "me.heckfyxe.mihome.POLLING_SERVICE_NOTIFICATION_CHANNEL"

        private const val TAG = "me.heckfyxe.mihome.service.LongPollingForegroundService"

        private const val EXTRA_POLLING_URL = "$TAG.EXTRA_POLLING_URL"
        private const val EXTRA_TIMEOUT = "$TAG.EXTRA_TIMEOUT"

        fun createIntent(context: Context, url: String, timeout: Duration = 5.minutes) =
            Intent(context, LongPollingForegroundService::class.java).apply {
                putExtra(EXTRA_POLLING_URL, url)
                putExtra(EXTRA_TIMEOUT, timeout.inWholeMilliseconds)
            }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    private val repository: AuthRepository by inject()

    override fun onBind(p0: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(
                CHANNEL_POLLING_SERVICE, NotificationManagerCompat.IMPORTANCE_LOW
            ).setName(getString(R.string.polling_service_notification_title)).build()
        )
    }

    private fun createNotification(timeout: Duration) =
        NotificationCompat.Builder(this, CHANNEL_POLLING_SERVICE)
            .setContentTitle(getString(R.string.polling_service_notification_title))
            .setUsesChronometer(true).setChronometerCountDown(true)
            .setWhen((Clock.System.now() + timeout).toEpochMilliseconds()).build()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val bundle = requireExtraNonNull(intent?.extras)
            val url = requireExtraNonNull(bundle.getString(EXTRA_POLLING_URL))
            val timeout = bundle.getLong(EXTRA_TIMEOUT, 5.minutes.inWholeMilliseconds).milliseconds

            createNotificationChannel()
            ServiceCompat.startForeground(
                this,
                1,
                createNotification(timeout),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC else 0
            )
            scope.launch { runPolling(url, timeout) }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return START_NOT_STICKY
    }

    private fun <T> requireExtraNonNull(arg: T?): T =
        requireNotNull(arg) { "Required extra not found. Use LongPollingForegroundService.newIntent method" }

    private suspend fun runPolling(url: String, timeout: Duration) {
        val result = runCatching { repository.startLongPolling(url, timeout) }
        withContext(Dispatchers.Main) {
            if (result.isFailure) {
                stopSelf()
                return@withContext
            }

            val intent = Intent(this@LongPollingForegroundService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
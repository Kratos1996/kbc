package com.ishan.kbc.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.ishan.kbc.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val pool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val lockId: Int = pool.load(context, R.raw.lock, 1)
    private val correctId: Int = pool.load(context, R.raw.correct, 1)
    private val wrongId: Int = pool.load(context, R.raw.wrong, 1)
    private val fanfareId: Int = pool.load(context, R.raw.fanfare, 1)

    private var _volume: Float = 1.0f

    fun setVolume(vol: Float) { _volume = vol.coerceIn(0f, 1f) }

    fun playLock() { pool.play(lockId, _volume, _volume, 1, 0, 1f) }

    fun playCorrect() { pool.play(correctId, _volume, _volume, 1, 0, 1f) }

    fun playWrong() { pool.play(wrongId, _volume, _volume, 1, 0, 1f) }

    fun playFanfare() { pool.play(fanfareId, _volume, _volume, 1, 0, 1f) }

    fun release() {
        pool.unload(lockId)
        pool.unload(correctId)
        pool.unload(wrongId)
        pool.unload(fanfareId)
        pool.release()
    }
}

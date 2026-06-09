package com.ishan.kbc.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
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

    // TODO: load from res/raw/ once audio assets are added
    // pool.load(context, R.raw.lock, 1)
    // pool.load(context, R.raw.correct, 1)
    // pool.load(context, R.raw.wrong, 1)
    // pool.load(context, R.raw.fanfare, 1)

    fun playLock() { /* pool.play(lockId, ...) */ }
    fun playCorrect() { /* pool.play(correctId, ...) */ }
    fun playWrong() { /* pool.play(wrongId, ...) */ }
    fun playFanfare() { /* pool.play(fanfareId, ...) */ }

    fun release() { pool.release() }
}

package com.example.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

object ButtonFeedbackHelper {
    private var clickTrack: AudioTrack? = null
    private var isAudioTrackInitialized = false

    @Synchronized
    private fun getClickTrack(): AudioTrack? {
        if (isAudioTrackInitialized) return clickTrack
        try {
            val sampleRate = 44100
            val durationMs = 12
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Crisp click with rapid exponential decay for realistic calculator button feel
                val decay = Math.exp(-t / 0.0022)
                val wave = Math.sin(2.0 * Math.PI * 1900.0 * t) * 0.65 +
                        Math.sin(2.0 * Math.PI * 1050.0 * t) * 0.35
                buffer[i] = (wave * decay * 24000).toInt().coerceIn(-32768, 32767).toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            clickTrack = track
        } catch (_: Exception) {
            clickTrack = null
        }
        isAudioTrackInitialized = true
        return clickTrack
    }

    fun playLightVibration(context: Context, view: View?) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(7L)
                }
            } else {
                view?.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
        } catch (_: Exception) {
            view?.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    fun playClickSound(context: Context, view: View?) {
        var playedViaTrack = false
        try {
            val track = getClickTrack()
            if (track != null && track.state == AudioTrack.STATE_INITIALIZED) {
                track.stop()
                track.reloadStaticData()
                track.play()
                playedViaTrack = true
            }
        } catch (_: Exception) {
            playedViaTrack = false
        }

        if (!playedViaTrack) {
            try {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.8f)
            } catch (_: Exception) {
                view?.playSoundEffect(android.view.SoundEffectConstants.CLICK)
            }
        }
    }
}

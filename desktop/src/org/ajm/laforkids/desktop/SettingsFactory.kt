package org.ajm.laforkids.desktop

import org.ajm.laforkids.Settings
import java.util.*

class SettingsFactory(val tag: String = "Test Tag 223", val max: Int = 25) {

    private val random = Random()


    fun generateTestSettings(): Settings {
        val settings = Settings(tag)

        settings.maxValue = random.nextInt(max * max) + 1
        settings.minValue = -random.nextInt(max * max) - 1

        settings.answerAlternatives = random.nextInt(max) + 1
        settings.answerMaxError = random.nextInt(max) + 1

        settings.minColumnsLeft = random.nextInt(max) + 1
        settings.maxColumnsLeft = settings.minColumnsLeft + random.nextInt(max) + 1

        settings.minRowsLeft = random.nextInt(max) + 1
        settings.maxRowsLeft = settings.minRowsLeft + random.nextInt(max) + 1

        settings.minColumnsRight = random.nextInt(max) + 1
        settings.maxColumnsRight = settings.minColumnsRight + random.nextInt(max) + 1

        return settings
    }
}
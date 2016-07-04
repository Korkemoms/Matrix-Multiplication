package org.ajm.laforkids

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Array

/**
 * The settings that can be adjusted in the [SettingsInterface].
 */
class Settings {

    private val prefs: Preferences

    val SETTING_MIN_VALUE = "minValue"
    val SETTING_MAX_VALUE = "maxValue"

    val SETTING_MIN_ROWS_LEFT = "minRowsLeft"
    val SETTING_MAX_ROWS_LEFT = "maxRowsLeft"

    val SETTING_MIN_COLUMNS_LEFT = "minColumnsLeft"
    val SETTING_MAX_COLUMNS_LEFT = "maxColumnsLeft"

    val SETTING_MIN_COLUMNS_RIGHT = "minColumnsRight"
    val SETTING_MAX_COLUMNS_RIGHT = "maxColumnsRight"

    val SETTING_ANSWER_ALTERNATIVES = "answerAlternatives"
    val SETTING_ANSWER_MAX_ERROR = "answerMaxError"

    /**
     * Load and save settings using the given tag.
     */
    constructor(tag: String) {
        prefs = Gdx.app.getPreferences(tag)

        minValue = prefs.getInteger(SETTING_MIN_VALUE, -10)
        maxValue = prefs.getInteger(SETTING_MAX_VALUE, 10)

        minRowsLeft = prefs.getInteger(SETTING_MIN_ROWS_LEFT, 1)
        maxRowsLeft = prefs.getInteger(SETTING_MAX_ROWS_LEFT, 3)

        minColumnsLeft = prefs.getInteger(SETTING_MIN_COLUMNS_LEFT, 1)
        maxColumnsLeft = prefs.getInteger(SETTING_MAX_COLUMNS_LEFT, 3)

        minColumnsRight = prefs.getInteger(SETTING_MIN_COLUMNS_RIGHT, 1)
        maxColumnsRight = prefs.getInteger(SETTING_MAX_COLUMNS_RIGHT, 3)

        answerAlternatives = prefs.getInteger(SETTING_ANSWER_ALTERNATIVES, 3)
        answerMaxError = prefs.getInteger(SETTING_ANSWER_MAX_ERROR, 10)

    }

    /**
     * Reload settings from gdx preferences.
     */
    fun reload() {
        minValue = prefs.getInteger(SETTING_MIN_VALUE, -10)
        maxValue = prefs.getInteger(SETTING_MAX_VALUE, 10)

        minRowsLeft = prefs.getInteger(SETTING_MIN_ROWS_LEFT, 1)
        maxRowsLeft = prefs.getInteger(SETTING_MAX_ROWS_LEFT, 3)

        minColumnsLeft = prefs.getInteger(SETTING_MIN_COLUMNS_LEFT, 1)
        maxColumnsLeft = prefs.getInteger(SETTING_MAX_COLUMNS_LEFT, 3)

        minColumnsRight = prefs.getInteger(SETTING_MIN_COLUMNS_RIGHT, 1)
        maxColumnsRight = prefs.getInteger(SETTING_MAX_COLUMNS_RIGHT, 3)

        answerAlternatives = prefs.getInteger(SETTING_ANSWER_ALTERNATIVES, 3)
        answerMaxError = prefs.getInteger(SETTING_ANSWER_MAX_ERROR, 10)
    }


    /** Smallest possible entry value. */
    var minValue: Int
    /** Largest possible entry value. */
    var maxValue: Int

    /** Lowest possible number of rows in left matrix, A. */
    var minRowsLeft: Int
    /** Highest possible number of rows in left matrix, A. */
    var maxRowsLeft: Int

    /** Lowest possible number of columns in left matrix, A. The number of rows in the right matrix B must be
     * the same. */
    var minColumnsLeft: Int
    /** Highest possible number of columns in left matrix, A. The number of rows in the right matrix B must be
     * the same. */
    var maxColumnsLeft: Int

    /** Lowest possible number of rows in right matrix, B. */
    var minColumnsRight: Int
    /** Highest possible number of rows in right matrix, B. */
    var maxColumnsRight: Int

    /** The number of value to choose from when entering your answer. */
    var answerAlternatives: Int

    /** The maximum error of the answer alternatives. */
    var answerMaxError: Int


    /**
     * Checks if the current settings make sense. For every setting that
     * is illegal a integer is added to the returned array.
     */
    fun dataInvariant(): Array<Int> {

        val errors = Array<Int>()

        if (minValue > maxValue) errors.add(0)

        if (minRowsLeft < 0) errors.add(1)
        if (maxRowsLeft < 0) errors.add(1)
        if (minRowsLeft > maxRowsLeft) errors.add(1)

        if (minColumnsLeft < 0) errors.add(2)
        if (maxColumnsLeft < 0) errors.add(2)
        if (minColumnsLeft > maxColumnsLeft) errors.add(2)

        if (minColumnsRight < 0) errors.add(3)
        if (maxColumnsRight < 0) errors.add(3)
        if (minColumnsRight > maxColumnsRight) errors.add(3)

        if (answerAlternatives < 1) errors.add(4)
        if (answerMaxError < 1) errors.add(5)

        return errors

    }

    /**
     *
     * Must be called after updating settings to ensure they
     * persist after app is closed.
     * @return whether settings were saved
     * */
    fun saveSettingsForever(): Boolean {
        val ok = dataInvariant().size == 0

        if (ok) {
            prefs.putInteger(SETTING_MIN_VALUE, minValue)
            prefs.putInteger(SETTING_MAX_VALUE, maxValue)
            prefs.putInteger(SETTING_MIN_ROWS_LEFT, minRowsLeft)
            prefs.putInteger(SETTING_MAX_ROWS_LEFT, maxRowsLeft)
            prefs.putInteger(SETTING_MIN_COLUMNS_LEFT, minColumnsLeft)
            prefs.putInteger(SETTING_MAX_COLUMNS_LEFT, maxColumnsLeft)
            prefs.putInteger(SETTING_MIN_COLUMNS_RIGHT, minColumnsRight)
            prefs.putInteger(SETTING_MAX_COLUMNS_RIGHT, maxColumnsRight)
            prefs.putInteger(SETTING_ANSWER_ALTERNATIVES, answerAlternatives)
            prefs.putInteger(SETTING_ANSWER_MAX_ERROR, answerMaxError)

            prefs.flush()
        }
        return ok
    }

}
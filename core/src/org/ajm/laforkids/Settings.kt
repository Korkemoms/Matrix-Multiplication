package org.ajm.laforkids

import com.badlogic.gdx.Gdx

/**
 * The settings that can be adjusted in the [SettingsInterface].
 */
class Settings {

    private val prefs = Gdx.app.getPreferences("Matrix Multiplication");

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


    /** Smallest possible entry value. */
    var minValue = prefs.getInteger(SETTING_MIN_VALUE, -10)
        set(value) {
            if (value > maxValue) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MIN_VALUE, value)
        }
    /** Largest possible entry value. */
    var maxValue = prefs.getInteger(SETTING_MAX_VALUE, 10)
        set(value) {
            if (value < minValue) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MAX_VALUE, value)
        }

    /** Lowest possible number of rows in left matrix, A. */
    var minRowsLeft = prefs.getInteger(SETTING_MIN_ROWS_LEFT, 1)
        set(value) {
            if (value < 0 || value > maxRowsLeft) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MIN_ROWS_LEFT, value)
        }
    /** Highest possible number of rows in left matrix, A. */
    var maxRowsLeft = prefs.getInteger(SETTING_MAX_ROWS_LEFT, 3)
        set(value) {
            if (value < 0 || value < minRowsLeft) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MAX_ROWS_LEFT, value)
        }

    /** Lowest possible number of columns in left matrix, A. The number of rows in the right matrix B must be
     * the same. */
    var minColumnsLeft = prefs.getInteger(SETTING_MIN_COLUMNS_LEFT, 1)
        set(value) {
            if (value < 0 || value > maxColumnsLeft) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MIN_COLUMNS_LEFT, value)
        }
    /** Highest possible number of columns in left matrix, A. The number of rows in the right matrix B must be
     * the same. */
    var maxColumnsLeft = prefs.getInteger(SETTING_MAX_COLUMNS_LEFT, 3)
        set(value) {
            if (value < 0 || value < minColumnsLeft) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MAX_COLUMNS_LEFT, value)
        }

    /** Lowest possible number of rows in right matrix, B. */
    var minColumnsRight = prefs.getInteger(SETTING_MIN_COLUMNS_RIGHT, 1)
        set(value) {
            if (value < 0 || value > maxColumnsRight) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MIN_COLUMNS_RIGHT, value)
        }
    /** Highest possible number of rows in right matrix, B. */
    var maxColumnsRight = prefs.getInteger(SETTING_MAX_COLUMNS_RIGHT, 3)
        set(value) {
            if (value < 0 || value < minColumnsRight) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_MAX_COLUMNS_RIGHT, value)
        }

    /** The number of value to choose from when entering your answer. */
    var answerAlternatives = prefs.getInteger(SETTING_ANSWER_ALTERNATIVES, 3)
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_ANSWER_ALTERNATIVES, value)
        }
    /** The maximum error of the answer alternatives. */
    var answerMaxError = prefs.getInteger(SETTING_ANSWER_MAX_ERROR, 10)
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value;
            prefs.putInteger(SETTING_ANSWER_MAX_ERROR, value)
        }


    /** Must be called after updating settings to ensure they
     * persist after app is closed. */
    fun saveSettingsForever() {
        prefs.flush()
    }
}
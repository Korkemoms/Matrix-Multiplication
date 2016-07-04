package org.ajm.laforkids

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import org.ajm.laforkids.actors.IColoredMultiplicationTable
import java.util.*

/**
 * The rules of the game is implemented here.
 */
class GameLogic {
    private var multiplicationTable: IColoredMultiplicationTable? = null
        private set(value) {
            if (value == null) throw IllegalArgumentException()
            field = value
        }

    private val settings: Settings
    private val random = Random()

    /** The progress of the game, iterates over all the entries in the product matrix C. */
    var progress = 0
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
        }

    var rowsLeft = 0
        private set(value) {
            field = value
        }
    var columnsLeft = 0
        private set(value) {
            field = value
        }
    var columnsRight = 0
        private set(value) {
            field = value
        }
    var answerAlternatives = 0
        private set(value) {
            field = value
        }

    constructor(settings: Settings) {
        this.settings = settings
    }

    /**
     *
     * 1. [newGame]
     * 2. [init]
     * 3. [progress]
     * 4. [progress]
     * 5. ...
     * 6. [newGame]
     * 7. [init]
     * 8. ...
     */
    fun init(multiplicationTable: IColoredMultiplicationTable) {
        if (multiplicationTable.rowsLeft != rowsLeft || rowsLeft <= 0) throw IllegalArgumentException()
        if (multiplicationTable.columnsLeft != columnsLeft || columnsLeft <= 0) throw IllegalArgumentException()
        if (multiplicationTable.columnsRight != columnsRight || columnsRight <= 0) throw IllegalArgumentException()
        if (multiplicationTable.answerAlternatives != answerAlternatives || answerAlternatives <= 0) throw IllegalArgumentException()

        this.multiplicationTable = multiplicationTable

        multiplicationTable.highlightCol = getHighlightCol()
        multiplicationTable.highlightRow = getHighlightRow()
    }

    /**
     *
     * 1. [newGame]
     * 2. [init]
     * 3. [progress]
     * 4. [progress]
     * 5. ...
     * 6. [newGame]
     * 7. [init]
     * 8. ...
     */
    fun progress(): Boolean {
        assertInitialized()

        // set entry of product matrix to the correct answer
        val correctAnswer = getCorrectAnswer()

        multiplicationTable!!.matrixProduct.set(getHighlightRow(), getHighlightCol(), correctAnswer)
        progress = MathUtils.clamp(progress + 1, 0, maxProgress())

        val completed = isComplete()

        if (!completed) {
            multiplicationTable!!.highlightCol = getHighlightCol()
            multiplicationTable!!.highlightRow = getHighlightRow()
        }

        return completed
    }

    fun maxProgress(): Int {
        return multiplicationTable!!.rowsLeft * multiplicationTable!!.columnsRight
    }

    fun isComplete(): Boolean {
        assertInitialized()
        return progress >= maxProgress()
    }

    /**
     *
     * 1. [newGame]
     * 2. [init]
     * 3. [progress]
     * 4. [progress]
     * 5. ...
     * 6. [newGame]
     * 7. [init]
     * 8. ...
     */
    fun newGame(rowsLeft: Int = getRandomLeftRowCount(),
                columnsLeft: Int = getRandomLeftColumnCount(),
                columnsRight: Int = getRandomRightColumnCount(),
                answerAlternatives: Int = getAnswerAlternativesCount()) {
        if (rowsLeft < 1) throw IllegalArgumentException()
        if (columnsLeft < 1) throw IllegalArgumentException()
        if (columnsRight < 1) throw IllegalArgumentException()
        if (answerAlternatives < 1) throw IllegalArgumentException()

        this.rowsLeft = rowsLeft
        this.columnsLeft = columnsLeft
        this.columnsRight = columnsRight
        this.answerAlternatives = answerAlternatives
        progress = 0
    }

    /**
     * Compute the correct answer for currently highlighted entry.
     * @throws [NumberFormatException] if a needed entry is not a number
     */
    fun getCorrectAnswer(): Int{
        assertInitialized()

        val i = getHighlightRow()
        val j = getHighlightCol()
        // convolve i'th left row with j'th right col
        var correctAnswer = 0

        for (k in 0 until multiplicationTable!!.columnsLeft) {
            val a = multiplicationTable!!.matrixLeft.get(i, k).toInt()
            val b = multiplicationTable!!.matrixRight.get(k, j).toInt()
            correctAnswer += a * b
        }
        return correctAnswer
    }

    fun getHighlightRow(): Int {
        return Math.min(progress % multiplicationTable!!.rowsLeft, maxProgress() - 1)
    }

    fun getHighlightCol(): Int {
        return Math.min(progress / multiplicationTable!!.rowsLeft, maxProgress() - 1)
    }

    fun getRandomLeftRowCount() = random.nextInt(settings.maxRowsLeft - settings.minRowsLeft + 1) + settings.minRowsLeft
    fun getRandomLeftColumnCount() = random.nextInt(settings.maxColumnsLeft - settings.minColumnsLeft + 1) + settings.minColumnsLeft
    fun getRandomRightColumnCount() = random.nextInt(settings.maxColumnsRight - settings.minColumnsRight + 1) + settings.minColumnsRight
    fun getAnswerAlternativesCount() = settings.answerAlternatives

    /**
     * Generate some answer alternatives.
     * @throws [NumberFormatException] if a needed entry is not a number
     */
    fun getAnswerAlternatives(): Array<String> {
        assertInitialized()
        if (progress < 0) throw IllegalStateException()
        if (progress >= maxProgress()) throw IllegalStateException()


        val alternatives = Array<String>()

        val correctAnswer = getCorrectAnswer()

        // make up some alternatives
        val errors = Array<Int>()
        for (i in 0 until multiplicationTable!!.answerAlternatives) {
            var error = 0
            var j = 0
            while ((error == 0 || errors.contains(error, false)) && j++ < 10) {
                error = random.nextInt(Math.max(settings.answerMaxError * 2, 1)) - settings.answerMaxError
            }
            errors.add(error)
            alternatives.add((correctAnswer + error).toString())
        }

        // let one be correct
        alternatives.set(random.nextInt(alternatives.size), correctAnswer.toString())

        return alternatives
    }

    /**
     * Update the answer alternatives found on the bottom
     * of the screen.
     * @throws [NumberFormatException] if a needed entry is not a number
     */
    fun updateAnswerAlternatives() {
        assertInitialized()
        var i = 0

        for (alternative in getAnswerAlternatives()) {
            multiplicationTable!!.matrixAnswers.set(0, i++, alternative)
        }
    }

    fun assertInitialized() {
        if (multiplicationTable == null) throw IllegalStateException()
        if (!multiplicationTable!!.initialized) throw IllegalStateException()
    }
}
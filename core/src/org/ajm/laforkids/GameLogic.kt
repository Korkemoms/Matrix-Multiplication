package org.ajm.laforkids

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.utils.Array
import org.ajm.laforkids.actors.IColoredMultiplicationTable
import java.util.*

/**
 * The rules of the game is implemented here.
 */
class GameLogic {

    var score = 0
        private set(value) {
            field = value
        }

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

    private var completed = false
    private var scoreThisRound = 0

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

        // increment score if completed previous game
        if (completed) score += Math.max(scoreThisRound / 2, 0)
        completed = false
        scoreThisRound = 0
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
     *
     * @return whether given answer was the correct one.
     */
    fun progress(answer: Int): Boolean {
        assertInitialized()

        // set entry of product matrix to the correct answer
        val correctAnswer = getCorrectAnswer()

        if (answer != correctAnswer) {
            score -= score(correctAnswer)
            scoreThisRound -= score(correctAnswer)
            return false
        }

        multiplicationTable!!.matrixProduct.set(getHighlightRow(), getHighlightCol(), correctAnswer)
        progress = MathUtils.clamp(progress + 1, 0, maxProgress())

        val completed = isComplete()

        if (!completed) {
            multiplicationTable!!.highlightCol = getHighlightCol()
            multiplicationTable!!.highlightRow = getHighlightRow()
        } else {
            this.completed = true
        }

        score += score(correctAnswer)
        scoreThisRound += score(correctAnswer)
        return true
    }

    private fun score(answer: Int): Int {
        val i = Math.max(1.0, Math.log((settings.maxValue - settings.minValue).toDouble())).toInt()
        return i * columnsLeft * Math.max(2, Math.log(answer.toDouble()).toInt())
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
    fun getCorrectAnswer(): Int {
        assertInitialized()

        return getCorrectAnswer(getHighlightRow(), getHighlightCol())
    }

    /**
     * Compute the correct answer for currently highlighted entry.
     * @throws [NumberFormatException] if a needed entry is not a number
     */
    fun getCorrectAnswer(row: Int, col: Int): Int {
        assertInitialized()

        // convolve i'th left row with j'th right col
        var correctAnswer = 0

        for (k in 0 until multiplicationTable!!.columnsLeft) {
            val a = multiplicationTable!!.matrixLeft.get(row, k).toInt()
            val b = multiplicationTable!!.matrixRight.get(k, col).toInt()
            correctAnswer += a * b
        }
        return correctAnswer
    }

    /**
     * Correct any non empty entry in the product matrix.
     */
    fun updateAnswers() {
        assertInitialized()

        for (row in 0 until rowsLeft) {
            for (col in 0 until columnsRight) {
                val current = multiplicationTable!!.matrixProduct.get(row, col)
                if (current.isEmpty()) continue
                multiplicationTable!!.matrixProduct.set(row, col, getCorrectAnswer(row, col))
            }
        }
    }

    fun getHighlightRow(): Int {
        return Math.min(progress , maxProgress() - 1)% multiplicationTable!!.rowsLeft
    }

    fun getHighlightCol(): Int {
        return Math.min(progress , maxProgress() - 1)/ multiplicationTable!!.rowsLeft
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

        for (cell in multiplicationTable!!.matrixAnswers.cells) {
            cell.actor.isVisible = true
        }

        for (alternative in getAnswerAlternatives()) {
            multiplicationTable!!.matrixAnswers.set(0, i++, alternative)
        }
    }

    fun assertInitialized() {
        if (multiplicationTable == null) throw IllegalStateException()
        if (!multiplicationTable!!.initialized) throw IllegalStateException()
    }
}
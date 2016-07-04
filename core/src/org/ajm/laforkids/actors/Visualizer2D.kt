package org.ajm.laforkids.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import org.ajm.laforkids.actors.MultiplicationTable

/**
 * If one of the matrices on the left side, and the matrix on the right side,
 * has two entries(are 2d vectors) then the multiplication can be visualized by this class.
 */
class Visualizer2D : Actor {

    private var beforeX = 0
    private var beforeY = 0

    private var afterX = 0
    private var afterY = 0

    private var drawAfterX = false
    private var drawAfterY = false

    val axisColor = Color(Color.GRAY)
    val beforeColor = Color(Color.RED)
    val afterColor = Color(Color.BLUE)


    /** Used for drawing stuff */
    private val dot: TextureRegion
    private val multiplicationTable: MultiplicationTable

    constructor(skin: Skin, multiplicationTable: MultiplicationTable) {
        touchable = Touchable.disabled

        val left = multiplicationTable.matrixLeft
        val leftEntries = left.matrixColumns * left.matrixRows

        val right = multiplicationTable.matrixRight
        val rightEntries = right.matrixColumns * right.matrixRows

        val product = multiplicationTable.matrixProduct
        val productEntries = product.matrixColumns * product.matrixRows

        val ok = productEntries == 2 && (leftEntries == 2 || rightEntries == 2)
        if (!ok) throw IllegalArgumentException()

        dot = skin.getRegion("dot")
        this.multiplicationTable = multiplicationTable

        multiplicationTable.changeListeners.add(Runnable {
            update()
        })

    }

    /**
     * Determine the vectors to be drawn.
     */
    fun update() {
        val left = multiplicationTable.matrixLeft
        val right = multiplicationTable.matrixRight
        val product = multiplicationTable.matrixProduct

        try {
            if (left.matrixRows == 1 && left.matrixColumns == 2) {
                beforeX = left.get(0, 0).toInt()
                beforeY = left.get(0, 1).toInt()
            } else if (left.matrixRows == 2 && left.matrixColumns == 1) {
                beforeX = left.get(0, 0).toInt()
                beforeY = left.get(1, 0).toInt()
            } else if (right.matrixRows == 1 && right.matrixColumns == 2) {
                beforeX = right.get(0, 0).toInt()
                beforeY = right.get(0, 1).toInt()
            } else if (right.matrixRows == 2 && right.matrixColumns == 1) {
                beforeX = right.get(0, 0).toInt()
                beforeY = right.get(1, 0).toInt()
            } else throw IllegalStateException()

            if (product.matrixRows == 1 && product.matrixColumns == 2) {
                afterX = computeProductEntry(0, 0)
                afterY = computeProductEntry(0, 1)
            } else if (product.matrixRows == 2 && product.matrixColumns == 1) {
                afterX = computeProductEntry(0, 0)
                afterY = computeProductEntry(1, 0)
            } else throw IllegalStateException()
        } catch (e: NumberFormatException) {
            // may happen text is just "-" (minus)
        }

        drawAfterX = !product.get(0).isEmpty()
        drawAfterY = !product.get(1).isEmpty()


    }

    fun computeProductEntry(row: Int, col: Int): Int {
        // convolve i'th left row with j'th right col
        var correctAnswer = 0

        for (k in 0 until multiplicationTable.columnsLeft) {
            val a = multiplicationTable.matrixLeft.get(row, k).toInt()
            val b = multiplicationTable.matrixRight.get(k, col).toInt()
            correctAnswer += a * b
        }
        return correctAnswer
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch as Batch

        val lineThickness = (2f + 6f * Math.min(width, height) / (1000f)).toInt().toFloat()
        val pos = localToStageCoordinates(Vector2())

        // draw axes
        batch.color = axisColor
        batch.draw(dot, pos.x, pos.y + height / 2f - lineThickness / 2f, width, lineThickness)
        batch.draw(dot, pos.x + width / 2f - lineThickness / 2f, pos.y, lineThickness, height)

        // determine scale
        val dim: Float = Math.min(width, height) / 2f
        var scale = Float.MAX_VALUE
        if (beforeX != 0) scale = Math.min(Math.abs(dim / beforeX), scale)
        if (beforeY != 0) scale = Math.min(Math.abs(dim / beforeY), scale)
        if (afterX != 0) scale = Math.min(Math.abs(dim / afterX), scale)
        if (afterY != 0) scale = Math.min(Math.abs(dim / afterY), scale)
        scale *= 0.9f

        // draw crosses representing the matrices(vectors) with 2 entries
        batch.color = beforeColor
        cross(batch, pos.x + width / 2f + beforeX * scale, pos.y + height / 2f + beforeY * scale, lineThickness)

        if (drawAfterX) {
            batch.color = axisColor
            cross(batch, pos.x + width / 2f + afterX * scale, pos.y + height / 2f, lineThickness)
        }
        if (drawAfterY) {
            batch.color = axisColor
            cross(batch, pos.x + width / 2f, pos.y + height / 2f + afterY * scale, lineThickness)
        }

        if (drawAfterX && drawAfterY) {
            batch.color = afterColor
            cross(batch, pos.x + width / 2f + afterX * scale, pos.y + height / 2f + afterY * scale, lineThickness)
        }
    }

    private fun cross(batch: Batch, x: Float, y: Float, thickness: Float) {
        batch.draw(dot, x - thickness * 3f, y - thickness / 2f, thickness * 6f, thickness)
        batch.draw(dot, x - thickness / 2f, y - thickness * 3f, thickness, thickness * 6f)
    }
}
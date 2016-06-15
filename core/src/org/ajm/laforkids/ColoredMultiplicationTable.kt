package org.ajm.laforkids

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Skin


/**
 * Also draws some colored rectangles for helping the player compute.
 */
class ColoredMultiplicationTable : MultiplicationTable {

    // settings, all can be changed
    var interpolationMethod = Interpolation.pow3Out
    var selectionColor = Color.FOREST
    var highlight = true
    var interpolationTime = 0.5f
        set(value) {
            if (value <= 0) throw IllegalArgumentException()
            field = value
        }
    var highlightRow = 0
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
        }
    var highlightCol = 0
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
        }
    var outlineThickness = 5f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
            matrixLeft.outlineThickness = outlineThickness
            matrixProduct.outlineThickness = outlineThickness
            matrixRight.outlineThickness = outlineThickness
        }


    // stuff for drawing the helping rectangles
    private var doneAnimating = false
    private var beganAnimation = System.currentTimeMillis()
    private val dot: TextureRegion
    private val productRectangle = Rectangle()
    private val leftRectangle = Rectangle()
    private val rightRectangle = Rectangle()


    /**
     * RowsRight is always the same as ColumnsLeft
     *
     * @param skin contains some custom graphics, also needed for labels
     * @param rowsLeft the number of rows in the left matrix A
     * @param columnsLeft the number of columns in the left matrix A, the same as the number of rows in the right matrix, B
     * @param columnsRight the number of columns in the right matrix B
     * @param answerAlternatives the number of answer buttons to choose from
     */
    constructor(skin: Skin, rowsLeft: Int, columnsLeft: Int, columnsRight: Int, answerAlternatives: Int)
    : super(skin, rowsLeft, columnsLeft, columnsRight, answerAlternatives) {

        // dot is just a pixel used to draw rectangles
        dot = skin.getRegion("dot")
    }

    /**
     * Draw all the matrices and also, if enabled, some rectangles for helping the player.
     */
    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        if (highlight) {
            batch!!.color = selectionColor

            if (!doneAnimating) {
                animate(batch)
            } else {
                outlineRectangle(batch, productRectangle.x, productRectangle.y, productRectangle.width, productRectangle.height, outlineThickness)
                outlineRectangle(batch, leftRectangle.x, leftRectangle.y, leftRectangle.width, leftRectangle.height, outlineThickness)
                outlineRectangle(batch, rightRectangle.x, rightRectangle.y, rightRectangle.width, rightRectangle.height, outlineThickness)

            }
        }
    }

    /**
     * Start the animation that moves the helping rectangles
     * to their correct position.
     */
    fun beginAnimation() {
        doneAnimating = false
        beganAnimation = System.currentTimeMillis()
    }

    /**
     * Move and draw help rectangles.
     */
    private fun animate(batch: Batch) {

        // determine how far the animation is
        var linearInterp = (System.currentTimeMillis() - beganAnimation) / 1000f
        linearInterp = MathUtils.clamp(linearInterp / interpolationTime, 0f, 1f)
        val nonLinearInterp = interpolationMethod.apply(linearInterp)
        val interp = nonLinearInterp


        // request render if not done animating
        if (linearInterp < 1) Gdx.graphics.requestRendering()


        val pad = matrixInsidePad * 2
        val thickness = outlineThickness;

        // animate product rectangle
        var actor = matrixProduct.getCell(highlightRow, highlightCol)!!.actor
        var pos = actor.localToStageCoordinates(Vector2(0f, 0f));
        var x = productRectangle.x * (1f - interp) + floor(pos.x) * interp
        var y = productRectangle.y * (1f - interp) + floor(pos.y) * interp
        var width = productRectangle.width * (1f - interp) + floor(actor.width) * interp
        var height = productRectangle.height * (1f - interp) + floor(actor.height) * interp
        outlineRectangle(batch, x, y, width, height, thickness)
        if (MathUtils.isEqual(interp, 1f)) {
            productRectangle.set(x, y, width, height)
        }


        // animate left rectangle
        actor = matrixLeft.getCell(highlightRow, 0)!!.actor
        pos = actor.localToStageCoordinates(Vector2(0f, 0f));
        x = leftRectangle.x * (1f - interp) + floor(pos.x) * interp
        y = leftRectangle.y * (1f - interp) + floor(pos.y) * interp
        width = (leftRectangle.width + pad) * (1f - interp) + floor(matrixLeft.width) * interp
        height = leftRectangle.height * (1f - interp) + floor(actor.height) * interp
        outlineRectangle(batch, x, y, width - pad, height, thickness)
        if (MathUtils.isEqual(interp, 1f)) {
            leftRectangle.set(x, y, width - pad, height)
        }


        // animate right rectangle
        actor = matrixRight.getCell(rowsRight - 1, highlightCol)!!.actor
        pos = actor.localToStageCoordinates(Vector2(0f, 0f));
        x = rightRectangle.x * (1f - interp) + floor(pos.x) * interp
        y = rightRectangle.y * (1f - interp) + floor(pos.y) * interp
        width = rightRectangle.width * (1f - interp) + floor(actor.width) * interp
        height = (rightRectangle.height + pad) * (1f - interp) + floor(matrixRight.height) * interp
        outlineRectangle(batch, x, y, width, height - pad, thickness)
        if (MathUtils.isEqual(interp, 1f)) {
            rightRectangle.set(x, y, width, height - pad)
        }

        if (MathUtils.isEqual(interp, 1f)) {
            doneAnimating = true
        }
    }

    /**
     * Draw a rectangle.
     *
     * @param x lower left
     * @param y lower left
     * @param width width of rect
     * @param height height of rect
     * @param outlineThickness thickness of outline
     *
     */
    private fun outlineRectangle(batch: Batch, x: Float, y: Float, width: Float, height: Float, outlineThickness: Float) {
        val y = floor(y)
        val x = floor(x)
        val height = floor(height)
        val width = floor(width)
        val outlineThickness = floor(outlineThickness)


        batch.draw(dot, x, y, outlineThickness, height)
        batch.draw(dot, x, y, width, outlineThickness)
        batch.draw(dot, x, y + height - outlineThickness, width, outlineThickness)
        batch.draw(dot, x + width - outlineThickness, y, outlineThickness, height)
    }

    private fun floor(a: Float) = Math.floor(a.toDouble()).toFloat()

}
package org.ajm.laforkids.desktop

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import org.ajm.laforkids.IMatrix

import org.ajm.laforkids.Matrix
import org.junit.Assert
import org.junit.Test
import org.junit.runners.Parameterized
import java.util.*

/**
 * Tests the properties of [Matrix] defined in [IMatrix].
 */
class MatrixTest {
    val random = Random()


    @Test
    fun setGet1() {

        val skin = DesktopLauncher.skin!!

        val rows = random.nextInt(100) + 1
        val columns = random.nextInt(100) + 1

        // test constructor
        val matrix: IMatrix = Matrix(skin, rows, columns)
        Assert.assertEquals(rows, matrix.matrixRows)
        Assert.assertEquals(columns, matrix.matrixColumns)

        // test set get
        val drawOutlines = random.nextBoolean()
        val outlineThickness = random.nextFloat() * 1000f
        val entryPad = random.nextFloat() * 1000f
        val entryWidth = random.nextFloat() * 1000f
        val entryHeight = random.nextFloat() * 1000f
        val backgroundTextWidth = random.nextFloat() * 1000f
        val backgroundTextHeight = random.nextFloat() * 1000f
        val backgroundText = random.nextLong().toString()
        val backgroundTextColor = Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat())

        matrix.drawOutlines = drawOutlines
        Assert.assertEquals(drawOutlines, matrix.drawOutlines)

        matrix.outlineThickness = outlineThickness
        Assert.assertEquals(outlineThickness, matrix.outlineThickness)

        matrix.entryPad = entryPad
        Assert.assertEquals(entryPad, matrix.entryPad)

        matrix.entryWidth = entryWidth
        Assert.assertEquals(entryWidth, matrix.entryWidth)

        matrix.entryHeight = entryHeight
        Assert.assertEquals(entryHeight, matrix.entryHeight)

        matrix.backgroundTextWidth = backgroundTextWidth
        Assert.assertEquals(backgroundTextWidth, matrix.backgroundTextWidth)

        matrix.backgroundTextHeight = backgroundTextHeight
        Assert.assertEquals(backgroundTextHeight, matrix.backgroundTextHeight)

        matrix.backgroundText = backgroundText
        Assert.assertEquals(backgroundText, matrix.backgroundText)

        matrix.backgroundTextColor = backgroundTextColor
        Assert.assertEquals(backgroundTextColor, matrix.backgroundTextColor)

    }

    @Test
    fun setGet2() {

        val skin = DesktopLauncher.skin!!

        val rows = random.nextInt(100) + 1
        val columns = random.nextInt(100) + 1


        val matrix: IMatrix = Matrix(skin, rows, columns)
        val control = IntArray(rows * columns)

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val i = random.nextInt()
                control[row * columns + col] = i
                matrix.set(row, col, i)
            }
        }

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                val expected = control[row * columns + col].toString()
                val actual = matrix.get(row, col)
                val actual2 = matrix.getCell(row,col)!!.actor.text.toString()

                Assert.assertEquals(expected,actual)
                Assert.assertEquals(expected,actual2)


            }
        }

    }
}
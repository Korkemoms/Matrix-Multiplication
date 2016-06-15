package org.ajm.laforkids

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import java.util.*

/**
 * Contains 3 matrices that are set up to visualize a matrix multiplication.
 * Also contains a vector with answer alternatives.
 */
open class MultiplicationTable : Table {

    /** A */
    val matrixLeft: Matrix
    /** AB=C */
    val matrixProduct: Matrix
    /** B */
    val matrixRight: Matrix
    /** Vector at the bottom displaying answer alternatives.*/
    val matrixAnswers: Matrix

    /** Shows the text 'AB=C' */
    val equationLabel: Label

    /** The number of rows in the left matrix. */
    val rowsLeft: Int
    /** The number of columns in the left matrix. Equal to the number of rows in the right matrix.*/
    val columnsLeft: Int
    /** The number of rows in the right matrix. Equal to the number of columns in the left matrix. */
    val rowsRight: Int
    /** The number of columns in the right matrix. */
    val columnsRight: Int

    /** The number of entries in the answer alternative vector at the bottom */
    val answerAlternatives: Int

    /** The height of each entry, in all matrices and also the answer vector. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var entryHeight = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
            matrixLeft.entryHeight = entryHeight
            matrixProduct.entryHeight = entryHeight
            matrixRight.entryHeight = entryHeight
            matrixAnswers.entryHeight = entryHeight
            mustPack = true
        }

    /** The width of each entry, in all matrices and also the answer vector. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var entryWidth = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
            matrixLeft.entryWidth = entryWidth
            matrixProduct.entryWidth = entryWidth
            matrixRight.entryWidth = entryWidth
            matrixAnswers.entryWidth = entryWidth
            mustPack = true
        }

    /** The padding inside each matrix boundary. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var matrixInsidePad = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
            for (cell in table.cells) {
                val actor = cell.actor
                if (actor is Matrix)
                    actor.pad(value)
            }
            for (cell in cells) {
                val actor = cell.actor
                if (actor is Matrix)
                    actor.pad(value)
            }

            mustPack = true
        }

    /** The padding outside each matrix boundary. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var matrixOutsidePad = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
            for (cell in table.cells) {
                cell.pad(value)
            }
            for (cell in cells) {
                cell.pad(value)
            }

            mustPack = true
        }

    /** The padding around each entry of each matrix. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var matrixEntryPad = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
            matrixLeft.entryPad = value
            matrixProduct.entryPad = value
            matrixRight.entryPad = value
            matrixAnswers.entryPad = value
            mustPack = true
        }


    private val table: Table
    private var mustPack = false

    /**
     * RowsRight is always the same as ColumnsLeft
     *
     * @param skin contains some custom graphics, also needed for labels
     * @param rowsLeft the number of rows in the left matrix A
     * @param columnsLeft the number of columns in the left matrix A, the same as the number of rows in the right matrix, B
     * @param columnsRight the number of columns in the right matrix B
     * @param answerAlternatives the number of answer buttons to choose from
     */
    constructor(skin: Skin, rowsLeft: Int, columnsLeft: Int, columnsRight: Int, answerAlternatives: Int) {
        this.skin = skin
        this.rowsLeft = rowsLeft
        this.columnsLeft = columnsLeft
        this.columnsRight = columnsRight
        this.rowsRight = columnsLeft
        this.answerAlternatives = answerAlternatives

        equationLabel = Label("AB=C", skin)

        matrixRight = Matrix(skin, rowsRight, columnsRight)
        matrixLeft = Matrix(skin, rowsLeft, columnsLeft)
        matrixProduct = Matrix(skin, rowsLeft, columnsRight)
        matrixAnswers = Matrix(skin, 1, answerAlternatives)
        matrixAnswers.drawOutlines = false


        table = Table()
        table.add(equationLabel)
        table.add(matrixRight)
        table.row()
        table.add(matrixLeft)
        table.add(matrixProduct)
        add(table)
        row()
        add(matrixAnswers)

        matrixLeft.align(Align.bottomRight)
        matrixProduct.align(Align.bottomLeft)
        matrixRight.align(Align.topLeft)

        matrixLeft.backgroundText = "A"
        matrixRight.backgroundText = "B"
        matrixProduct.backgroundText = "C"
    }

    /** Copy the entry values from all the matrices of given multiplication table.
     * @param multiplicationTable the one to copy all the entries from
     * */
    open fun copyEntries(multiplicationTable: MultiplicationTable) {

        for (row in 0 until  rowsLeft) {
            for (col in 0 until  columnsLeft) {
                matrixLeft.set(row, col, multiplicationTable.matrixLeft.get(row, col))
            }
        }

        for (row in 0 until rowsLeft) {
            for (col in 0 until  columnsRight) {
                matrixProduct.set(row, col, multiplicationTable.matrixProduct.get(row, col))
            }
        }

        for (row in 0 until rowsRight) {
            for (col in 0 until  columnsRight) {
                matrixRight.set(row, col, multiplicationTable.matrixRight.get(row, col))
            }
        }

        for (col in 0 until matrixAnswers.columns) {
            matrixAnswers.set(0, col, multiplicationTable.matrixAnswers.get(0, col))
        }
    }

    /**
     * Randomize the entries in the left and right matrix, erase the entries in the product matrix.
     * @param min the smallest possible value
     * @param max the largest possible value
     */
    fun randomizeEntries(min: Int, max: Int) {
        val random = Random()

        for (row in 0 until  rowsLeft) {
            for (col in 0 until  columnsLeft) {
                matrixLeft.set(row, col, random.nextInt(max - min + 1) + min)
            }
        }

        for (row in 0 until rowsRight) {
            for (col in 0 until  columnsRight) {
                matrixRight.set(row, col, random.nextInt(max - min + 1) + min)
            }
        }

        for (row in 0 until rowsLeft) {
            for (col in 0 until  columnsRight) {
                matrixProduct.set(row, col, "")
            }
        }

    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (mustPack) {
            pack()
            mustPack = false
        }
        super.draw(batch, parentAlpha)
    }

}
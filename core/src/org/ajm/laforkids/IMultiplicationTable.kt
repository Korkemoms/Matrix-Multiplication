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
interface IMultiplicationTable {

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
    var entryHeight: Float

    /** The width of each entry, in all matrices and also the answer vector. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var entryWidth: Float

    /** The padding inside each matrix boundary. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var matrixInsidePad: Float

    /** The padding outside each matrix boundary. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var matrixOutsidePad: Float

    /** The padding around each entry of each matrix. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    var matrixEntryPad: Float


    /** Copy the entry values from all the matrices of given multiplication table.
     * @param multiplicationTable the one to copy all the entries from
     * */
    open fun copyEntries(multiplicationTable: IMultiplicationTable) {
        if (rowsLeft != multiplicationTable.rowsLeft) throw IllegalArgumentException()
        if (columnsLeft != multiplicationTable.columnsLeft) throw IllegalArgumentException()
        if (columnsRight != multiplicationTable.columnsRight) throw IllegalArgumentException()

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

}
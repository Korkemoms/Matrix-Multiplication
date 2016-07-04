package org.ajm.laforkids.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label


/**
 * Matrix with string entries.
 */
interface IMatrix {

    /** Whether to draw the matrix left and right outlines. */
    var drawOutlines: Boolean

    /** The width of the outlines. */
    var outlineThickness: Float

    /** The padding around each entry. */
    var entryPad: Float

    /** The width of each entry. */
    var entryWidth: Float

    /** The height of each entry. */
    var entryHeight: Float

    /** The width of the text displayed in the background. */
    var backgroundTextWidth: Float

    /** The height of the text displayed in the background. */
    var backgroundTextHeight: Float

    /** Text drawn behind the entries. Intended for displaying
     * the name of the matrix, for example: A */
    var backgroundText: String

    /** The color of the background text. */
    var backgroundTextColor: Color

    /** The number of rows. */
    val matrixRows: Int

    /** The number of columns*/
    val matrixColumns: Int

    /**
     *
     * Set the value of the entry at given row and column.
     *
     * toString() is called on given value and the resulting string
     * is the value that is saved in the indicated position.
     *
     * (0,0) is top left
     *
     * @param row the row of the entry
     * @param col the column of the entry
     * @param value toString() is called on this value
     */
    fun set(row: Int, col: Int, value: Any)

    /**
     * Get the string stored in the indicated position.
     *
     * (0,0) is top left
     *
     * @param row the row of the entry
     * @param col the column of the entry
     * @return the string stored in the indicated position
     */
    fun get(row: Int, col: Int): String

    /**
     * Get the cell of indicated position.
     *
     * (0,0) is top left
     *
     * @param row the row of the entry
     * @param col the column of the entry
     * @return the cell of indicated position
     */
    fun getCell(row: Int, col: Int): Cell<Label>?

    /**
     * @return the string stored in the ith position.
     */
    fun get(entry: Int): String

    /**
     * @return the number of entries in this matrix.
     * */
    fun size():Int

}

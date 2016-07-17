package org.ajm.laforkids.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import org.ajm.laforkids.actors.IMatrix

/**
 * Table representing a matrix with integer entries.
 */
open class Matrix : IMatrix, Table {

    /** Whether to draw the matrix left and right outlines. */
    override var drawOutlines = true

    /** The width of the outlines. */
    override var outlineThickness = 5f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
        }

    /** The padding around each entry. */
    override var entryPad = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            for (cell in cells) {
                cell.pad(value)
            }
            mustPack = true
            field = value
        }

    /** The width of each entry. */
    override var entryWidth = 15f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            if (field != value) {
                mustPack = true
                for (cell in cells)
                    cell.width(value)
            }

            field = value
        }

    /** The height of each entry. */
    override var entryHeight = 15f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            if (field != value) {
                mustPack = true
                for (cell in cells)
                    cell.height(value)
            }

            field = value
        }

    /** Used for drawing the outlines. */
    private val dot: TextureRegion
    private var mustPack = false
    private var backgroundFont: BitmapFont? = null

    /** The width of the text displayed in the background. */
    override var backgroundTextWidth = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
        }

    /** The height of the text displayed in the background. */
    override var backgroundTextHeight = 0f
        set(value) {
            if (value < 0) throw IllegalArgumentException()
            field = value
        }

    /** Text drawn behind the entries. Intended for displaying
     * the name of the matrix, for example: A */
    override var backgroundText = ""
        set(value) {
            field = value
            val glyphLayout = GlyphLayout(backgroundFont, value)
            backgroundTextWidth = glyphLayout.width
            backgroundTextHeight = glyphLayout.height
        }

    /** The color of the background text. */
    override var backgroundTextColor = Color(0.9f, 0.9f, 0.9f, 1f)

    override val matrixRows: Int
    override val matrixColumns: Int

    /**
     *
     * @param skin needed for the fonts
     * @param matrixRows number of rows the matrix should have
     * @param matrixColumns number of columns the matrix should have
     */
    constructor(skin: Skin, matrixRows: Int, matrixColumns: Int) {
        if (matrixRows < 1) throw IllegalArgumentException()
        if (matrixColumns < 1) throw IllegalArgumentException()

        this.matrixRows = matrixRows
        this.matrixColumns = matrixColumns

        dot = skin.getRegion("dot")

        backgroundFont = skin.get("OpenSans-Large", BitmapFont::class.java)

        for (row in 0 until matrixRows) {
            for (col in 0 until matrixColumns) {
                val label1 = Label("", skin, "OpenSans-Entry")

                label1.setAlignment(Align.center)
                add(label1).width(entryWidth).height(entryHeight)
            }
            row()
        }
    }

    /**
     * Get the index of the label. If matrix does not contain the label
     * a [IllegalArgumentException] is thrown.
     */
    fun getIndex(label: Label): Int {
        var i = 0
        var found = false
        for (actor in children) {
            if (actor == label) {
                found = true
                break
            }
            i++
        }
        if (!found) throw IllegalArgumentException()
        return i
    }

    fun getRow(label: Label): Int {
        val index = getIndex(label)
        return index / matrixColumns
    }

    fun getColumn(label: Label): Int {
        val index = getIndex(label)
        return index % matrixColumns
    }

    fun set(index: Int, value: Any) {
        if (index < 0) throw IllegalArgumentException()

        val actor = cells.get(index).actor

        if (actor is Label) {
            actor.isVisible = true
            actor.setText(value.toString())
        }
    }

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
     * */
    override fun set(row: Int, col: Int, value: Any) {
        if (row < 0 || col < 0) throw IllegalArgumentException()

        val actor = cells.get(row * columns + col).actor

        if (actor is Label) {
            actor.isVisible = true
            actor.setText(value.toString())
        }
    }

    /**
     * Get the string stored in the indicated position.
     *
     * (0,0) is top left
     *
     * @param row the row of the entry
     * @param col the column of the entry
     */
    override fun get(row: Int, col: Int): String {
        if (row < 0 || col < 0) throw IllegalArgumentException()

        val actor = cells.get(row * columns + col).actor
        if (actor is Label)
            return actor.text.toString()
        return actor.toString()
    }

    /**
     * Get the cell of indicated position.
     *
     * (0,0) is top left
     *
     * @param row the row of the entry
     * @param col the column of the entry
     */
    override fun getCell(row: Int, col: Int): Cell<Label>? {
        if (row < 0 || col < 0) throw IllegalArgumentException()

        val cell = cells.get(row * columns + col)
        if (cell.actor != null && cell.actor is Label)
            return cell as Cell<Label>
        return null
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (mustPack) {
            pack()
            mustPack = false
        }

        // draw background text
        if (backgroundText.length > 0) {
            backgroundFont!!.color = backgroundTextColor
            val matrixCenter = localToStageCoordinates(Vector2(width / 2f, height / 2f))

            val x = matrixCenter.x - backgroundTextWidth / 2f
            val y = matrixCenter.y + backgroundTextHeight / 2f
            backgroundFont!!.draw(batch, backgroundText, x, y)
        }

        super.draw(batch, parentAlpha)

        // draw outlines
        if (!drawOutlines) return
        batch!!.color = Color.BLACK

        // left vertical outline
        val pos = localToStageCoordinates(Vector2(0f, 0f))
        val x = round(pos.x)
        val y = round(pos.y)
        val width = round(width)
        val height = round(height)
        val outlineThickness = round(outlineThickness)

        // left vertical outline
        filledRectangle(batch, x, y, outlineThickness, height)
        // left bottom horizontal outline
        filledRectangle(batch, x, y, outlineThickness * 5, outlineThickness)
        // left top horizontal outline
        filledRectangle(batch, x, y + height - outlineThickness, outlineThickness * 5, outlineThickness)
        // right vertical outline
        filledRectangle(batch, x + width - outlineThickness, y, outlineThickness, height)
        // left bottom horizontal outline
        filledRectangle(batch, x + width - outlineThickness - outlineThickness * 4, y, outlineThickness * 5, outlineThickness)
        // left top horizontal outline
        filledRectangle(batch, x + width - outlineThickness - outlineThickness * 4,
                y + height - outlineThickness, outlineThickness * 5, outlineThickness)

    }

    private fun filledRectangle(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        batch.draw(dot, round(x), round(y), round(width), round(height))
    }

    private fun round(a: Float) = Math.floor(a.toDouble()).toFloat()

    override fun get(entry: Int): String {
        val label = cells.get(entry).actor as Label
        return label.text.toString()
    }

    override fun size(): Int {
        return cells.size
    }

    override fun clearListeners() {
        super.clearListeners()
        for (cell in cells) {
            cell.actor.clearListeners()
        }
    }

}
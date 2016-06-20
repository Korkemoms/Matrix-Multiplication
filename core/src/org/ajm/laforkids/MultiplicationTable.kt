package org.ajm.laforkids

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import java.util.*

open class MultiplicationTable : IMultiplicationTable, Table {

    // i don't know why i had to redeclare these values
    override val matrixLeft: Matrix
    override val matrixProduct: Matrix
    override val matrixRight: Matrix
    override val matrixAnswers: Matrix
    override val equationLabel: Label
    override val rowsLeft: Int
    override val columnsLeft: Int
    override val rowsRight: Int
    override val columnsRight: Int
    override val answerAlternatives: Int

    /** The height of each entry, in all matrices and also the answer vector. May be overridden by
     * accessing the individual matrices and could therefore be incorrect. */
    override var entryHeight = 0f
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
    override var entryWidth = 0f
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
    override var matrixInsidePad = 0f
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
    override var matrixOutsidePad = 0f
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
    override var matrixEntryPad = 0f
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
        if (rowsLeft < 1) throw IllegalArgumentException()
        if (columnsLeft < 1) throw IllegalArgumentException()
        if (columnsRight < 1) throw IllegalArgumentException()
        if (answerAlternatives < 1) throw IllegalArgumentException()




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

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (mustPack) {
            pack()
            mustPack = false
        }
        super.draw(batch, parentAlpha)
    }

}
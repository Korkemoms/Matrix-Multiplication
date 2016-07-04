package org.ajm.laforkids.actors

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import org.ajm.laforkids.actors.ColoredMultiplicationTable
import org.ajm.laforkids.actors.VisualizerFactory
import org.ajm.laforkids.actors.IMultiplicationTable

/**
 * Also displays some additional information in the free top left space.
 */
class VisualizedMultiplicationTable : ColoredMultiplicationTable {

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

    }

    override fun prepareTopLeftActor(): Actor {
        val actor = VisualizerFactory().create(skin, this)
        return actor
    }

    override fun init(copyFrom: IMultiplicationTable) {
        super<ColoredMultiplicationTable>.init(copyFrom)
        notifyChangeListeners()
    }

    override fun init(min: Int, max: Int) {
        super<ColoredMultiplicationTable>.init(min, max)
        notifyChangeListeners()
    }


}
package org.ajm.laforkids.actors

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

/**
 * Creates appropriate visualizer for given multiplication table.
 */
class VisualizerFactory {

    /**
     * Creates appropriate visualizer for given multiplication table.
     */
    fun create(skin: Skin, multiplicationTable: MultiplicationTable): Actor {
        val left = multiplicationTable.matrixLeft
        val leftEntries = left.matrixColumns * left.matrixRows

        val right = multiplicationTable.matrixRight
        val rightEntries = right.matrixColumns * right.matrixRows

        val product = multiplicationTable.matrixProduct
        val productEntries = product.matrixColumns * product.matrixRows

        if (productEntries == 2 && (leftEntries == 2 || rightEntries == 2))
            return Visualizer2D(skin, multiplicationTable)

        // default is just a label
        val label = Label("AB=C", skin)
        label.touchable = Touchable.disabled
        label.setAlignment(Align.center)

        return label
    }
}
package org.ajm.laforkids.desktop

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import org.ajm.laforkids.ColoredMultiplicationTable
import org.ajm.laforkids.IMultiplicationTable
import org.ajm.laforkids.MultiplicationTable
import java.util.*

class MultiplicationTableFactory<T>(val klass: Class<T>) {

    /**
     * Create a [IMultiplicationTable] of class T with random specifications.
     */
    fun create(skin: Skin): IMultiplicationTable {

        val a = klass.isAssignableFrom(ColoredMultiplicationTable::class.java)
        val b = klass.isAssignableFrom(MultiplicationTable::class.java)

        val random = Random()

        if (b) {
            return MultiplicationTable(skin, random.nextInt(100) + 1, random.nextInt(100) + 1, random.nextInt(100) + 1, random.nextInt(100) + 1)
        } else if (a) {
            return ColoredMultiplicationTable(skin, random.nextInt(100) + 1, random.nextInt(100) + 1, random.nextInt(100) + 1, random.nextInt(100) + 1)
        }

        throw IllegalArgumentException()
    }

    /**
     * Create a [IMultiplicationTable] of class T with given specifications.
     */
    fun create(skin: Skin, rowsLeft: Int, columnsLeft: Int, columnsRight: Int, answerAlternatives: Int): IMultiplicationTable {

        val a = klass.isAssignableFrom(ColoredMultiplicationTable::class.java)
        val b = klass.isAssignableFrom(MultiplicationTable::class.java)

        if (b) {
            return MultiplicationTable(skin, rowsLeft, columnsLeft, columnsRight, answerAlternatives)
        } else if (a) {
            return ColoredMultiplicationTable(skin, rowsLeft, columnsLeft, columnsRight, answerAlternatives)
        }


        throw IllegalArgumentException()
    }
}
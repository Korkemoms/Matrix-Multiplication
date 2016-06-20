package org.ajm.laforkids.desktop

import org.ajm.laforkids.ColoredMultiplicationTable
import org.ajm.laforkids.IMultiplicationTable
import org.ajm.laforkids.MultiplicationTable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

/**
 * Test the properties of [MultiplicationTable] and [ColoredMultiplicationTable] defined in [IMultiplicationTable].
 * Each test function is run two times, once where the factory generates [MultiplicationTable],
 * and once where the factory generates [ColoredMultiplicationTable].
 */
@RunWith(Parameterized::class)
class MultiplicationTableTest(val factory: MultiplicationTableFactory<*>) {

    val random = Random()

    /**
     * Gives the test functions access to a [MultiplicationTableFactory].
     */
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {

            val factory1 = MultiplicationTableFactory<MultiplicationTable>(MultiplicationTable::class.java) as Any
            val factory2 = MultiplicationTableFactory<ColoredMultiplicationTable>(ColoredMultiplicationTable::class.java) as Any

            return listOf(arrayOf(factory1), arrayOf(factory2))
        }
    }


    /**
     * Test that the variables reject values that make no sense.
     */
    @Test
    fun illegalArguments1() {
        val table = factory.create(DesktopLauncher.skin!!)

        assertIllegalArgumentExceptionThrown({
            table.entryHeight = -(random.nextFloat() + 0.00001f) * 100f
        })
        assertIllegalArgumentExceptionThrown({
            table.entryWidth = -(random.nextFloat() + 0.00001f) * 100f
        })
        assertIllegalArgumentExceptionThrown({
            table.matrixInsidePad = -(random.nextFloat() + 0.00001f) * 100f
        })
        assertIllegalArgumentExceptionThrown({
            table.matrixOutsidePad = -(random.nextFloat() + 0.00001f) * 100f
        })
        assertIllegalArgumentExceptionThrown({
            table.matrixEntryPad = -(random.nextFloat() + 0.00001f) * 100f
        })

    }

    /**
     * Test that the constructor reject values that make no sense.
     */
    @Test
    fun illegalArguments2() {
        val skin = DesktopLauncher.skin!!


        for (i in 0 until 100) {
            val rowsLeft = 50 - random.nextInt(100)
            val columnsLeft = 50 - random.nextInt(100)
            val columnsRight = 50 - random.nextInt(100)
            val answerAlternatives = 50 - random.nextInt(100)

            if (rowsLeft < 1 || columnsLeft < 1 || columnsRight < 1 || answerAlternatives < 1) {
                assertIllegalArgumentExceptionThrown({
                    factory.create(skin, rowsLeft, columnsLeft, columnsRight, answerAlternatives)
                })
            }
        }
    }

    /**
     * Test that the copy function reject values that make no sense.
     */
    @Test
    fun illegalArguments3() {
        val skin = DesktopLauncher.skin!!

        // make two incompatible tables
        var rowsLeft = random.nextInt(100) + 1
        var columnsLeft = random.nextInt(100) + 1
        var columnsRight = random.nextInt(100) + 1
        var answerAlternatives = random.nextInt(100) + 1

        val table1 = factory.create(skin, rowsLeft, columnsLeft, columnsRight, answerAlternatives)

        while (rowsLeft == table1.rowsLeft && columnsLeft == table1.columnsLeft &&
                columnsRight == table1.columnsRight) {

            rowsLeft = random.nextInt(100) + 1
            columnsLeft = random.nextInt(100) + 1
            columnsRight = random.nextInt(100) + 1
            answerAlternatives = random.nextInt(100) + 1

        }

        val table2 = factory.create(skin, rowsLeft, columnsLeft, columnsRight, answerAlternatives)

        assertIllegalArgumentExceptionThrown({ table1.copyEntries(table2) })
    }

    @Test
    fun setGet() {
        val skin = DesktopLauncher.skin!!

        val rowsLeft = random.nextInt(100) + 1
        val columnsLeft = random.nextInt(100) + 1
        val columnsRight = random.nextInt(100) + 1
        val answerAlternatives = random.nextInt(100) + 1

        val multiplicationTable = factory.create(skin,
                rowsLeft, columnsLeft, columnsRight, answerAlternatives)

        // test constructor
        Assert.assertEquals(rowsLeft, multiplicationTable.rowsLeft)
        Assert.assertEquals(columnsLeft, multiplicationTable.columnsLeft)
        Assert.assertEquals(columnsRight, multiplicationTable.columnsRight)
        Assert.assertEquals(answerAlternatives, multiplicationTable.answerAlternatives)


        // test set get
        val entryHeight = random.nextFloat() * 100f
        val entryWidth = random.nextFloat() * 100f
        val matrixInsidePad = random.nextFloat() * 100f
        val matrixOutsidePad = random.nextFloat() * 100f
        val matrixEntryPad = random.nextFloat() * 100f

        multiplicationTable.entryHeight = entryHeight
        multiplicationTable.entryWidth = entryWidth
        multiplicationTable.matrixInsidePad = matrixInsidePad
        multiplicationTable.matrixOutsidePad = matrixOutsidePad
        multiplicationTable.matrixEntryPad = matrixEntryPad

        Assert.assertEquals(entryHeight, multiplicationTable.entryHeight)
        Assert.assertEquals(entryWidth, multiplicationTable.entryWidth)
        Assert.assertEquals(matrixInsidePad, multiplicationTable.matrixInsidePad)
        Assert.assertEquals(matrixOutsidePad, multiplicationTable.matrixOutsidePad)
        Assert.assertEquals(matrixEntryPad, multiplicationTable.matrixEntryPad)
    }

    @Test
    fun copyEntries() {
        val skin = DesktopLauncher.skin!!

        // make two compatible tables
        val rowsLeft = random.nextInt(100) + 1
        val columnsLeft = random.nextInt(100) + 1
        val rowsRight = columnsLeft
        val columnsRight = random.nextInt(100) + 1
        val answerAlternatives = random.nextInt(100) + 1

        val table1 = factory.create(skin,
                rowsLeft, columnsLeft, columnsRight, answerAlternatives)
        val table2 = factory.create(skin,
                rowsLeft, columnsLeft, columnsRight, answerAlternatives)


        // test the copy functionality
        table1.copyEntries(table2)

        for (row in 0 until  rowsLeft) {
            for (col in 0 until  columnsLeft) {
                val actual = table1.matrixLeft.get(row, col)
                val expected = table2.matrixLeft.get(row, col)
                Assert.assertEquals(expected, actual)
            }
        }

        for (row in 0 until  rowsLeft) {
            for (col in 0 until  columnsRight) {
                val actual = table1.matrixProduct.get(row, col)
                val expected = table2.matrixProduct.get(row, col)
                Assert.assertEquals(expected, actual)
            }
        }

        for (row in 0 until  rowsRight) {
            for (col in 0 until  columnsRight) {
                val actual = table1.matrixRight.get(row, col)
                val expected = table2.matrixRight.get(row, col)
                Assert.assertEquals(expected, actual)
            }
        }

    }
}
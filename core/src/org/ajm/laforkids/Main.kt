package org.ajm.laforkids

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.ajm.laforkids.actors.VisualizedMultiplicationTable
import org.jrenner.smartfont.SmartFontGenerator
import java.util.*

class Main {

    companion object {
        val showStackTrace = false
        fun log(e: Exception) {
            if (showStackTrace)
                Gdx.app.log("Exception", e.toString() + "\n" + Arrays.toString(e.stackTrace).replace(",", "\n"))
            else
                Gdx.app.log("Exception", e.toString())

            //throw e
        }

        fun log(s: String) {
            Gdx.app.log("Log", s)
        }
    }

    // hardcoded parameters
    val entryPad = 0f
    val screenFill = 1f // 1 = 100%
    val outlineThickness = 5f // just a factor
    val selectionColor: Color = Color.valueOf("ED7D31")
    val interpolationMethod: Interpolation = Interpolation.pow3Out
    val interpolationTime = 0.5f


    val stage = Stage(ScreenViewport())
    var multiplicationTable: VisualizedMultiplicationTable? = null
    var menu: Menu? = null
    var helpFrame: ScrollPane? = null
    val skin = Skin()
    val settings = Settings("Matrix Multiplication")
    val gameIterator = GameIterator(settings)
    var entryFont: BitmapFont? = null
    var font_large: BitmapFont? = null
    var defaultFont: BitmapFont? = null


    private val generator = SmartFontGenerator(Gdx.files.internal("OpenSans.ttf"))
    private val generatorDigits = SmartFontGenerator(Gdx.files.internal("OpenSans-Digits.ttf"))
    private val generatorABC = SmartFontGenerator(Gdx.files.internal("OpenSans-ABC.ttf"))
    private var firstInit = true

    private val stressTester: StressTester

    constructor() {
        Gdx.input.inputProcessor = stage
        Gdx.graphics.isContinuousRendering = false
        Gdx.graphics.requestRendering()


        skin.addRegions(TextureAtlas(Gdx.files.internal("gdx-skins-master/kenney-pixel/custom-skin/skin.atlas")))

        init(true, true)

        //stage!!.setDebugAll(true)
        stressTester = StressTester(stage)
        stressTester.active = false
    }

    fun resize() {
        init(false, true)
    }

    fun init(newGame: Boolean, resize: Boolean) {
        Gdx.graphics.requestRendering()


        stage.viewport.update(Gdx.graphics.width, Gdx.graphics.height, true)
        stage.clear()

        val gl = gameIterator.gameLogic
        if (newGame) gameIterator.newGame()

        // determine some visual details
        val columns = gl.columnsLeft + gl.columnsRight
        val rows = gl.rowsLeft + gl.columnsLeft + 1

        var outlineThickness = this.outlineThickness * (2f +
                6f * Math.min(Gdx.graphics.width, Gdx.graphics.height) / (1000f)).toInt().toFloat()
        outlineThickness /= Math.max(columns, rows).toFloat()

        val matrixInsidePad = 3 * outlineThickness
        val matrixOutsidePad = outlineThickness
        val pad = matrixInsidePad + matrixOutsidePad

        val entryWidth = (Gdx.graphics.width.toFloat() * screenFill - pad * 4 - columns * entryPad * 2) /
                Math.max(columns, gl.answerAlternatives).toFloat()
        val entryHeight = (Gdx.graphics.height.toFloat() * screenFill - pad * 6 - rows * entryPad * 2) / rows.toFloat()


        // prepare entry font
        if (entryFont != null) {
            skin.remove("OpenSans-Entry", entryFont!!.javaClass)
            entryFont!!.dispose()
        }
        var size = (Math.max(Math.min(entryHeight, entryWidth) / 1.8f, 8f)).toInt()
        entryFont = generatorDigits.createFont(Gdx.files.internal("OpenSans-Regular-Digits.ttf"), "OpenSans-Entry", size)
        skin.add("OpenSans-Entry", entryFont!!, entryFont!!.javaClass)


        // prepare default font
        if (resize) {
            if (defaultFont != null) {
                skin.remove("default", BitmapFont::class.java)
                defaultFont!!.dispose()
            }

            size = (15f + 0.5f * Math.sqrt(Math.sqrt(Gdx.graphics.density.toDouble())) * Math.min(Gdx.graphics.width, Gdx.graphics.height) / 10f).toInt()
            defaultFont = generator.createFont(Gdx.files.internal("OpenSans.ttf"), "default", size)
            skin.add("default", defaultFont!!, defaultFont!!.javaClass)
        }


        // prepare large font
        size = 250
        if (firstInit) {
            font_large = generatorABC.createFont(Gdx.files.internal("OpenSans-ABC.ttf"), "OpenSans-Large", size)
            skin.add("OpenSans-Large", font_large!!, font_large!!.javaClass)
        }
        var min = Math.min(gl.rowsLeft * entryHeight, gl.columnsLeft * entryWidth)
        min = Math.min(min, gl.columnsRight * entryWidth)
        min = Math.min(min, gl.columnsLeft * entryHeight)
        font_large!!.data.setScale((min + pad) / size)


        // skin must be reloaded to include the new fonts
        skin.load(Gdx.files.internal("gdx-skins-master/kenney-pixel/custom-skin/skin.json"))


        // add menu
        if (resize) menu = Menu(stage, skin)
        menu!!.clearMenuItemListeners()
        stage.addActor(menu)
        menu!!.isVisible = true
        menu!!.setPosition(0f, stage.height - menu!!.height)


        // prepare a new multiplication table
        multiplicationTable = VisualizedMultiplicationTable(skin,
                gl.rowsLeft, gl.columnsLeft, gl.columnsRight, gl.answerAlternatives)
        multiplicationTable!!.interpolationMethod = interpolationMethod
        multiplicationTable!!.interpolationTime = interpolationTime
        multiplicationTable!!.entryWidth = entryWidth
        multiplicationTable!!.entryHeight = entryHeight
        multiplicationTable!!.matrixInsidePad = matrixInsidePad
        multiplicationTable!!.matrixOutsidePad = matrixOutsidePad
        multiplicationTable!!.selectionColor = selectionColor
        multiplicationTable!!.outlineThickness = outlineThickness
        multiplicationTable!!.matrixEntryPad = entryPad
        multiplicationTable!!.setFillParent(true)
        multiplicationTable!!.matrixAnswers.entryWidth = Gdx.graphics.width / gl.answerAlternatives.toFloat()
        multiplicationTable!!.matrixAnswers.entryPad = 0f
        stage.addActor(multiplicationTable!!)


        // connect the game logic to the new MultiplicationTable
        gameIterator.init(multiplicationTable!!, this, newGame)


        // add settings functionality
        menu!!.settingsLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (stressTester.active) return

                // hide everything else
                menu!!.hideMenu()
                menu!!.hideButton()
                multiplicationTable!!.isVisible = false

                // prepare settings interface
                val settings = SettingsInterface(this@Main)
                settings.onSaved = Runnable { init(true, false) }
                settings.onCancel = Runnable {
                    stage.actors.removeValue(settings, true)
                    multiplicationTable!!.isVisible = true
                    menu!!.isVisible = true
                }

                // show settings interface
                stage.addActor(settings)
                settings.setFillParent(true)
            }
        })

        firstInit = false
    }

    /**
     * Show a message on the bottom of the screen. It is removed the next time the user clicks anywhere.
     */
    fun showMessage(message: String, buttonText: String = "Ok", onOk: () -> Unit = {}) {
        // remove previous just in case
        stage.actors.removeValue(helpFrame, true)

        Gdx.app.postRunnable {
            // prepare table with message and a button that does nothing
            val label = Label(message, skin)
            val button = Label(buttonText, skin)
            val table = Table()
            table.add(label).row()
            table.add(button)
            table.background = skin.getDrawable("dot")

            // put it in ScrollPane in case the message is too big
            helpFrame = ScrollPane(table)

            // add to the bottom of the stage
            stage.addActor(helpFrame)
            helpFrame!!.setOverscroll(false, false)
            helpFrame!!.setPosition(0f, 0f)
            helpFrame!!.height = multiplicationTable!!.matrixAnswers.height
            helpFrame!!.width = stage.width

            // when user clicks anywhere remove the message
            stage.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    stage.actors.removeValue(helpFrame, true)
                    stage.removeListener(this)
                    onOk.invoke()
                }
            })
        }
    }

    fun render() {
        try {
            stressTester.invoke()

            Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

            stage.act()
            stage.draw()
        } catch(exception: Exception) {
            dispose()
            throw exception
        }
    }

    fun dispose() {
        _try { stage.dispose() }
        _try { skin.dispose() }
        _try { entryFont!!.dispose() }
        _try { font_large!!.dispose() }
        _try { defaultFont!!.dispose() }
        _try { generator.dispose() }
        _try { generatorDigits.dispose() }
        _try { generatorABC.dispose() }
    }

    fun _try(inline: () -> Unit) {
        try {
            inline.invoke()
        } catch (e: Exception) {
            // ignore
        }
    }
}
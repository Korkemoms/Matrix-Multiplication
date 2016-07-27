package org.ajm.laforkids

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.ajm.laforkids.actors.*
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

    val palette = Array<Color>()

    init {
        palette.add(Color.valueOf("#70AD47"))
        palette.add(Color.valueOf("#4472C4"))
        palette.add(Color.valueOf("#FFC000"))
        palette.add(Color.valueOf("#A5A5A5"))
        palette.add(Color.valueOf("#ED7D31"))
        palette.add(Color.valueOf("#5B9BD5"))
        palette.add(Color.valueOf("#44546A"))
        palette.add(Color.valueOf("#44546A"))
        palette.add(Color.valueOf("#E7E6E6"))
        palette.add(Color.valueOf("#000000"))
        palette.add(Color.valueOf("#FFFFFF"))
    }


    // hardcoded parameters
    val selectionColor: Color = Color(palette[4])
    val topBarBackgroundColor: Color = Color(palette[2])
    val backgroundColor: Color = Color(palette[10])
    val matrixBackgroundTextColor: Color = Color(palette[8])
    val fontColor: Color = Color(palette[9])
    val menuBackgroundColor: Color = Color(palette[2])
    val keyboardBackgroundColor: Color = Color(palette[0]).lerp(Color.WHITE, 0.5f)

    val entryPad = 0f
    val screenFill = 1f // 1 = 100%
    val outlineThickness = 5f // just a factor
    val interpolationMethod: Interpolation = Interpolation.pow3Out // used for most animations
    val interpolationTime = 0.5f // used for most animations


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
    var scoreLabel: ScoreLabel? = null
    var keypad: Keypad

    private val generator = SmartFontGenerator(Gdx.files.internal("OpenSans.ttf"))
    private val generatorDigits = SmartFontGenerator(Gdx.files.internal("OpenSans-Digits.ttf"))
    private val generatorABC = SmartFontGenerator(Gdx.files.internal("OpenSans-ABC.ttf"))


    private var firstInit = true

    val stressTest = false
    private val stressTester: StressTester

    constructor() {

        Gdx.input.inputProcessor = stage
        Gdx.graphics.isContinuousRendering = false
        Gdx.graphics.requestRendering()


        skin.addRegions(TextureAtlas(Gdx.files.internal("gdx-skins-master/kenney-pixel/custom-skin/skin.atlas")))

        init(true, true)


        stressTester = StressTester(stage, this)
        stressTester.active = stressTest
        if (stressTest) {
            settings.maxColumnsLeft = 20
            settings.maxRowsLeft = 20
            settings.maxColumnsRight = 20
            // do not save
        }
        keypad = Keypad(skin)
        keypad.color.set(keyboardBackgroundColor)
        for (child in keypad.children) {
            if (child !is TextButton) continue
            child.label.style.fontColor = fontColor
        }
        keypad.interpolationMethod = interpolationMethod
        keypad.interpolationTime = interpolationTime
    }

    fun resize(width: Int = Gdx.graphics.width, height: Int = Gdx.graphics.height) {
        init(false, true, width, height)
    }

    fun init(newGame: Boolean, resize: Boolean, width: Int = Gdx.graphics.width, height: Int = Gdx.graphics.height) {
        Gdx.graphics.requestRendering()

        if (newGame) gameIterator.newGame()

        // to update the fonts everything is rebuilt
        stage.viewport.update(width, height, true)
        stage.clear()

        // prepare default font
        var size = (10f + 0.5f * Math.sqrt(Math.sqrt(Gdx.graphics.density.toDouble())) * Math.min(width, height) / 10f).toInt()

        if (resize) {
            if (defaultFont != null) {
                skin.remove("default", BitmapFont::class.java)
                defaultFont!!.dispose()
            }

            defaultFont = generator.createFont(Gdx.files.internal("OpenSans.ttf"), "default", size)
            skin.add("default", defaultFont!!, defaultFont!!.javaClass)
        }

        // determine some visual details
        val padTop = defaultFont!!.capHeight * 2f
        val tableHeight = (height - padTop).toInt()

        val gl = gameIterator.gameLogic
        val columns = gl.columnsLeft + gl.columnsRight
        val rows = gl.rowsLeft + gl.columnsLeft + 1

        var outlineThickness = this.outlineThickness * (2f +
                6f * Math.min(width, tableHeight) / (1000f)).toInt().toFloat()
        outlineThickness /= Math.max(columns, rows).toFloat()

        val matrixInsidePad = 3 * outlineThickness
        val matrixOutsidePad = outlineThickness
        val pad = matrixInsidePad + matrixOutsidePad

        val entryWidth = (width.toFloat() * screenFill - pad * 4 - columns * entryPad * 2) /
                Math.max(columns, gl.answerAlternatives).toFloat()
        val entryHeight = (tableHeight * screenFill - pad * 6 - rows * entryPad * 2) / rows.toFloat()


        // prepare entry font
        if (entryFont != null) {
            skin.remove("OpenSans-Entry", entryFont!!.javaClass)
            entryFont!!.dispose()
        }
        size = (Math.max(Math.min(entryHeight, entryWidth) / 1.8f, 8f)).toInt()
        entryFont = generatorDigits.createFont(Gdx.files.internal("OpenSans-Regular-Digits.ttf"), "OpenSans-Entry", size)
        skin.add("OpenSans-Entry", entryFont!!, entryFont!!.javaClass)


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
        multiplicationTable!!.padTop(padTop)
        multiplicationTable!!.padBottom(height / 3f)
        multiplicationTable!!.align(Align.bottom)
        multiplicationTable!!.matrixAnswers.entryWidth = width / gl.answerAlternatives.toFloat()
        multiplicationTable!!.matrixAnswers.entryPad = 0f
        multiplicationTable!!.setMatrixBackgroundTextColor(matrixBackgroundTextColor)

        // put in scrollPane so when soft keyboard is visible one can scroll to view what is under it
        val scrollPane = object : ScrollPane(multiplicationTable!!) {
            val dot = skin.getDrawable("dot")
            override fun draw(batch: Batch?, parentAlpha: Float) {
                batch as Batch
                batch.color = backgroundColor
                dot.draw(batch, x, y, width.toFloat(), height.toFloat())
                super.draw(batch, parentAlpha)
            }
        }
        stage.addActor(scrollPane)
        scrollPane.setFillParent(true)
        scrollPane.setScrollingDisabled(true, false)


        // prepare top bar
        val topBar = Table()
        topBar.background = skin.getDrawable("dot")
        topBar.color.set(topBarBackgroundColor)
        stage.addActor(topBar)

        // add menu
        if (resize) menu = Menu(stage, skin)
        menu!!.setTextColor(fontColor)
        menu!!.clearMenuItemListeners()
        topBar.add(menu).width(width * 0.5f)
        menu!!.isVisible = true
        menu!!.menuBackgroundColor = menuBackgroundColor
        menu!!.interpolationMethod = interpolationMethod
        menu!!.interpolationTime = interpolationTime

        // add score display
        scoreLabel = ScoreLabel(skin, gameIterator.gameLogic.score)
        topBar.add(scoreLabel).width(width * (0.49f)).padRight(width * 0.01f)
        scoreLabel!!.setAlignment(Align.right)
        scoreLabel!!.interpolationTime = interpolationTime
        scoreLabel!!.interpolationMethod = interpolationMethod
        scoreLabel!!.style.fontColor = fontColor

        topBar.pack()
        topBar.setPosition(0f, stage.height - topBar.height)


        // add settings functionality
        menu!!.settingsLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (stressTester.active) return

                // hide everything else
                menu!!.hideMenu()
                multiplicationTable!!.isVisible = false
                topBar.isVisible = false

                // prepare settings interface
                val settings = SettingsInterface(this@Main)
                settings.setFontColor(fontColor)
                settings.onSaved = Runnable { init(true, false) }
                settings.onCancel = Runnable {
                    stage.actors.removeValue(settings, true)
                    multiplicationTable!!.isVisible = true
                    topBar.isVisible = true
                }

                // show settings interface
                stage.addActor(settings)
                settings.setFillParent(true)
            }
        })


        // connect the game logic to the new MultiplicationTable
        gameIterator.init(multiplicationTable!!, this, newGame)


        multiplicationTable!!.updateTopLeftActorSize()

        keypad = Keypad(skin)
        keypad.color.set(keyboardBackgroundColor)
        for (child in keypad.children) {
            if (child !is TextButton) continue
            child.label.style.fontColor = fontColor
        }
        keypad.interpolationMethod = interpolationMethod
        keypad.interpolationTime = interpolationTime

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

            // scroll to the right in case the message is to big
            val interpolationTime = interpolationTime * 2 * (label.width / helpFrame!!.width)
            animate({ lerp -> helpFrame!!.scrollPercentX = lerp }, interpolationMethod, interpolationTime)

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
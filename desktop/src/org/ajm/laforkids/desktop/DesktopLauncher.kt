package org.ajm.laforkids.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import org.ajm.laforkids.Adapter
import org.ajm.laforkids.Main
import org.junit.runner.Description
import org.junit.runner.JUnitCore
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import java.util.*

/**
 * Created by Andreas on 17.06.2016.
 */

object DesktopLauncher {

    var skin: Skin? = null

    val testClasses: com.badlogic.gdx.utils.Array<Class<*>> = com.badlogic.gdx.utils.Array<Class<*>>()

    init {
        testClasses.add(SettingsTest::class.java)
        testClasses.add(MatrixTest::class.java)
        testClasses.add(MultiplicationTableTest::class.java)
        testClasses.add(GameLogicTest::class.java)

    }

    fun allTests(main: Main) {
        skin = main.skin

        val core = JUnitCore()

        core.addListener(object : RunListener() {
            override fun testFinished(description: Description?) {
                println(description!!.className + ": " + description.methodName)
            }
        })

        val failures = ArrayList<Failure>()
        var total = 0

        for (klass in testClasses) {
            val results = core.run(klass)
            failures.addAll(results.failures)
            total += results.runCount
        }

        println(total.toString() + " tests have been run.\n" + failures.size.toString() + " tests failed:")

        for (failure in failures) {
            println(failure.message)
            println(failure.trace)

        }
    }


    @JvmStatic fun main(arg: Array<String>) {

        //TexturePacker.process("gdx-skins-master\\kenney-pixel\\custom-raw",
        //	"gdx-skins-master\\kenney-pixel\\custom-skin", "skin");

        val config = LwjglApplicationConfiguration()
        val adapter = Adapter()
        LwjglApplication(adapter, config)

        Gdx.app.postRunnable({
            allTests(adapter.main!!)
        })

    }
}


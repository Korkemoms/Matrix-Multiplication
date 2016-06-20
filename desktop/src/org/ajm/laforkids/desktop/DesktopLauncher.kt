package org.ajm.laforkids.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import org.ajm.laforkids.Adapter
import org.ajm.laforkids.Main
import org.junit.runner.JUnitCore
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import java.util.*

/**
 * Created by Andreas on 17.06.2016.
 */

object DesktopLauncher {

    var skin: Skin? = null

    fun allTests(main: Main) {
        skin = main.skin

        val core = JUnitCore()

        val failures = ArrayList<Failure>()
        var total = 0

        var results = core.run(SettingsTest::class.java)
        failures.addAll(results.failures)
        total += results.runCount

        results = core.run(MatrixTest::class.java)
        failures.addAll(results.failures)
        total += results.runCount

        results = core.run(MultiplicationTableTest::class.java)
        failures.addAll(results.failures)
        total += results.runCount


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


package org.ajm.laforkids

import com.badlogic.gdx.ApplicationAdapter

class Adapter : ApplicationAdapter {

    var main: Main? = null


    constructor() {
    }


    override fun create() {
        main = Main()
    }

    override fun resize(width: Int, height: Int) {
        main!!.resize()
    }

    override fun render() {
        main!!.render()
    }

    override fun dispose() {
        main!!.dispose()
    }
}

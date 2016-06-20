package org.ajm.laforkids.desktop

import org.junit.Assert


fun assertIllegalArgumentExceptionThrown(inlined: () -> Unit) {
    var illegal = false
    try {
        inlined.invoke()
    } catch(e: IllegalArgumentException) {
        illegal = true
    }
    Assert.assertTrue(illegal)
}
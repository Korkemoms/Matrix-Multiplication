package org.ajm.laforkids.desktop

import org.junit.Assert


fun assertExceptionThrown(inlined: () -> Unit) {
    var illegal = false
    try {
        inlined.invoke()
    } catch(e: Exception) {
        illegal = true
    }
    Assert.assertTrue(illegal)
}
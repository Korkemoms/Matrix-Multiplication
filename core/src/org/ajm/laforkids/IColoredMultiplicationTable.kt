package org.ajm.laforkids

/**
 * Contains 3 matrices that are set up to visualize a matrix multiplication.
 * Also contains a vector with answer alternatives.
 * Entries can be highlighted to help players.
 */
interface IColoredMultiplicationTable : IMultiplicationTable {
    var highlight: Boolean
    var highlightRow: Int
    var highlightCol: Int
}
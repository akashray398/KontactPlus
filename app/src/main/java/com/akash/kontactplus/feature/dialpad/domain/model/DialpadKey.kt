package com.akash.kontactplus.feature.dialpad.domain.model

/**
 * Represents a key on the dialpad.
 */
sealed class DialpadKey(val value: String, val letters: String = "") {
    data object One : DialpadKey("1")
    data object Two : DialpadKey("2", "ABC")
    data object Three : DialpadKey("3", "DEF")
    data object Four : DialpadKey("4", "GHI")
    data object Five : DialpadKey("5", "JKL")
    data object Six : DialpadKey("6", "MNO")
    data object Seven : DialpadKey("7", "PQRS")
    data object Eight : DialpadKey("8", "TUV")
    data object Nine : DialpadKey("9", "WXYZ")
    data object Star : DialpadKey("*")
    data object Zero : DialpadKey("0", "+")
    data object Hash : DialpadKey("#")

    companion object {
        val keys = listOf(
            One, Two, Three,
            Four, Five, Six,
            Seven, Eight, Nine,
            Star, Zero, Hash
        )
    }
}

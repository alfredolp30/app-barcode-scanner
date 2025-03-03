package com.alplabs.barcodescanner.barcode.scanner.text

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
interface TextPattern {
    val regexes: List<Regex>

    fun sanitizer(displayValue: String): String
    fun validate(value: String): Boolean
}
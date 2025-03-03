package com.alplabs.barcodescanner.barcode.scanner.text

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class BarcodeTextPattern: TextPattern {

    private val patterns = arrayOf(
        "^\\d{12}$",
    )

    override val regexes by lazy {
        patterns.map {
            Regex(
                pattern = it,
                options =  setOf(
                    RegexOption.MULTILINE,
                    RegexOption.DOT_MATCHES_ALL
                )
            )
        }
    }

    override fun sanitizer(displayValue: String): String {
        return regexes.mapNotNull {
            it.find(displayValue)?.groupValues?.firstOrNull()
        }.firstOrNull()?.filter { it.isDigit() } ?: ""
    }

    override fun validate(value: String): Boolean {
        return value.length == 12
    }

}
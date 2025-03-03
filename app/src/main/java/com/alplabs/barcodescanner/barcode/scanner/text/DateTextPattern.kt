package com.alplabs.barcodescanner.barcode.scanner.text

/**
 * Created by Alfredo Lima Porfirio on 13/03/23.
 */
class DateTextPattern: TextPattern {
    private val patterns = arrayOf(
        "^(([0-9]{2}\\/[0-9]{2}\\/[0-9]{4})$",
        "^([0-9]{2} [A-Z]{3} [0-9]{4}))$"
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
        }.firstOrNull() ?: ""
    }

    override fun validate(value: String): Boolean {
        return value.isNotEmpty()
    }

}
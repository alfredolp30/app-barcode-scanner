package com.alplabs.barcodescanner.barcode.scanner.invoice

/**
 * Created by Alfredo L. Porfirio on 2019-05-31.
 */
class InvoiceChecker(barcode: String) {
    private val containsOnlyDigits = barcode.all { it.isDigit() }
    private val isBarcode = barcode.length == 44 && containsOnlyDigits

    val isBarcodeCollection = isBarcode && barcode[0] == '8'

}
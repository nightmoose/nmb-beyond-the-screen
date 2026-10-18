package com.beyondthescreen.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

object QrCodes {
    fun matrix(content: String): BitMatrix =
        QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 0, 0)
}

@Composable
fun QrImage(content: String, modifier: Modifier = Modifier) {
    val matrix = remember(content) { QrCodes.matrix(content) }
    Canvas(modifier.background(Color.White)) {
        val cells = matrix.width
        val cell = size.minDimension / cells.toFloat()
        val left = (size.width - cell * cells) / 2f
        val top = (size.height - cell * cells) / 2f
        for (y in 0 until cells) {
            for (x in 0 until cells) {
                if (matrix.get(x, y)) {
                    drawRect(
                        color = Ink,
                        topLeft = Offset(left + x * cell, top + y * cell),
                        size = Size(cell + 0.5f, cell + 0.5f),
                    )
                }
            }
        }
    }
}

fun openAmazonSearch(context: Context, url: String) {
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No browser on this TV. Scan the code instead.", Toast.LENGTH_LONG).show()
    }
}

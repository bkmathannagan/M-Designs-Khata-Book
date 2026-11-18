package com.mdesigns.khata.core.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.net.URLEncoder

object UpiHelper {
    fun createUpiUriString(
        vpa: String,
        name: String,
        amount: Double,
        note: String = "Khata Settlement"
    ): String {
        val cleanVpa = vpa.trim()
        val formattedAmount = if (amount > 0) String.format(java.util.Locale.US, "%.2f", amount) else ""
        val encodedName = URLEncoder.encode(name.trim().ifEmpty { "M Designs Merchant" }, "UTF-8")
        val encodedNote = URLEncoder.encode(note.trim(), "UTF-8")
        return if (formattedAmount.isNotEmpty()) {
            "upi://pay?pa=$cleanVpa&pn=$encodedName&am=$formattedAmount&cu=INR&tn=$encodedNote"
        } else {
            "upi://pay?pa=$cleanVpa&pn=$encodedName&cu=INR&tn=$encodedNote"
        }
    }

    fun createUpiUri(
        vpa: String,
        name: String,
        amount: Double,
        note: String = "Khata Settlement"
    ): Uri {
        return Uri.parse(createUpiUriString(vpa, name, amount, note))
    }

    fun generateQrBitmap(content: String, sizePx: Int = 512): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.MARGIN, 1)
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
            }
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun launchUpiApp(context: Context, upiUriString: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUriString)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, "Pay via UPI App").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

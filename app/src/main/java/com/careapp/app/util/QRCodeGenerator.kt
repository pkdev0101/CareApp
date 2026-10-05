package com.careapp.app.util

import android.graphics.Bitmap
import android.graphics.Color
import com.careapp.app.data.SeniorProfile
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeGenerator {

    fun generateQRCode(data: String, size: Int = 400): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    fun generateEmergencyQRData(senior: SeniorProfile): String {
        return "CAREANCHOR_EMERGENCY\n" +
            "NAME:${senior.fullName}\n" +
            "PREF_NAME:${senior.preferredName}\n" +
            "CAREGIVER:${senior.primaryCaregiverName}\n" +
            "PHONE:${senior.primaryCaregiverPhone}\n" +
            "DOCTOR:${senior.doctorName}\n" +
            "DOCTOR_PHONE:${senior.doctorPhone}\n" +
            "HOSPITAL:${senior.preferredHospital}\n" +
            "ADDRESS:${senior.address}\n" +
            "ALLERGIES:${senior.allergies}\n" +
            "CONDITIONS:${senior.medicalConditions}\n" +
            "MEMORY:${senior.memoryNote}"
    }

    fun generateCompactQRData(senior: SeniorProfile): String {
        return "${senior.fullName}|${senior.primaryCaregiverName}|${senior.primaryCaregiverPhone}"
    }

    fun parseQRData(qrContent: String): Map<String, String>? {
        return try {
            if (!qrContent.startsWith("CAREANCHOR_EMERGENCY")) {
                return null
            }

            val lines = qrContent.split("\n").drop(1)
            val data = mutableMapOf<String, String>()

            lines.forEach { line ->
                val parts = line.split(":", limit = 2)
                if (parts.size == 2) {
                    data[parts[0]] = parts[1]
                }
            }

            data
        } catch (e: Exception) {
            null
        }
    }

    fun getQRCodeText(senior: SeniorProfile): String {
        return """
            📱 SCAN THIS QR CODE

            This QR code contains:
            • Senior's name
            • Caregiver contact
            • Doctor information
            • Medical conditions
            • Emergency instructions

            Display this QR code to emergency responders.
        """.trimIndent()
    }

    fun formatQRContent(senior: SeniorProfile, format: String = "full"): String {
        return when (format) {
            "basic" -> generateCompactQRData(senior)
            "full" -> generateEmergencyQRData(senior)
            else -> generateEmergencyQRData(senior)
        }
    }
}

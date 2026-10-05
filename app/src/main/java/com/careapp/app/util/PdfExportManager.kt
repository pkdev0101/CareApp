package com.careapp.app.util

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File

object PdfExportManager {
    fun createTextPdf(context: Context, fileName: String, title: String, content: String): File {
        val document = PdfDocument()
        val paint = Paint().apply { textSize = 11f; color = android.graphics.Color.DKGRAY }
        val titlePaint = Paint().apply { textSize = 18f; isFakeBoldText = true; color = android.graphics.Color.BLACK }
        val pageWidth = 612
        val pageHeight = 792
        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        var y = 42f
        canvas.drawText(title, 36f, y, titlePaint)
        y += 28f
        content.lines().forEach { line ->
            if (y > pageHeight - 40) {
                document.finishPage(page)
                pageNumber++
                page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = page.canvas
                y = 40f
            }
            canvas.drawText(line.take(105), 36f, y, paint)
            y += 16f
        }
        document.finishPage(page)
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        directory.mkdirs()
        val file = File(directory, fileName)
        file.outputStream().use { document.writeTo(it) }
        document.close()
        return file
    }

    fun sharePdf(context: Context, file: File, chooserTitle: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}

package com.osman.studentqr.util

import android.os.Environment
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.osman.studentqr.data.model.ReportData
import java.io.File
import java.io.FileOutputStream

class PdfCreator {
    val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
    val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
    private lateinit var pdf: PdfWriter

    private fun createFile(): File {
        //Prepare file
        val title = "yoklama raporu.pdf"
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, title)
        if (!file.exists()) file.createNewFile()
        return file
    }

    private fun addLineSpace(document: Document, number: Int) {
        for (i in 0 until number) {
            document.add(Paragraph(" "))
        }
    }

    private fun createDocument(): Document {
        //PDF dosyası olustur
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = PageSize.A4
        return document
    }

    private fun setupPdfWriter(document: Document, file: File) {
        pdf = PdfWriter.getInstance(document, FileOutputStream(file))
        pdf.setFullCompression()
        //Dosyayı ac
        document.open()
    }

    private fun createTable(column: Int, columnWidth: FloatArray): PdfPTable {
        val table = PdfPTable(column)
        table.widthPercentage = 100f
        table.setWidths(columnWidth)
        table.headerRows = 1
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        return table
    }

    private fun createParagraph(content: String): Paragraph {
        val paragraph = Paragraph(content, BODY_FONT)
        paragraph.firstLineIndent = 25f
        paragraph.alignment = Element.ALIGN_JUSTIFIED
        return paragraph
    }

    private fun createCell(content: String): PdfPCell {
        val cell = PdfPCell(Phrase(content))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        //Boslukları ayarla
        cell.setPadding(8f)
        cell.isUseAscender = true
        cell.paddingLeft = 4f
        cell.paddingRight = 4f
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

    fun createUserTable(
        data: List<ReportData>,
        lessonName: String,
        onFinish: (file: File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        //Define the document
        val file = createFile()
        val document = createDocument()

        //Setup PDF Writer
        setupPdfWriter(document, file)

        //Add table title
        document.add(Paragraph(lessonName, TITLE_FONT))
        addLineSpace(document, 1)

        //Tabloyu Tanımla
        val tableWidth = 0.01f
        val columnWidth = floatArrayOf(
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth,
            tableWidth
        )
        val table = createTable(14, columnWidth)
        //Tablo baslıklarını tanımla
        val tableHeaderContent = listOf(
            "Öğrenciler",
            " Hafta 1",
            " Hafta 2",
            " Hafta 3",
            " Hafta 4",
            " Hafta 5",
            " Hafta 6",
            " Hafta 7",
            " Hafta 9",
            " Hafta 10",
            " Hafta 11",
            " Hafta 12",
            " Hafta 13",
            " Hafta 14"
        )
        //Tablo baslıgını tabloya ekle
        tableHeaderContent.forEach {
            //define a cell
            val cell = createCell(it)
            //add our cell into our table
            table.addCell(cell)
        }

        //Kullanıcı verılerını tabloya ekle
        data.forEach { mData ->
            val studentName = mData.studentName?.let { mStudentName -> createCell(mStudentName) }
            table.addCell(studentName)

            mData.week.forEach { w ->
                val cell = createCell(w)
                //tabloya olusturulan haftayı ekleme
                table.addCell(cell)
            }
        }
        document.add(table)
        document.close()

        try {
            pdf.close()
        } catch (ex: Exception) {
            onError(ex)
        } finally {
            onFinish(file)
        }
    }
}
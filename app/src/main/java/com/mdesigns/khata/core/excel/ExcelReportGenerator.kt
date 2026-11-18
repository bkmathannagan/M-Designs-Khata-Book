package com.mdesigns.khata.core.excel

import android.content.Context
import com.mdesigns.khata.core.utils.CurrencyFormatter
import com.mdesigns.khata.core.utils.DateUtils
import java.io.File
import java.io.FileWriter

object ExcelReportGenerator {

    data class ExportRow(
        val date: Long,
        val personName: String,
        val description: String,
        val type: String,
        val amount: Double,
        val paymentMode: String,
        val balance: Double
    )

    fun exportToCsv(context: Context, filename: String, rows: List<ExportRow>): File {
        val outputDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val file = File(outputDir, "$filename.csv")
        val writer = FileWriter(file)

        writer.append("M Designs Khata - Ledger Export\n")
        writer.append("Website: https://mdesignsrmd.com\n")
        writer.append("Generated on: ${DateUtils.formatDateTime(System.currentTimeMillis())}\n\n")
        writer.append("Date,Party / Person,Description,Type,Amount (INR),Payment Mode,Running Balance (INR)\n")

        for (row in rows) {
            val safeDesc = row.description.replace(",", ";")
            val safeParty = row.personName.replace(",", ";")
            writer.append("${DateUtils.formatDate(row.date)},$safeParty,$safeDesc,${row.type},${row.amount},${row.paymentMode},${row.balance}\n")
        }

        writer.flush()
        writer.close()
        return file
    }
}

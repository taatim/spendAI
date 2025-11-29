package com.example.spendai.services

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class OCRService(private val context: Context) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val amountRegex = Pattern.compile("(\\d{1,6}\\.\\d{2})")
    private val merchantRegex = Pattern.compile("\\d{5,}")

    suspend fun extractText(imageUri: Uri): String {
        return try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).await()
            result.text
        } catch (e: IOException) {
            e.printStackTrace()
            "Error extracting text"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error extracting text"
        }
    }

    fun parseReceiptData(text: String): Map<String, Any> {
        val lines = text.split("\n")
        val totalKeywords = listOf("total", "bill amount", "amount", "grand total", "net total")
        var total = 0.0

        // Find total
        for (i in lines.indices) {
            val line = lines[i].lowercase()
            for (keyword in totalKeywords) {
                if (line.contains(keyword)) {
                    // Check current line
                    val matcherCurrent = amountRegex.matcher(lines[i])
                    if (matcherCurrent.find()) {
                        val amount = matcherCurrent.group(1)?.toDoubleOrNull()
                        if (amount != null && amount > 0 && amount < 1000000) {
                            total = amount
                            break
                        }
                    }

                    // Check next line
                    if (i + 1 < lines.size && total == 0.0) {
                        val matcherNext = amountRegex.matcher(lines[i + 1])
                        if (matcherNext.find()) {
                            val amount = matcherNext.group(1)?.toDoubleOrNull()
                            if (amount != null && amount > 0 && amount < 1000000) {
                                total = amount
                                break
                            }
                        }
                    }
                }
            }
            if (total > 0.0) break
        }

        // Fallback for total
        if (total == 0.0) {
            val amounts = mutableListOf<Double>()
            for (line in lines) {
                val matcher = amountRegex.matcher(line)
                while (matcher.find()) {
                    val amount = matcher.group(1)?.toDoubleOrNull()
                    if (amount != null && amount > 0 && amount < 10000) {
                        amounts.add(amount)
                    }
                }
            }
            if (amounts.isNotEmpty()) {
                total = amounts.maxOrNull() ?: 0.0
            }
        }

        // Find merchant
        var merchant = "Unknown Merchant"
        for (i in 0 until minOf(lines.size, 5)) {
            val line = lines[i].trim()
            if (line.length > 5 && !merchantRegex.matcher(line).find()) {
                merchant = line
                break
            }
        }

        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val date = LocalDate.now().format(formatter)

        return mapOf(
            "merchant" to merchant,
            "amount" to total,
            "date" to date
        )
    }
}

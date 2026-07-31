package com.example.ui.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.local.PearlEntity

object ExportUtils {

    fun copyToClipboard(context: Context, label: String, content: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText(label, content)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Clipboard error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Share text directly via Android Share Sheet (Email, Notes, Messages, WhatsApp, Drive)
     * and copy to system clipboard with a confirmation Toast message.
     */
    fun shareAndExportText(context: Context, title: String, content: String) {
        try {
            // 1. Copy to clipboard
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText(title, content)
            clipboard?.setPrimaryClip(clip)

            // 2. Trigger System Share Intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
            }
            val chooserIntent = Intent.createChooser(shareIntent, "Export $title via:")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)

            Toast.makeText(context, "Copied to clipboard and ready to export!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Converts a list of PearlEntity into Anki-compatible tab-separated format (.txt / .csv)
     * Compatible with Anki Desktop, AnkiMobile, and AnkiDroid flashcard import.
     */
    fun generateAnkiDeckString(pearls: List<PearlEntity>): String {
        val sb = StringBuilder()
        sb.append("#separator:tab\n")
        sb.append("#html:true\n")
        sb.append("#tags column:3\n")
        sb.append("# Deck: MedColleague High-Yield Medical Pearls\n")

        for (p in pearls) {
            val front = "<b>[${p.specialty}] ${p.title}</b><br><br><i>Concept:</i> ${p.concept}"
                .replace("\n", "<br>").replace("\t", " ")
            val back = "<b>HIGH-YIELD PEARL:</b><br>${p.highYieldPearl}<br><br><b>GUIDELINE / MOA:</b><br>${p.moaOrGuideline}"
                .replace("\n", "<br>").replace("\t", " ")
            val tag = "MedColleague::${p.specialty.replace(" ", "_")}"
            sb.append("$front\t$back\t$tag\n")
        }
        return sb.toString()
    }

    fun exportPearlsToAnki(context: Context, pearls: List<PearlEntity>) {
        if (pearls.isEmpty()) {
            Toast.makeText(context, "No saved pearls to export!", Toast.LENGTH_SHORT).show()
            return
        }
        val ankiContent = generateAnkiDeckString(pearls)
        shareAndExportText(
            context = context,
            title = "MedColleague_Anki_Deck.txt",
            content = ankiContent
        )
    }
}


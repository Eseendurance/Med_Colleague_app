package com.example.ui.utils

data class LabConversion(
    val labName: String,
    val conventionalUnit: String,
    val siUnit: String,
    val factorToSi: Double,
    val referenceRangeConventional: String,
    val referenceRangeSi: String,
    val clinicalContext: String
)

object LabUnitConverter {

    val labs = listOf(
        LabConversion(
            labName = "Glucose",
            conventionalUnit = "mg/dL",
            siUnit = "mmol/L",
            factorToSi = 0.0555,
            referenceRangeConventional = "70 - 99 mg/dL (Fasting)",
            referenceRangeSi = "3.9 - 5.5 mmol/L",
            clinicalContext = "Fasting plasma glucose. Diabetic diagnostic cutoff >= 126 mg/dL (7.0 mmol/L)."
        ),
        LabConversion(
            labName = "Serum Creatinine",
            conventionalUnit = "mg/dL",
            siUnit = "µmol/L",
            factorToSi = 88.4,
            referenceRangeConventional = "0.7 - 1.3 mg/dL",
            referenceRangeSi = "62 - 115 µmol/L",
            clinicalContext = "Key marker for renal function and eGFR calculation."
        ),
        LabConversion(
            labName = "Total Cholesterol",
            conventionalUnit = "mg/dL",
            siUnit = "mmol/L",
            factorToSi = 0.0259,
            referenceRangeConventional = "< 200 mg/dL",
            referenceRangeSi = "< 5.2 mmol/L",
            clinicalContext = "Desirable lipid panel target for cardiovascular primary prevention."
        ),
        LabConversion(
            labName = "Triglycerides",
            conventionalUnit = "mg/dL",
            siUnit = "mmol/L",
            factorToSi = 0.0113,
            referenceRangeConventional = "< 150 mg/dL",
            referenceRangeSi = "< 1.7 mmol/L",
            clinicalContext = "Hypertriglyceridemia marker (>500 mg/dL increases acute pancreatitis risk)."
        ),
        LabConversion(
            labName = "Serum Calcium",
            conventionalUnit = "mg/dL",
            siUnit = "mmol/L",
            factorToSi = 0.2495,
            referenceRangeConventional = "8.5 - 10.5 mg/dL",
            referenceRangeSi = "2.15 - 2.55 mmol/L",
            clinicalContext = "Total calcium. Correct for albumin: Corrected Ca = Total Ca + 0.8 * (4.0 - Albumin)."
        ),
        LabConversion(
            labName = "Total Bilirubin",
            conventionalUnit = "mg/dL",
            siUnit = "µmol/L",
            factorToSi = 17.1,
            referenceRangeConventional = "0.2 - 1.2 mg/dL",
            referenceRangeSi = "3.4 - 20.5 µmol/L",
            clinicalContext = "Hepatic function and hemolysis evaluation. Clinical jaundice apparent at > 2.5 mg/dL."
        ),
        LabConversion(
            labName = "Blood Urea Nitrogen (BUN)",
            conventionalUnit = "mg/dL",
            siUnit = "mmol/L",
            factorToSi = 0.357,
            referenceRangeConventional = "7 - 20 mg/dL",
            referenceRangeSi = "2.5 - 7.1 mmol/L",
            clinicalContext = "BUN/Creatinine ratio > 20 suggests prerenal azotemia."
        ),
        LabConversion(
            labName = "Serum Albumin",
            conventionalUnit = "g/dL",
            siUnit = "g/L",
            factorToSi = 10.0,
            referenceRangeConventional = "3.5 - 5.0 g/dL",
            referenceRangeSi = "35 - 50 g/L",
            clinicalContext = "Visceral protein synthesis and oncotic pressure marker."
        )
    )

    fun convertConventionalToSi(value: Double, lab: LabConversion): Double {
        return value * lab.factorToSi
    }

    fun convertSiToConventional(value: Double, lab: LabConversion): Double {
        return value / lab.factorToSi
    }

    /**
     * Parses a slash command input like `/convert glucose 100` or `/unit creatinine 1.5`
     */
    fun processSlashCommand(command: String): String? {
        val trimmed = command.trim()
        if (!trimmed.startsWith("/") && !trimmed.startsWith("\\")) return null

        val parts = trimmed.substring(1).trim().split("\\s+".toRegex())
        val verb = parts.getOrNull(0)?.lowercase() ?: return null

        if (verb != "convert" && verb != "unit" && verb != "lab" && verb != "units") {
            return null
        }

        if (parts.size < 2) {
            return """
                🧪 **Lab Unit Converter Slash Commands Available:**
                Usage: `/convert [lab] [value]`
                Examples:
                • `/convert glucose 100` (mg/dL to mmol/L)
                • `/convert creatinine 1.2`
                • `/convert cholesterol 200`
                • `/convert calcium 9.5`
                • `/convert bilirubin 1.8`
                • `/convert bun 18`
            """.trimIndent()
        }

        val labQuery = parts[1].lowercase()
        val rawVal = parts.getOrNull(2)?.toDoubleOrNull() ?: 100.0

        val matchedLab = labs.find { it.labName.lowercase().contains(labQuery) } ?: labs.first()

        val siVal = convertConventionalToSi(rawVal, matchedLab)
        val convVal = convertSiToConventional(rawVal, matchedLab)

        return """
            🧪 **Medical Lab Unit Converter Result:**
            
            **${matchedLab.labName}**:
            • **Input Value**: $rawVal ${matchedLab.conventionalUnit} = **${String.format("%.2f", siVal)} ${matchedLab.siUnit}** (SI Units)
            • **Reverse Conversion**: $rawVal ${matchedLab.siUnit} = **${String.format("%.2f", convVal)} ${matchedLab.conventionalUnit}**
            
            📊 **Reference Ranges:**
            • Conventional: ${matchedLab.referenceRangeConventional}
            • SI Standard: ${matchedLab.referenceRangeSi}
            
            💡 **Clinical Context:**
            ${matchedLab.clinicalContext}
        """.trimIndent()
    }
}

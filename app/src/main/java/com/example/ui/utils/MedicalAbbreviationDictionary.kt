package com.example.ui.utils

data class MedicalTermDefinition(
    val term: String,
    val expansion: String,
    val category: String,
    val definition: String,
    val highYieldPearl: String
)

object MedicalAbbreviationDictionary {

    val dictionary = mapOf(
        "STEMI" to MedicalTermDefinition(
            term = "STEMI",
            expansion = "ST-Elevation Myocardial Infarction",
            category = "Cardiology",
            definition = "Acute transmural myocardial ischemia causing ST-segment elevation on ECG across contiguous leads.",
            highYieldPearl = "Door-to-balloon time goal is < 90 minutes for primary PCI."
        ),
        "NSTEMI" to MedicalTermDefinition(
            term = "NSTEMI",
            expansion = "Non-ST-Elevation Myocardial Infarction",
            category = "Cardiology",
            definition = "Subendocardial myocardial necrosis evidenced by elevated cardiac troponin without ST-segment elevation.",
            highYieldPearl = "Requires risk stratification using TIMI or GRACE scores."
        ),
        "HFrEF" to MedicalTermDefinition(
            term = "HFrEF",
            expansion = "Heart Failure with Reduced Ejection Fraction",
            category = "Cardiology",
            definition = "Clinical heart failure syndrome with left ventricular ejection fraction (LVEF) <= 40%.",
            highYieldPearl = "Requires 4 GDMT pillars: ARNI/ACEi/ARB, Beta-Blocker, MRA, and SGLT2 inhibitor."
        ),
        "HFpEF" to MedicalTermDefinition(
            term = "HFpEF",
            expansion = "Heart Failure with Preserved Ejection Fraction",
            category = "Cardiology",
            definition = "Clinical heart failure with LVEF >= 50% characterized by impaired LV diastolic relaxation.",
            highYieldPearl = "SGLT2 inhibitors (Empagliflozin, Dapagliflozin) have Class 1A recommendation to reduce hospitalizations."
        ),
        "SGLT2i" to MedicalTermDefinition(
            term = "SGLT2i",
            expansion = "Sodium-Glucose Cotransporter-2 Inhibitor",
            category = "Pharmacology",
            definition = "Class of oral medications (e.g. Dapagliflozin, Empagliflozin) preventing renal glucose reabsorption in PCT.",
            highYieldPearl = "Provides mortality benefit in heart failure and slows CKD progression regardless of diabetes status."
        ),
        "DOAC" to MedicalTermDefinition(
            term = "DOAC",
            expansion = "Direct Oral Anticoagulant",
            category = "Pharmacology",
            definition = "Oral anticoagulants directly inhibiting Factor Xa (Apixaban, Rivaroxaban) or Thrombin (Dabigatran).",
            highYieldPearl = "Preferred over Warfarin for non-valvular atrial fibrillation due to lower ICH risk."
        ),
        "CKD" to MedicalTermDefinition(
            term = "CKD",
            expansion = "Chronic Kidney Disease",
            category = "Nephrology",
            definition = "Abnormalities of kidney structure or function present for > 3 months, defined as eGFR < 60 mL/min/1.73m² or albuminuria.",
            highYieldPearl = "First-line renal protection includes ACEi/ARB and SGLT2 inhibitors."
        ),
        "ABG" to MedicalTermDefinition(
            term = "ABG",
            expansion = "Arterial Blood Gas",
            category = "Pulmonology / Critical Care",
            definition = "Blood test measuring pH, PaO2, PaCO2, HCO3-, and lactate to assess acid-base status and oxygenation.",
            highYieldPearl = "Winter's Formula calculates expected PaCO2 compensation in metabolic acidosis: 1.5*(HCO3) + 8 ± 2."
        ),
        "BNP" to MedicalTermDefinition(
            term = "BNP",
            expansion = "B-type Natriuretic Peptide",
            category = "Cardiology / Labs",
            definition = "Hormone secreted by ventricular myocytes in response to wall stress and volume stretch.",
            highYieldPearl = "High negative predictive value (>95%) for ruling out acute heart failure exacerbation."
        ),
        "CrCl" to MedicalTermDefinition(
            term = "CrCl",
            expansion = "Creatinine Clearance",
            category = "Nephrology / Pharmacology",
            definition = "Estimate of GFR calculated via Cockcroft-Gault equation using age, body weight, serum creatinine, and sex.",
            highYieldPearl = "Multiply overall formula result by 0.85 for female patients."
        ),
        "eGFR" to MedicalTermDefinition(
            term = "eGFR",
            expansion = "Estimated Glomerular Filtration Rate",
            category = "Nephrology",
            definition = "Calculated rate of blood filtration through glomeruli based on serum creatinine (CKD-EPI equation).",
            highYieldPearl = "eGFR < 15 mL/min/1.73m² corresponds to Stage 5 CKD / End-Stage Renal Disease."
        ),
        "ARNI" to MedicalTermDefinition(
            term = "ARNI",
            expansion = "Angiotensin Receptor-Neprilysin Inhibitor",
            category = "Pharmacology",
            definition = "Combination drug (Sacubitril/Valsartan) inhibiting neprilysin and blocking AT1 receptors.",
            highYieldPearl = "Requires 36-hour washout period when switching from ACE inhibitor to avoid severe angioedema."
        ),
        "COPD" to MedicalTermDefinition(
            term = "COPD",
            expansion = "Chronic Obstructive Pulmonary Disease",
            category = "Pulmonology",
            definition = "Progressive airflow limitation characterized by chronic bronchitis and emphysema with post-bronchodilator FEV1/FVC < 0.70.",
            highYieldPearl = "Supplemental O2 goal in acute COPD exacerbation is 88-92% SpO2 to prevent hypercapnic respiratory drive suppression."
        ),
        "DKA" to MedicalTermDefinition(
            term = "DKA",
            expansion = "Diabetic Ketoacidosis",
            category = "Endocrinology",
            definition = "Acute metabolic complication characterized by hyperglycemia (>250 mg/dL), high anion gap metabolic acidosis, and ketonemia.",
            highYieldPearl = "Always check potassium level before starting IV insulin; if K < 3.3 mEq/L, replete K first."
        ),
        "GDMT" to MedicalTermDefinition(
            term = "GDMT",
            expansion = "Guideline-Directed Medical Therapy",
            category = "Clinical Evidence",
            definition = "Optimized medical regimen supported by high-level randomized controlled clinical trials.",
            highYieldPearl = "Mandatory titration to target evidence-based doses reduces patient mortality."
        )
    )

    fun findTerm(query: String): MedicalTermDefinition? {
        val upper = query.trim().uppercase()
        dictionary[upper]?.let { return it }
        return dictionary.values.find {
            it.term.equals(upper, ignoreCase = true) ||
            it.expansion.contains(upper, ignoreCase = true)
        }
    }

    /**
     * Finds all medical terms present in a text body to render tap-to-define chips/popups.
     */
    fun findTermsInText(text: String): List<MedicalTermDefinition> {
        val found = mutableListOf<MedicalTermDefinition>()
        for ((term, def) in dictionary) {
            val pattern = Regex("\\b$term\\b", RegexOption.IGNORE_CASE)
            if (pattern.containsMatchIn(text)) {
                found.add(def)
            }
        }
        return found
    }
}

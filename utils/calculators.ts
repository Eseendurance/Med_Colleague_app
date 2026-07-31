/**
 * Clinically Verified Medical Calculators Engine
 * - eGFR (CKD-EPI 2021 Race-Free Formula)
 * - Serum Anion Gap with Albumin Correction
 */

export interface EgfrParams {
  scr: number; // Serum Creatinine in mg/dL
  age: number; // Age in years
  isFemale: boolean;
}

export interface EgfrResult {
  egfr: number; // mL/min/1.73m²
  stage: string; // CKD Stage description
}

/**
 * Calculates eGFR using the 2021 CKD-EPI Creatinine Equation (Ref: Inker LA et al., NEJM 2021)
 * Formula: eGFR = 142 * min(Scr/kappa, 1)^alpha * max(Scr/kappa, 1)^-1.200 * 0.9938^Age * (1.012 if female)
 */
export function calculateEgfr2021({ scr, age, isFemale }: EgfrParams): EgfrResult {
  if (scr <= 0 || age <= 0) {
    throw new Error("Invalid input: Creatinine and age must be greater than zero.");
  }

  const kappa = isFemale ? 0.7 : 0.9;
  const alpha = isFemale ? -0.241 : -0.302;
  const femaleFactor = isFemale ? 1.012 : 1.0;

  const minScr = Math.min(scr / kappa, 1.0);
  const maxScr = Math.max(scr / kappa, 1.0);

  const egfrValue =
    142 *
    Math.pow(minScr, alpha) *
    Math.pow(maxScr, -1.2) *
    Math.pow(0.9938, age) *
    femaleFactor;

  const egfr = Math.round(egfrValue * 10) / 10;

  let stage = "G1 (Normal or High, ≥90)";
  if (egfr < 15) {
    stage = "G5 (Kidney Failure, <15)";
  } else if (egfr < 30) {
    stage = "G4 (Severe decrease, 15-29)";
  } else if (egfr < 45) {
    stage = "G3b (Moderate-to-severe, 30-44)";
  } else if (egfr < 60) {
    stage = "G3a (Mild-to-moderate, 45-59)";
  } else if (egfr < 90) {
    stage = "G2 (Mild decrease, 60-89)";
  }

  return { egfr, stage };
}

export interface AnionGapParams {
  na: number; // Sodium mEq/L
  cl: number; // Chloride mEq/L
  hco3: number; // Bicarbonate mEq/L
  albumin?: number; // Albumin g/dL (Normal ~ 4.0 g/dL)
}

export interface AnionGapResult {
  uncorrectedGap: number;
  correctedGap: number;
  interpretation: string;
}

/**
 * Calculates Serum Anion Gap
 * Uncorrected Gap = Na - (Cl + HCO3)
 * Corrected Gap (for hypoalbuminemia) = Uncorrected + 2.5 * (4.0 - Albumin)
 */
export function calculateAnionGap({ na, cl, hco3, albumin }: AnionGapParams): AnionGapResult {
  if (na <= 0 || cl <= 0 || hco3 <= 0) {
    throw new Error("Electrolyte values must be positive numbers.");
  }

  const uncorrectedGap = Math.round((na - (cl + hco3)) * 10) / 10;
  let correctedGap = uncorrectedGap;

  if (albumin !== undefined && albumin > 0) {
    const adjustment = 2.5 * (4.0 - albumin);
    correctedGap = Math.round((uncorrectedGap + adjustment) * 10) / 10;
  }

  let interpretation = "Normal Anion Gap (4-12 mEq/L)";
  if (correctedGap > 12) {
    interpretation = "High Anion Gap Metabolic Acidosis (HAGMA) (>12 mEq/L) - Consider GOLD MARK / MUDPILES differential.";
  } else if (correctedGap < 4) {
    interpretation = "Low Anion Gap (<4 mEq/L) - Consider hypoalbuminemia, multiple myeloma, or lithium toxicity.";
  }

  return { uncorrectedGap, correctedGap, interpretation };
}

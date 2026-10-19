import type { VercelRequest, VercelResponse } from '@vercel/node';

export interface TutorRequest {
  documentText?: string;
  studentQuestion?: string;
  topic?: string;
  mode?: 'lecture' | 'question' | 'walkthrough' | 'general_qa';
}

export default async function handler(req: VercelRequest, res: VercelResponse) {
  // Enable CORS for PWA and Mobile clients
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  try {
    const { documentText, studentQuestion, topic, mode = 'question' }: TutorRequest = req.body || {};

    if (!documentText && !studentQuestion && !topic) {
      return res.status(400).json({ error: 'documentText, studentQuestion, or topic required' });
    }

    const apiKey = process.env.GEMINI_API_KEY;

    if (!apiKey) {
      // High-yield fallback clinical educator engine
      const explanation = generateComprehensiveTutorResponse(topic, studentQuestion, documentText, mode);
      return res.status(200).json({
        success: true,
        source: 'dr_ese_clinical_engine',
        explanation,
        lecturer: 'Dr. Ese, MD (Senior Clinical Educator)'
      });
    }

    let prompt = '';
    if (mode === 'lecture' || topic) {
      prompt = `You are Dr. Ese, an esteemed Senior Medical Professor, Clinician, and USMLE/Board Exam Educator. 
Deliver a high-yield, engaging, and comprehensive Clinical Masterclass Lecture on the medical topic: "${topic || studentQuestion || documentText}".

Structure your lecture clearly with the following sections in clean Markdown:
# 🎓 Masterclass with Dr. Ese: ${topic || studentQuestion}

## 🎯 1. Learning Objectives & Core Mechanism
- Clearly define the disease process, pathophysiology, and cellular/molecular mechanism.

## 🩺 2. Clinical Presentation & Diagnostic Algorithm
- Classic symptoms, physical exam hallmarks, diagnostic criteria, and first-line labs/imaging.

## 💊 3. Evidence-Based Pharmacotherapy & Management
- First-line interventions, guideline-directed therapy (GDMT), dosing considerations, and alternatives.

## ⚡ 4. Board Traps, Contraindications & High-Yield Pearls
- What questions try to trick students with (e.g. initial vs definitive step, contraindicated drugs).

## ❓ 5. Interactive Board Challenge Question
- A USMLE/ABIM-style vignette question with 4 options (A-D) testing this concept, followed by the correct answer with full explanation.

Keep the tone encouraging, authoritative, and clinically precise.`;
    } else {
      prompt = `You are Dr. Ese, a Senior Medical Professor and expert clinical physician. 
A student/clinician asks you this medical/health question: "${studentQuestion || documentText}".

Answer thoroughly, accurately, and with full medical depth, suitable for medical students, healthcare practitioners, and curious patients.
Include:
1. Direct Clinical Answer & Core Summary
2. Underlying Pathophysiology & Mechanisms
3. Diagnostic Workup / Key Findings (if relevant)
4. Management, Pharmacology, or Treatment Recommendations
5. ⚡ Dr. Ese's High-Yield Clinical Pearl & Watch-Outs / Red Flags

Provide accurate medical terminology and clear explanations.`;
    }

    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`;

    const geminiRes = await fetch(geminiUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: { temperature: 0.3 }
      })
    });

    const data = await geminiRes.json();
    const explanation = data?.candidates?.[0]?.content?.parts?.[0]?.text;

    if (explanation) {
      return res.status(200).json({
        success: true,
        source: 'gemini_1_5_flash',
        explanation,
        lecturer: 'Dr. Ese, MD'
      });
    } else {
      const fallback = generateComprehensiveTutorResponse(topic, studentQuestion, documentText, mode);
      return res.status(200).json({
        success: true,
        source: 'fallback_clinical_engine',
        explanation: fallback,
        lecturer: 'Dr. Ese, MD'
      });
    }
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Internal server error' });
  }
}

function generateComprehensiveTutorResponse(topic?: string, q?: string, doc?: string, mode?: string): string {
  const query = (topic || q || doc || '').toLowerCase();

  // Curated high-yield lectures and general medical answers
  if (query.includes('acid') || query.includes('base') || query.includes('anion') || query.includes('winter')) {
    return `### 🎓 Clinical Masterclass with Dr. Ese: Acid-Base Mastery & Winter's Formula

#### 🎯 1. Core Pathophysiology & Systematic Algorithm
Acid-base interpretation must ALWAYS follow a strict 4-step sequence:
1. **Examine pH**: Normal 7.35–7.45. pH < 7.35 = Acidemia; pH > 7.45 = Alkalemia.
2. **Identify Primary Disturbance**: Look at PaCO2 (respiratory) and HCO3- (metabolic).
3. **Calculate the Serum Anion Gap**: $\\text{AG} = [\\text{Na}^+] - ([\\text{Cl}^-] + [\\text{HCO}_3^-])$. Normal is **8–12 mEq/L**.
   - *Albumin correction*: For every 1.0 g/dL drop in serum albumin below 4.0, add 2.5 mEq/L to the calculated AG.
4. **Evaluate Compensation**:
   - In Metabolic Acidosis: Use **Winter's Formula**: Expected $\\text{PaCO}_2 = 1.5 \\times [\\text{HCO}_3^-] + 8 \\pm 2$.
   - If actual PaCO2 > expected: Concurrent **Respiratory Acidosis** (hypoventilation / fatigue).
   - If actual PaCO2 < expected: Concurrent **Respiratory Alkalosis** (hyperventilation / sepsis / aspirin).

#### 🧪 2. The Delta-Delta Ratio ($\\Delta\\text{AG} / \\Delta\\text{HCO}_3^-$)
- Formula: $(\\text{Calculated AG} - 12) / (24 - \\text{Measured } [\\text{HCO}_3^-])$.
- **Ratio < 1**: Combined High Anion Gap (HAGMA) + Normal Anion Gap (NAGMA/RTA/diarrhea).
- **Ratio 1 – 2**: Pure High Anion Gap Metabolic Acidosis (DKA, lactic acidosis).
- **Ratio > 2**: Concurrent Metabolic Alkalosis or pre-existing high bicarbonate (e.g., severe vomiting or COPD).

#### ⚡ 3. Dr. Ese's High-Yield Board Pearls
- **Mnemonic**: Use **GOLDMARK** (Glycols, Oxoproline, L-lactate, D-lactate, Methanol, Aspirin, Renal failure, Ketoacidosis).
- **Exam Trap**: A patient with DKA who also vomits violently will have a **HAGMA + Metabolic Alkalosis**; their bicarbonate may appear near normal (e.g. 20 mEq/L), but their Anion Gap will be severely elevated (e.g. 26 mEq/L). Always calculate the gap!`;
  }

  if (query.includes('wpw') || query.includes('wolff') || query.includes('arrhythmia')) {
    return `### 🎓 Clinical Masterclass with Dr. Ese: Wolff-Parkinson-White (WPW) & Pre-Excitation Syndromes

#### 🎯 1. Mechanism of Conduction & Electrocardiography
WPW is characterized by an accessory pathway (the **Bundle of Kent**) that directly bridges the atria and ventricles, bypassing the standard physiological conduction delay of the AV node.
- **Classic ECG Triad**: Short PR interval (< 120 ms), Slurred upstroke of the QRS (**Delta Wave**), and widened QRS complex (> 120 ms).

#### 🚨 2. Pre-Excited Atrial Fibrillation: Emergency Pitfall
When Atrial Fibrillation occurs in a patient with WPW:
- Irregularly irregular, wide-complex, extremely rapid tachycardia (often > 200–250 bpm).
- **CRITICAL CONTRAINDICATION (ABCD Traps)**:
  - **A**denosine
  - **B**eta-blockers
  - **C**alcium channel blockers (non-DHP: Diltiazem, Verapamil)
  - **D**igoxin
  - *Mechanism*: Blocking the AV node diverts 100% of the erratic atrial impulses down the uninhibited accessory pathway, precipitating **Ventricular Fibrillation and sudden cardiac death**.

#### 💊 3. Guideline-Directed Management
- **Hemodynamically Unstable** (Hypotension, altered mental status, chest pain): Immediate **Synchronized Electrical Cardioversion**.
- **Hemodynamically Stable**: Intravenous **Procainamide** or **Ibutilide** (prolongs refractory period of the accessory pathway).
- **Definitive Cure**: Catheter Radiofrequency Ablation of the bypass tract.`;
  }

  if (query.includes('sepsis') || query.includes('shock') || query.includes('hypotension')) {
    return `### 🎓 Clinical Masterclass with Dr. Ese: Sepsis & Septic Shock Guidelines

#### 🎯 1. Surviving Sepsis Campaign 1-Hour Bundle
Time is tissue in septic shock. Complete within 60 minutes of presentation:
1. **Measure Serum Lactate**: Remeasure within 2-4 hours if initial > 2 mmol/L.
2. **Obtain Blood Cultures**: Before antimicrobial administration (2 sets from different sites).
3. **Administer Broad-Spectrum Antibiotics**: Target likely pathogen within 1 hour.
4. **Rapid IV Fluid Resuscitation**: $30 \\text{ mL/kg}$ of balanced crystalloid (Lactated Ringer's) for hypotension or lactate $\\ge 4 \\text{ mmol/L}$.
5. **Vasopressors**: If MAP $< 65 \\text{ mmHg}$ despite adequate fluid resuscitation:
   - First-line: **Norepinephrine** (titrated to MAP $\\ge 65 \\text{ mmHg}$).
   - Second-line: Add **Vasopressin** (fixed dose 0.03 units/min) to reduce norepinephrine dose.

#### ⚡ 2. Dr. Ese's Board Pearls
- Use **qSOFA** for quick bedside triage: Respiratory rate $\\ge 22$, Altered mentation (GCS $< 15$), Systolic BP $\\le 100 \\text{ mmHg}$. (Score $\\ge 2$ signifies high risk).
- Avoid relying on fluid responsiveness solely from central venous pressure (CVP). Use dynamic measures like passive leg raise test.`;
  }

  if (query.includes('hyponatremia') || query.includes('sodium') || query.includes('electrolyte')) {
    return `### 🎓 Clinical Masterclass with Dr. Ese: Systematic Approach to Hyponatremia

#### 🎯 1. Stepwise Diagnostic Algorithm
1. **Confirm Hypo-osmolality**: Measure Serum Osmolality.
   - Normal (275–295): Pseudohyponatremia (severe hyperlipidemia or paraproteinemia).
   - High (> 295): Hyperglycemia / Mannitol (Correct Na: add 1.6–2.0 mEq/L per 100 mg/dL glucose over 100).
2. **Evaluate Volume Status**:
   - **Hypovolemic**: Dehydration, diuretics, vomiting, burns. (Urine Na < 20 indicates extra-renal losses; Urine Na > 20 indicates renal losses).
   - **Euvolemic**: SIADH, Psychogenic Polydipsia, Hypothyroidism, Adrenal Insufficiency.
   - **Hypervolemic**: Heart Failure, Cirrhosis, Nephrotic Syndrome.

#### 🚨 2. Safe Correction Limits & Osmotic Demyelination
- **Acute severe symptomatic** (seizures, coma): Bolus $3\\%$ Hypertonic Saline (100 mL over 10 min, repeat up to twice) to raise Na by $4-6\\text{ mEq/L}$.
- **Chronic Hyponatremia**: Never exceed **8 mEq/L in 24 hours** (6 mEq/L in liver failure/malnutrition). Rapid overcorrection causes **Osmotic Demyelination Syndrome (Central Pontine Myelinolysis)**!`;
  }

  // General Comprehensive Answer for any other medical or health topic
  return `### 👩‍🏫 Clinical Answer & Educator Breakdown with Dr. Ese

#### 🎯 1. Clinical Overview & Summary
Regarding **"${q || topic || doc || 'your medical query'}"**:
In modern clinical medicine and evidence-based practice, this scenario requires understanding both the immediate pathophysiological mechanism and the prioritized diagnostic triage.

#### 🔬 2. Pathophysiology & Disease Mechanism
- Cellular and organ-level interactions dictate clinical symptoms and progression.
- Always analyze whether the presentation is an acute decompensation versus a chronic, indolent physiological shift.
- Compensatory physiological reserves (baroreceptors, cardiorenal feedback, neuroendocrine activation) modulate the initial presentation.

#### 🩺 3. Diagnostic & Evidence-Based Workup
1. **Initial Assessment**: Assess stability (Airway, Breathing, Circulation, Vital Signs).
2. **Targeted Testing**: Order focused diagnostic studies (CBC, Comprehensive Metabolic Panel, targeted biomarkers, or appropriate cross-sectional imaging).
3. **Differential Stratification**: Rule out life-threatening mimics before investigating rare or zebrine etiologies.

#### 💊 4. Management Protocol & Guideline Considerations
- Initiate guideline-directed medical therapy (GDMT) tailored to patient comorbidities, renal clearance (eGFR), and drug interactions.
- Avoid common contraindications and drug-drug interactions.
- Monitor response with serial clinical assessments and repeat laboratory indices.

#### ⚡ 5. Dr. Ese's High-Yield Board Pearl
- **Exam & Practice Tip**: Whenever a question stem asks for the "Next Step in Management", distinguish strictly between the **initial non-invasive diagnostic maneuver** and the **definitive confirmatory gold standard**. 
- Keep red-flag symptoms in mind: hemodynamic instability, acute neurological deficits, and progressive refractory pain demand emergency stabilization!`;
}

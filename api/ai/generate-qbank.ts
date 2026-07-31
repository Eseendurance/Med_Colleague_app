import type { VercelRequest, VercelResponse } from '@vercel/node';

export default async function handler(req: VercelRequest, res: VercelResponse) {
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
    const { organSystem, weaknessLevel, examType } = req.body || {};
    const targetSystem = organSystem || 'Renal / Nephrology';
    const targetExam = examType || 'USMLE Step 1';

    const apiKey = process.env.GEMINI_API_KEY || process.env.GOOGLE_API_KEY;

    if (!apiKey) {
      const fallbackItem = generateFallbackQuestion(targetSystem, targetExam);
      return res.status(200).json({
        success: true,
        source: 'fallback_generator',
        question: fallbackItem
      });
    }

    const prompt = `You are an expert NBME/USMLE question item writer.
Generate ONE high-yield, realistic clinical vignette question targeting the student's weakest organ system: "${targetSystem}" for "${targetExam}".
Return strictly valid JSON with this format:
{
  "system": "${targetSystem}",
  "vignette": "A 54-year-old male presents with...",
  "options": [
    "A. Option 1",
    "B. Option 2",
    "C. Option 3",
    "D. Option 4",
    "E. Option 5"
  ],
  "correctIndex": 1,
  "correctReason": "Detailed pathophysiological explanation of why option B is correct.",
  "incorrectReasons": [
    "A is incorrect because...",
    "C is incorrect because...",
    "D is incorrect because...",
    "E is incorrect because..."
  ],
  "pearl": "High-yield board pearl regarding this topic.",
  "difficulty": "Hard"
}`;

    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`;

    const response = await fetch(geminiUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
          temperature: 0.4,
          responseMimeType: "application/json"
        }
      })
    });

    const data = await response.json();
    const jsonText = data?.candidates?.[0]?.content?.parts?.[0]?.text;

    if (jsonText) {
      const parsedQuestion = JSON.parse(jsonText);
      return res.status(200).json({
        success: true,
        source: 'gemini_1_5_flash',
        question: parsedQuestion
      });
    } else {
      const fallbackItem = generateFallbackQuestion(targetSystem, targetExam);
      return res.status(200).json({
        success: true,
        source: 'fallback_generator',
        question: fallbackItem
      });
    }
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Internal server error generating QBank item' });
  }
}

function generateFallbackQuestion(system: string, exam: string) {
  if (system.includes('Renal') || system.includes('Nephrology')) {
    return {
      system: 'Renal / Nephrology',
      vignette: 'A 62-year-old male with long-standing poorly controlled type 2 diabetes mellitus presents for routine follow-up. Laboratory evaluation reveals serum creatinine 2.1 mg/dL, BUN 32 mg/dL, and a urine albumin-to-creatinine ratio of 320 mg/g. Renal biopsy demonstrates diffuse glomerulosclerosis with nodular hyaline deposits within the mesangium.',
      options: [
        'A. Kimmelstiel-Wilson nodules',
        'B. Crescentic formation in Bowman space',
        'C. Apple-green birefringence under polarized light',
        'D. Subepithelial humps on electron microscopy',
        'E. Subendothelial immune complex deposits'
      ],
      correctIndex: 0,
      correctReason: 'Kimmelstiel-Wilson nodules (nodular glomerulosclerosis) are pathognomonic for diabetic nephropathy caused by non-enzymatic glycation of efferent arterioles.',
      incorrectReasons: [
        'Crescentic formation is classic for Rapidly Progressive Glomerulonephritis (RPGN).',
        'Apple-green birefringence is characteristic of Renal Amyloidosis (Congo Red stain).',
        'Subepithelial humps are seen in Post-Streptococcal Glomerulonephritis (PSGN).',
        'Subendothelial deposits are seen in Lupus Nephritis (Class IV) or MPGN Type I.'
      ],
      pearl: 'Diabetic Nephropathy initially presents with hyperfiltration (increased GFR) followed by microalbuminuria (30-300 mg/day) and Kimmelstiel-Wilson nodular glomerulosclerosis.',
      difficulty: 'Hard'
    };
  }

  return {
    system: system,
    vignette: `A 58-year-old patient presents for evaluation related to ${system}. Diagnostic workup reveals abnormal physiological markers requiring targeted board-level management.`,
    options: [
      'A. Initiate immediate targeted guideline-directed medical therapy',
      'B. Perform emergency invasive procedures before hemodynamic stabilization',
      'C. Discontinue all active medications without laboratory monitoring',
      'D. Reassure the patient and discharge without follow-up',
      'E. Order serial monitoring without therapeutic intervention'
    ],
    correctIndex: 0,
    correctReason: 'First-line management requires prompt initiation of guideline-directed medical therapy (GDMT) tailored to patient risk stratification.',
    incorrectReasons: [
      'Invasive procedures should not precede acute resuscitation.',
      'Abrupt medication cessation without laboratory monitoring poses rebound risk.',
      'Reassurance alone is inadequate for active pathological signs.',
      'Therapeutic intervention is indicated when clinical thresholds are crossed.'
    ],
    pearl: `In ${system} questions, always confirm acute hemodynamic stability before selecting definitive maintenance options.`,
    difficulty: 'Medium'
  };
}

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
    const { caseText, fileName, organSystem } = req.body || {};

    if (!caseText) {
      return res.status(400).json({ error: 'caseText parameter is required.' });
    }

    const apiKey = process.env.GEMINI_API_KEY || process.env.GOOGLE_API_KEY;

    if (!apiKey) {
      const fallbackPayload = generateFallbackPipelineOutput(caseText, organSystem);
      return res.status(200).json({
        success: true,
        source: 'fallback_pipeline',
        data: fallbackPayload
      });
    }

    const prompt = `You are an expert medical education AI pipeline.
Analyze the following clinical case/upload text:
"${caseText}"

Generate a complete study bundle in strictly valid JSON with this structure:
{
  "extractedOrganSystem": "Cardiology",
  "summary": "Brief 2-sentence clinical summary of key findings.",
  "weaknessDelta": {
    "system": "Cardiology",
    "suggestedMasteryAdjustment": -0.05,
    "rationale": "High complexity presented in acute ECG ischemia findings."
  },
  "qbankItems": [
    {
      "system": "Cardiology",
      "vignette": "Vignette question 1 based on case...",
      "options": ["A. Choice 1", "B. Choice 2", "C. Choice 3", "D. Choice 4"],
      "correctIndex": 0,
      "correctReason": "Rationale for question 1",
      "incorrectReasons": ["Note 1", "Note 2", "Note 3"],
      "pearl": "High yield pearl 1"
    }
  ],
  "flashcards": [
    {
      "system": "Cardiology",
      "front": "What is the hallmark finding on ECG for acute inferior MI?",
      "back": "ST-segment elevation in leads II, III, and aVF with reciprocal depression in leads I and aVL.",
      "pearl": "RCA occlusion is responsible in ~85% of cases."
    }
  ]
}`;

    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`;

    const response = await fetch(geminiUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        contents: [{ parts: [{ text: prompt }] }],
        generationConfig: {
          temperature: 0.3,
          responseMimeType: "application/json"
        }
      })
    });

    const data = await response.json();
    const jsonText = data?.candidates?.[0]?.content?.parts?.[0]?.text;

    if (jsonText) {
      const parsed = JSON.parse(jsonText);
      return res.status(200).json({
        success: true,
        source: 'gemini_1_5_flash_pipeline',
        data: parsed
      });
    } else {
      const fallbackPayload = generateFallbackPipelineOutput(caseText, organSystem);
      return res.status(200).json({
        success: true,
        source: 'fallback_pipeline',
        data: fallbackPayload
      });
    }
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Internal server error in vignette pipeline' });
  }
}

function generateFallbackPipelineOutput(text: string, defaultSys?: string) {
  const sys = defaultSys || 'Cardiology';
  return {
    extractedOrganSystem: sys,
    summary: `Extracted key diagnostic features from uploaded clinical case note ("${text.substring(0, 80)}...").`,
    weaknessDelta: {
      system: sys,
      suggestedMasteryAdjustment: -0.05,
      rationale: 'Case presentation highlights high-yield differential nuances.'
    },
    qbankItems: [
      {
        system: sys,
        vignette: `A 62-year-old patient described in the uploaded record presents with progressive symptoms characteristic of acute ${sys} pathology. Initial vital signs demonstrate acute hemodynamic alterations. What is the most appropriate next step in management?`,
        options: [
          'A. Obtain ECG and initiate acute emergency resuscitation protocol',
          'B. Discharge patient with outpatient routine follow-up',
          'C. Schedule elective non-contrast CT in 2 weeks',
          'D. Administer oral antacids and re-evaluate in 24 hours'
        ],
        correctIndex: 0,
        correctReason: 'Immediate diagnostic evaluation and hemodynamic stabilization are mandatory for acute symptomatic presentations.',
        incorrectReasons: [
          'Outpatient follow-up is unsafe for acute clinical distress.',
          'Elective CT delays critical emergency intervention.',
          'Symptomatic management alone ignores potential life-threatening pathology.'
        ],
        pearl: `Always prioritize immediate diagnostic confirmation and ABCs in acute ${sys} presentations.`
      }
    ],
    flashcards: [
      {
        system: sys,
        front: `What is the first-line diagnostic investigation for suspected acute ${sys} decompensation?`,
        back: `Targeted laboratory panels, targeted bedside point-of-care ultrasound or ECG, and immediate vital sign assessment.`,
        pearl: `Rapid triage prevents progression to irreversible organ ischemia.`
      }
    ]
  };
}

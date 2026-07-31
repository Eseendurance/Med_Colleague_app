import type { VercelRequest, VercelResponse } from '@vercel/node';

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
    const { documentText, studentQuestion } = req.body || {};

    if (!documentText && !studentQuestion) {
      return res.status(400).json({ error: 'documentText or studentQuestion required' });
    }

    const apiKey = process.env.GEMINI_API_KEY;

    if (!apiKey) {
      // Fallback rule-based medical educator logic
      const explanation = generateFallbackTutorResponse(documentText, studentQuestion);
      return res.status(200).json({
        success: true,
        source: 'fallback_educator',
        explanation
      });
    }

    const prompt = studentQuestion
      ? `You are Dr. Ese, senior medical professor and AI board exam tutor. Student studies: "${documentText}". Question: "${studentQuestion}". Answer concisely with mechanism and high-yield board tip.`
      : `You are Dr. Ese, senior medical professor. Uploaded document: "${documentText}". Give a 3-point walkthrough: 1. Core Pathophysiology, 2. Key Diagnostic Rationale, 3. High-Yield Board Pearl, 4. One follow-up question.`;

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
        explanation
      });
    } else {
      const fallback = generateFallbackTutorResponse(documentText, studentQuestion);
      return res.status(200).json({ success: true, source: 'fallback', explanation: fallback });
    }
  } catch (error: any) {
    return res.status(500).json({ error: error.message || 'Internal server error' });
  }
}

function generateFallbackTutorResponse(doc?: string, q?: string): string {
  if (q) {
    return `👩‍🏫 **Dr. Ese's Clinical Answer**:\n\nGreat question regarding "${q}"!\n• In board questions, always identify whether the item asks for the *initial diagnostic test* versus *definitive gold-standard procedure*.\n• Option traps usually offer chronic maintenance medications when acute stabilization (ABC) is required first!`;
  }
  return `👩‍🏫 **Dr. Ese's Clinical Walkthrough**:\n\n"Let's break down this case together!"\n\n• **Core Pathophysiology**: The presented document indicates acute hemodynamic or metabolic shifts requiring immediate recognition.\n• **Diagnostic Rationale**: Always confirm patient stability before proceeding to secondary imaging or invasive procedures.\n• **⚡ High-Yield Board Pearl**: On USMLE & NCLEX, acute symptomatic presentations prioritize resuscitation (Airway, Breathing, Circulation) first!`;
}

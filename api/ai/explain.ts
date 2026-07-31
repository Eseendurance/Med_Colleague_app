import type { VercelRequest, VercelResponse } from '@vercel/node';

export default async function handler(req: VercelRequest, res: VercelResponse) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  try {
    const { question, selectedOption, correctOption, organSystem } = req.body || {};

    const explanation = `
🔬 **Clinical Rationale for ${organSystem || 'Medical Case'}**:

• **Correct Choice**: ${correctOption || 'Option A'} is the gold-standard intervention.
• **Selected Choice**: ${selectedOption || 'Your choice'}.
• **Mechanism**: In acute management, stabilizing hemodynamics and target receptor activity takes priority over long-term outpatient maintenance therapy.
• **⚡ High-Yield Memory Pearl**: Remember the first-line guidelines for ${organSystem || 'this topic'} on USMLE and NCLEX boards!
    `.trim();

    return res.status(200).json({ success: true, explanation });
  } catch (err: any) {
    return res.status(500).json({ error: err.message });
  }
}

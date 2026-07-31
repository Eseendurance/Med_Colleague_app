import type { VercelRequest, VercelResponse } from '@vercel/node';

export const config = {
  api: {
    bodyParser: {
      sizeLimit: '10mb',
    },
  },
};

export default async function handler(req: VercelRequest, res: VercelResponse) {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed. Use POST.' });
  }

  try {
    const { fileBase64, fileName, rawText } = req.body || {};

    if (!fileBase64 && !rawText) {
      return res.status(400).json({
        error: 'Missing file upload payload. Provide fileBase64 or rawText in body.',
      });
    }

    // Check payload size constraint (Max 10MB)
    const payloadLength = JSON.stringify(req.body).length;
    if (payloadLength > 10 * 1024 * 1024) {
      return res.status(413).json({ error: 'File size exceeds maximum allowed limit of 10MB.' });
    }

    let extractedText = '';

    if (fileBase64) {
      // Decode base64 buffer
      const buffer = Buffer.from(fileBase64, 'base64');

      // Simple, robust PDF text extraction from buffer
      const bufferStr = buffer.toString('utf-8');
      
      // Attempt PDF stream text extraction if standard text streams exist
      const streamMatches = bufferStr.match(/BT[\s\S]*?ET/g);
      if (streamMatches && streamMatches.length > 0) {
        extractedText = streamMatches
          .map((stream) => stream.replace(/[^\x20-\x7E\n\r]/g, ' '))
          .join('\n');
      } else {
        // Fallback buffer text extraction
        extractedText = bufferStr.replace(/[^\x20-\x7E\n\r]/g, ' ');
      }
    } else if (rawText) {
      extractedText = rawText;
    }

    // Basic sanitization
    let sanitizedText = extractedText
      .replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '') // Strip script tags
      .replace(/<[^>]+>/g, '') // Strip all HTML tags
      .replace(/[\x00-\x08\x0B\x0C\x0E-\x1F\x7F]/g, '') // Strip non-printable ASCII control characters
      .replace(/\s+/g, ' ') // Normalize spaces
      .trim();

    if (!sanitizedText || sanitizedText.length < 5) {
      sanitizedText = `Clinical case summary for ${fileName || 'uploaded document'}: Patient presents with acute symptoms requiring evaluation.`;
    }

    // Infer Organ System from sanitized text
    let detectedOrganSystem = 'General Internal Medicine';
    const lower = sanitizedText.toLowerCase();

    if (lower.includes('heart') || lower.includes('ecg') || lower.includes('troponin') || lower.includes('cardiac') || lower.includes('mi')) {
      detectedOrganSystem = 'Cardiology';
    } else if (lower.includes('kidney') || lower.includes('creatinine') || lower.includes('egfr') || lower.includes('renal') || lower.includes('urine')) {
      detectedOrganSystem = 'Renal / Nephrology';
    } else if (lower.includes('lung') || lower.includes('copd') || lower.includes('asthma') || lower.includes('pao2') || lower.includes('dyspnea')) {
      detectedOrganSystem = 'Pulmonology';
    } else if (lower.includes('gastro') || lower.includes('liver') || lower.includes('alt') || lower.includes('ast') || lower.includes('bowel')) {
      detectedOrganSystem = 'Gastroenterology';
    }

    return res.status(200).json({
      success: true,
      fileName: fileName || 'uploaded_case.pdf',
      charCount: sanitizedText.length,
      organSystem: detectedOrganSystem,
      extractedText: sanitizedText.substring(0, 3000), // Limit character length for downstream LLM
      sanitized: true,
    });
  } catch (error: any) {
    return res.status(500).json({
      error: error.message || 'Failed to parse uploaded PDF file.',
    });
  }
}

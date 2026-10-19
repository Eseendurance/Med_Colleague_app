/**
 * Clinical Notes Management Engine
 * Provides persistent local & cloud sync for medical concepts,
 * category color-coding, Google Voice synthesis, and SM-2 flashcard export.
 */

export interface NoteCategoryMeta {
  color: string;
  bg: string;
  border: string;
  icon: string;
}

export const NOTE_CATEGORIES: Record<string, NoteCategoryMeta> = {
  'Emergency': { color: '#B91C1C', bg: '#FEE2E2', border: '#FCA5A5', icon: '🚨' },
  'Pharmacology': { color: '#6D28D9', bg: '#EDE9FE', border: '#C4B5FD', icon: '💊' },
  'Review Needed': { color: '#B45309', bg: '#FEF3C7', border: '#FCD34D', icon: '⚠️' },
  'High-Yield': { color: '#0F766E', bg: '#CCFBF1', border: '#5EEAD4', icon: '⚡' },
  'Diagnostic': { color: '#0369A1', bg: '#E0F2FE', border: '#7DD3FC', icon: '🩺' },
  'Contraindication': { color: '#9F1239', bg: '#FFE4E6', border: '#FDA4AF', icon: '🚫' },
  'Guideline': { color: '#4338CA', bg: '#E0E7FF', border: '#A5B4FC', icon: '📋' },
  'Cardiology': { color: '#0369A1', bg: '#E0F2FE', border: '#BAE6FD', icon: '❤️' },
  'Renal / Acid-Base': { color: '#B45309', bg: '#FEF3C7', border: '#FDE68A', icon: '🧪' },
  'Endocrinology': { color: '#BE185D', bg: '#FCE7F3', border: '#FBCFE8', icon: '🩸' }
};

export function getTagStyle(tagName: string): NoteCategoryMeta {
  if (NOTE_CATEGORIES[tagName]) return NOTE_CATEGORIES[tagName];
  const lower = tagName.toLowerCase();
  for (const [key, val] of Object.entries(NOTE_CATEGORIES)) {
    if (key.toLowerCase() === lower || lower.includes(key.toLowerCase())) {
      return val;
    }
  }
  return { color: '#334155', bg: '#F1F5F9', border: '#CBD5E1', icon: '🏷️' };
}

export interface ClinicalNote {
  id: string;
  title: string;
  content: string;
  organSystem: string;
  isHighYield: boolean;
  isPinned: boolean;
  createdAt: string;
  tags: string[];
}

const STORAGE_KEY = 'medcolleague_clinical_notes_v1';

export const INITIAL_HIGH_YIELD_NOTES: ClinicalNote[] = [
  {
    id: 'note-sglt2i-hfp-ef',
    title: 'SGLT2 Inhibitors Cardiorenal GDMT',
    content: 'Dapagliflozin (DAPA-HF) & Empagliflozin (EMPEROR-Preserved) significantly reduce cardiovascular mortality and heart failure hospitalizations across the entire ejection fraction spectrum (HFrEF, HFmrEF, HFpEF). Crucial: Expect an initial, benign eGFR dip of up to 30% upon initiation due to afferent arteriole vasoconstriction and intraglomerular pressure reduction. Do NOT stop therapy for mild GFR dips.',
    organSystem: 'Cardiology',
    isHighYield: true,
    isPinned: true,
    createdAt: new Date(Date.now() - 3600000 * 4).toISOString(),
    tags: ['Pharmacology', 'High-Yield', 'Guideline', 'Cardiology']
  },
  {
    id: 'note-hagma-acid-base',
    title: 'HAGMA Diagnostic Workflow & Delta Gap',
    content: 'Calculate Anion Gap: [Na+] - ([Cl-] + [HCO3-]). Normal: 8-12 mEq/L. High AG (>12) indicates unmeasured organic acids. Etiology mnemonic: GOLDMARK (Glycols, Oxoproline, L-lactate, D-lactate, Methanol, Aspirin, Renal failure, Ketoacidosis). Always check Delta-Delta ratio (ΔAG / ΔHCO3-): Ratio < 1 indicates concurrent Normal Anion Gap Metabolic Acidosis (NAGMA); Ratio > 2 indicates concurrent Metabolic Alkalosis or pre-existing high HCO3-.',
    organSystem: 'Renal / Acid-Base',
    isHighYield: true,
    isPinned: true,
    createdAt: new Date(Date.now() - 3600000 * 24).toISOString(),
    tags: ['Diagnostic', 'Review Needed', 'Renal / Acid-Base']
  },
  {
    id: 'note-wpw-afib-contraindications',
    title: 'Pre-Excited Atrial Fibrillation (WPW Danger)',
    content: 'Presents as irregularly irregular, wide-complex, polymorphic tachycardia in younger patients. PATHOGNOMONIC TRAP: Do NOT administer AV-nodal blocking agents (Beta-blockers, Non-DHP CCBs like Diltiazem/Verapamil, Digoxin, or Adenosine)! Blocking the AV node diverts all conduction down the accessory pathway (Bundle of Kent), precipitating Ventricular Fibrillation and cardiac arrest. Hemodynamically stable: IV Procainamide or Ibutilide. Unstable: Immediate synchronized electrical cardioversion.',
    organSystem: 'Cardiology',
    isHighYield: true,
    isPinned: false,
    createdAt: new Date(Date.now() - 3600000 * 48).toISOString(),
    tags: ['Emergency', 'Contraindication', 'Pharmacology', 'Cardiology']
  },
  {
    id: 'note-hyponatremia-osmolality',
    title: 'Hyponatremia Serum Sodium Correction Ceilings',
    content: 'Acute severe symptomatic hyponatremia: Administer 3% hypertonic saline (100 mL boluses over 10 min, up to 3 times) to raise Na+ by 4-6 mEq/L and abort herniation/seizures. Chronic hyponatremia: Max correction rate is strictly 8 mEq/L in any 24-hour window (6 mEq/L in high-risk patients with malnutrition or cirrhosis) to prevent irreversible Osmotic Demyelination Syndrome (Central Pontine Myelinolysis). If overcorrecting: Administer DDAVP + D5W.',
    organSystem: 'Endocrinology',
    isHighYield: true,
    isPinned: false,
    createdAt: new Date(Date.now() - 3600000 * 72).toISOString(),
    tags: ['Emergency', 'Review Needed', 'High-Yield', 'Endocrinology']
  }
];

export function getStoredNotes(): ClinicalNote[] {
  if (typeof window === 'undefined') return INITIAL_HIGH_YIELD_NOTES;
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(INITIAL_HIGH_YIELD_NOTES));
      return INITIAL_HIGH_YIELD_NOTES;
    }
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) && parsed.length > 0 ? parsed : INITIAL_HIGH_YIELD_NOTES;
  } catch (err) {
    console.error('Failed to read notes from localStorage', err);
    return INITIAL_HIGH_YIELD_NOTES;
  }
}

export function saveStoredNotes(notes: ClinicalNote[]): void {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notes));
  } catch (err) {
    console.error('Failed to save notes to localStorage', err);
  }
}

export function filterNotesByTag(notes: ClinicalNote[], tag: string): ClinicalNote[] {
  if (!tag || tag === 'ALL') return notes;
  const target = tag.toLowerCase();
  return notes.filter(n => (n.tags || []).some(t => t.toLowerCase() === target));
}

export function addClinicalNote(note: Omit<ClinicalNote, 'id' | 'createdAt'>): ClinicalNote {
  const notes = getStoredNotes();
  const newNote: ClinicalNote = {
    ...note,
    id: `note-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
    createdAt: new Date().toISOString()
  };
  const updated = [newNote, ...notes];
  saveStoredNotes(updated);
  return newNote;
}

export function deleteClinicalNote(id: string): ClinicalNote[] {
  const notes = getStoredNotes();
  const updated = notes.filter(n => n.id !== id);
  saveStoredNotes(updated);
  return updated;
}

export function togglePinClinicalNote(id: string): ClinicalNote[] {
  const notes = getStoredNotes();
  const updated = notes.map(n => n.id === id ? { ...n, isPinned: !n.isPinned } : n);
  saveStoredNotes(updated);
  return updated;
}

export interface TagSuggestion {
  name: string;
  count: number;
  isPreset: boolean;
  style: NoteCategoryMeta;
}

export function getAllKnownTags(notes: ClinicalNote[]): string[] {
  const tagsSet = new Set<string>();
  Object.keys(NOTE_CATEGORIES).forEach(k => tagsSet.add(k));
  notes.forEach(n => {
    (n.tags || []).forEach(t => {
      if (t && t.trim()) tagsSet.add(t.trim());
    });
  });
  return Array.from(tagsSet);
}

export function getTagSuggestions(
  query: string,
  notes: ClinicalNote[],
  excludeTags: string[] = []
): TagSuggestion[] {
  const cleanQuery = (query || '').trim().toLowerCase();
  const excludeSet = new Set(excludeTags.map(t => t.toLowerCase()));

  // Count frequencies across all notes
  const counts: Record<string, number> = {};
  notes.forEach(n => {
    (n.tags || []).forEach(t => {
      const trimmed = t.trim();
      counts[trimmed] = (counts[trimmed] || 0) + 1;
    });
  });

  const knownTags = getAllKnownTags(notes);

  const matched = knownTags.filter(tag => {
    if (excludeSet.has(tag.toLowerCase())) return false;
    if (!cleanQuery) return true;
    return tag.toLowerCase().includes(cleanQuery);
  });

  // Ranking: Prefix matches first, then frequency, then alphabetical
  matched.sort((a, b) => {
    const aLower = a.toLowerCase();
    const bLower = b.toLowerCase();
    const aStarts = cleanQuery ? aLower.startsWith(cleanQuery) : false;
    const bStarts = cleanQuery ? bLower.startsWith(cleanQuery) : false;
    if (aStarts && !bStarts) return -1;
    if (!aStarts && bStarts) return 1;
    const countA = counts[a] || 0;
    const countB = counts[b] || 0;
    if (countB !== countA) return countB - countA;
    return a.localeCompare(b);
  });

  return matched.map(tag => ({
    name: tag,
    count: counts[tag] || 0,
    isPreset: Boolean(NOTE_CATEGORIES[tag]),
    style: getTagStyle(tag)
  }));
}

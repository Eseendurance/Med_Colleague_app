const test = require('node:test');
const assert = require('node:assert/strict');

// Import compiled or transpiled modules directly or test equivalent logic
const { calculateEgfr2021, calculateAnionGap } = require('../utils/calculators.ts');
const { calculateSM2 } = require('../utils/sm2.ts');
const { updateMasteryBKT, calculateIRTScorePrediction } = require('../utils/weaknessEngine.ts');

test('CKD-EPI 2021 eGFR Calculator - Reference Standard Checks', () => {
  // Test case 1: 50 yo Male, Serum Creatinine 1.0 mg/dL (Normal renal function)
  const resultMale = calculateEgfr2021({ scr: 1.0, age: 50, isFemale: false });
  assert.ok(resultMale.egfr > 85, `Expected eGFR > 85, got ${resultMale.egfr}`);
  assert.equal(resultMale.stage, 'G1 (Normal or High, ≥90)');

  // Test case 2: 65 yo Female, Serum Creatinine 2.5 mg/dL (Severe CKD)
  const resultFemale = calculateEgfr2021({ scr: 2.5, age: 65, isFemale: true });
  assert.ok(resultFemale.egfr < 30, `Expected eGFR < 30, got ${resultFemale.egfr}`);
  assert.equal(resultFemale.stage, 'G4 (Severe decrease, 15-29)');
});

test('Serum Anion Gap Calculator - Normal & HAGMA Checks', () => {
  // Test case 1: Na 140, Cl 104, HCO3 24 (Uncorrected Gap = 12)
  const normalGap = calculateAnionGap({ na: 140, cl: 104, hco3: 24 });
  assert.equal(normalGap.uncorrectedGap, 12);
  assert.equal(normalGap.interpretation, 'Normal Anion Gap (4-12 mEq/L)');

  // Test case 2: Na 138, Cl 98, HCO3 15, Albumin 2.0 g/dL (High Anion Gap Acidosis with Hypoalbuminemia)
  const hagma = calculateAnionGap({ na: 138, cl: 98, hco3: 15, albumin: 2.0 });
  assert.equal(hagma.uncorrectedGap, 25);
  // Corrected = 25 + 2.5 * (4.0 - 2.0) = 30
  assert.equal(hagma.correctedGap, 30);
  assert.ok(hagma.interpretation.includes('HAGMA'));
});

test('SuperMemo-2 (SM-2) Spaced Repetition Scheduling', () => {
  // Test case 1: First recall (grade 4 - Good)
  const sm2First = calculateSM2({ grade: 4, repetitions: 0, easeFactor: 2.5, intervalDays: 0 });
  assert.equal(sm2First.repetitions, 1);
  assert.equal(sm2First.intervalDays, 1);

  // Test case 2: Failure (grade 1 - Wrong) -> Resets repetitions
  const sm2Fail = calculateSM2({ grade: 1, repetitions: 3, easeFactor: 2.4, intervalDays: 14 });
  assert.equal(sm2Fail.repetitions, 0);
  assert.equal(sm2Fail.intervalDays, 1);
});

test('Bayesian Knowledge Tracing & IRT Board Score Prediction', () => {
  const initMastery = 0.5;
  const updatedCorrect = updateMasteryBKT(initMastery, true);
  assert.ok(updatedCorrect > initMastery, 'Mastery should increase after correct response');

  const updatedIncorrect = updateMasteryBKT(initMastery, false);
  assert.ok(updatedIncorrect < initMastery, 'Mastery should decrease after incorrect response');

  const masteryMap = {
    Cardiology: 0.8,
    Pulmonology: 0.7,
    Renal: 0.4,
    Endocrinology: 0.3
  };

  const irtPrediction = calculateIRTScorePrediction(masteryMap);
  assert.ok(irtPrediction.predictedScore3Digit >= 180 && irtPrediction.predictedScore3Digit <= 260);
  assert.equal(irtPrediction.weakestSystems[0].system, 'Endocrinology');
});

test('Clinical Notes Management & Flashcard Conversion Engine', () => {
  const { INITIAL_HIGH_YIELD_NOTES } = require('../utils/notesStorage.ts');
  assert.ok(Array.isArray(INITIAL_HIGH_YIELD_NOTES));
  assert.ok(INITIAL_HIGH_YIELD_NOTES.length >= 4, 'Should have initial high-yield notes');

  const sglt2i = INITIAL_HIGH_YIELD_NOTES.find(n => n.id.includes('sglt2i'));
  assert.ok(sglt2i, 'SGLT2i note must exist');
  assert.equal(sglt2i.organSystem, 'Cardiology');
  assert.equal(sglt2i.isHighYield, true);

  // Conversion of note to SM-2 Flashcard format
  const convertedCard = {
    system: sglt2i.organSystem.toUpperCase(),
    front: `Clinical Concept: ${sglt2i.title} - Core Management Principles`,
    back: `✅ ${sglt2i.content.substring(0, 150)}...`,
    pearl: `Tags: ${sglt2i.tags.join(', ')}`
  };

  assert.ok(convertedCard.front.includes('SGLT2'));
  assert.ok(convertedCard.back.startsWith('✅'));
});

test('Clinical Note Tag Management & Color-Coded Categorization', () => {
  const { NOTE_CATEGORIES, getTagStyle, filterNotesByTag, INITIAL_HIGH_YIELD_NOTES } = require('../utils/notesStorage.ts');

  // Verify preset categories exist
  assert.ok(NOTE_CATEGORIES['Emergency'], 'Emergency category must be defined');
  assert.ok(NOTE_CATEGORIES['Pharmacology'], 'Pharmacology category must be defined');
  assert.ok(NOTE_CATEGORIES['Review Needed'], 'Review Needed category must be defined');

  // Verify distinct colors
  assert.equal(NOTE_CATEGORIES['Emergency'].color, '#B91C1C');
  assert.equal(NOTE_CATEGORIES['Pharmacology'].color, '#6D28D9');
  assert.equal(NOTE_CATEGORIES['Review Needed'].color, '#B45309');

  // Verify getTagStyle matches case-insensitively
  const emergencyStyle = getTagStyle('emergency');
  assert.equal(emergencyStyle.color, '#B91C1C');
  assert.equal(emergencyStyle.icon, '🚨');

  // Verify filtering by tags
  const emergencyNotes = filterNotesByTag(INITIAL_HIGH_YIELD_NOTES, 'Emergency');
  assert.ok(emergencyNotes.length >= 2, 'Should find at least 2 Emergency notes');

  const pharmNotes = filterNotesByTag(INITIAL_HIGH_YIELD_NOTES, 'Pharmacology');
  assert.ok(pharmNotes.length >= 2, 'Should find at least 2 Pharmacology notes');

  const allNotes = filterNotesByTag(INITIAL_HIGH_YIELD_NOTES, 'ALL');
  assert.equal(allNotes.length, INITIAL_HIGH_YIELD_NOTES.length);
});

test('Tag Auto-Suggestion Engine & Dropdown Suggestions', () => {
  const { getTagSuggestions, getAllKnownTags, INITIAL_HIGH_YIELD_NOTES } = require('../utils/notesStorage.ts');

  const knownTags = getAllKnownTags(INITIAL_HIGH_YIELD_NOTES);
  assert.ok(knownTags.includes('Emergency'), 'Should include preset Emergency');
  assert.ok(knownTags.includes('Pharmacology'), 'Should include preset Pharmacology');
  assert.ok(knownTags.includes('Cardiology'), 'Should include note tag Cardiology');

  // Test suggestion matching query 'phar'
  const pharSuggestions = getTagSuggestions('phar', INITIAL_HIGH_YIELD_NOTES);
  assert.ok(pharSuggestions.length > 0, 'Should return matching suggestions for phar');
  assert.equal(pharSuggestions[0].name, 'Pharmacology');
  assert.ok(pharSuggestions[0].count >= 2, 'Should report correct usage frequency');

  // Test excludeTags parameter (already selected tags in modal)
  const excludedSuggestions = getTagSuggestions('emer', INITIAL_HIGH_YIELD_NOTES, ['Emergency']);
  assert.ok(!excludedSuggestions.some(s => s.name === 'Emergency'), 'Should exclude already selected tag');

  // Test empty query returns ranked popular tags
  const emptyQuerySuggestions = getTagSuggestions('', INITIAL_HIGH_YIELD_NOTES);
  assert.ok(emptyQuerySuggestions.length >= 5, 'Should return known tags on empty query');
});


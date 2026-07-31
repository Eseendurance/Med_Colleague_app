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

/**
 * Unified Weakness Engine & IRT (Item Response Theory) Knowledge Mastery Model
 * Combines QBank attempts, Flashcard SM-2 recall, and AI Tutor interactions
 * into a single unified mastery graph per organ system per user.
 */

export interface OrganMasteryState {
  organSystem: string;
  pMastery: number; // Probability of mastery [0.0 - 1.0]
  attemptsCount: number;
  correctCount: number;
  flashcardReviewsCount: number;
  lastUpdated: string;
}

export interface IRTScorePrediction {
  theta: number; // IRT Latent Ability (-3.0 to +3.0)
  predictedScore3Digit: number; // USMLE 3-digit score scale (approx 140 - 280)
  passProbability: number; // NCLEX/USMLE Pass Probability [0 - 100%]
  confidenceInterval95: [number, number]; // [lowerBound, upperBound] 3-digit score
  weakestSystems: Array<{ system: string; masteryPercent: number }>;
}

const DEFAULT_ORGAN_SYSTEMS = [
  "Cardiology",
  "Pulmonology",
  "Renal / Nephrology",
  "Endocrinology",
  "Gastroenterology",
  "Neurology",
  "Hematology / Oncology",
  "Infectious Disease",
];

// Bayesian Knowledge Tracing Parameters
const BKT_PARAMS = {
  pInit: 0.5, // Initial prior probability of knowing a topic
  pTransit: 0.1, // Probability of transitioning from unmastered to mastered per study activity
  pSlip: 0.1, // Probability of making a mistake despite knowing the concept
  pGuess: 0.25, // Probability of guessing correctly (4-option multiple choice)
};

/**
 * Updates Bayesian probability of mastery given a binary outcome (1 = correct/good, 0 = wrong/bad)
 */
export function updateMasteryBKT(currentMastery: number, isCorrect: boolean): number {
  let pObsGivenMastery = isCorrect ? (1 - BKT_PARAMS.pSlip) : BKT_PARAMS.pSlip;
  let pObsGivenNotMastery = isCorrect ? BKT_PARAMS.pGuess : (1 - BKT_PARAMS.pGuess);

  // Bayes Rule update
  let pPost = (pObsGivenMastery * currentMastery) /
    (pObsGivenMastery * currentMastery + pObsGivenNotMastery * (1 - currentMastery));

  // Transition update
  let pNext = pPost + (1 - pPost) * BKT_PARAMS.pTransit;

  return Math.max(0.05, Math.min(0.98, Math.round(pNext * 1000) / 1000));
}

/**
 * Computes 1-Parameter Rasch IRT Ability (theta) and predicts 3-digit USMLE score & CI
 */
export function calculateIRTScorePrediction(masteryMap: Record<string, number>): IRTScorePrediction {
  const systems = Object.keys(masteryMap);
  if (systems.length === 0) {
    return {
      theta: 0.0,
      predictedScore3Digit: 215,
      passProbability: 75,
      confidenceInterval95: [205, 225],
      weakestSystems: [],
    };
  }

  const avgMastery = systems.reduce((acc, sys) => acc + masteryMap[sys], 0) / systems.length;

  // Convert avg mastery (0 to 1) to IRT Theta (-3.0 to +3.0) using Logit transformation
  const pClamped = Math.max(0.05, Math.min(0.95, avgMastery));
  const theta = Math.log(pClamped / (1 - pClamped)); // Logit(p)

  // Predict 3-Digit Board Score: Baseline 220 + (theta * 20)
  const predictedScore3Digit = Math.round(220 + theta * 22);

  // Standard Error of Estimate SE = 1 / sqrt(N_items_information)
  const standardErrorScore = Math.max(5, Math.round(15 / Math.sqrt(systems.length)));
  const ciLower = Math.max(160, predictedScore3Digit - 1.96 * standardErrorScore);
  const ciUpper = Math.min(280, predictedScore3Digit + 1.96 * standardErrorScore);

  // Pass Probability (Cutoff ~ 196 for Step 1 / NCLEX equivalent)
  const passProbability = Math.min(99, Math.max(10, Math.round((1 / (1 + Math.exp(-(predictedScore3Digit - 196) / 8))) * 100)));

  // Rank weakest systems
  const weakestSystems = systems
    .map((sys) => ({ system: sys, masteryPercent: Math.round(masteryMap[sys] * 100) }))
    .sort((a, b) => a.masteryPercent - b.masteryPercent);

  return {
    theta: Math.round(theta * 100) / 100,
    predictedScore3Digit,
    passProbability,
    confidenceInterval95: [Math.round(ciLower), Math.round(ciUpper)],
    weakestSystems,
  };
}

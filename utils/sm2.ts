/**
 * SuperMemo-2 (SM-2) Spaced Repetition Algorithm Implementation
 * Reference: SuperMemo-2 Algorithm (Wozniak, 1990)
 */

export interface SM2Input {
  grade: number; // Quality of recall (0: Complete blackout, 1: Wrong, 2: Wrong but remembered, 3: Hard, 4: Good, 5: Easy)
  repetitions: number; // Previous repetition count
  easeFactor: number; // Previous ease factor (default 2.5)
  intervalDays: number; // Previous interval in days
}

export interface SM2Output {
  repetitions: number;
  easeFactor: number;
  intervalDays: number;
  nextReviewAt: Date;
}

export function calculateSM2({ grade, repetitions, easeFactor, intervalDays }: SM2Input): SM2Output {
  if (grade < 0 || grade > 5) {
    throw new Error("Grade must be an integer between 0 and 5.");
  }

  let nextRepetitions = repetitions;
  let nextEaseFactor = easeFactor;
  let nextIntervalDays = intervalDays;

  if (grade >= 3) {
    // Correct response
    if (repetitions === 0) {
      nextIntervalDays = 1;
    } else if (repetitions === 1) {
      nextIntervalDays = 6;
    } else {
      nextIntervalDays = Math.round(intervalDays * easeFactor);
    }
    nextRepetitions = repetitions + 1;
  } else {
    // Incorrect response - reset repetitions
    nextRepetitions = 0;
    nextIntervalDays = 1;
  }

  // Update Ease Factor: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
  nextEaseFactor = easeFactor + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02));
  if (nextEaseFactor < 1.3) {
    nextEaseFactor = 1.3; // Minimum ease factor floor
  }

  nextEaseFactor = Math.round(nextEaseFactor * 100) / 100;

  const nextReviewAt = new Date();
  nextReviewAt.setDate(nextReviewAt.getDate() + nextIntervalDays);

  return {
    repetitions: nextRepetitions,
    easeFactor: nextEaseFactor,
    intervalDays: nextIntervalDays,
    nextReviewAt,
  };
}

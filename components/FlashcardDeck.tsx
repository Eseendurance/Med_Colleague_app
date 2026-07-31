"use client";

import React, { useState, useEffect } from "react";

export interface FlashcardItem {
  id: string;
  system: string;
  frontText: string;
  backText: string;
  notes?: string;
}

const SAMPLE_FLASHCARDS: FlashcardItem[] = [
  {
    id: "fc-1",
    system: "CARDIOVASCULAR • PHARMACOLOGY",
    frontText:
      "What is the first-line pharmacotherapy for acute symptomatic Wolff-Parkinson-White (WPW) syndrome presenting with Atrial Fibrillation?",
    backText: "✅ IV Procainamide or Ibutilide",
    notes: "(Contraindicated: Metoprolol, Diltiazem, Digoxin, Adenosine)",
  },
  {
    id: "fc-2",
    system: "RENAL & ACID-BASE",
    frontText:
      "What formula is used to calculate the Serum Anion Gap, and what threshold defines High Anion Gap Metabolic Acidosis?",
    backText: "✅ Anion Gap = Na⁺ - (Cl⁻ + HCO₃⁻)",
    notes: "Normal range is 8-12 mEq/L. A gap > 12 mEq/L indicates HAGMA.",
  },
  {
    id: "fc-3",
    system: "PULMONOLOGY • CRITICAL CARE",
    frontText:
      "What classic triad on ECG is suggestive of acute Cor Pulmonale secondary to massive Pulmonary Embolism?",
    backText: "✅ S1Q3T3 Pattern",
    notes: "S wave in lead I, Q wave in lead III, Inverted T wave in lead III (plus sinus tachycardia).",
  },
];

export const FlashcardDeck: React.FC = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isFlipped, setIsFlipped] = useState<boolean>(false);
  const [reviewedCount, setReviewedCount] = useState<number>(380);
  const [lastRatingFeedback, setLastRatingFeedback] = useState<string | null>(
    null
  );

  const currentCard = SAMPLE_FLASHCARDS[currentIndex];

  const handleFlip = () => {
    setIsFlipped((prev) => !prev);
  };

  const handleRating = (ratingScore: number, intervalLabel: string) => {
    setReviewedCount((prev) => prev + 1);
    setLastRatingFeedback(`Card scheduled for ${intervalLabel}`);
    setIsFlipped(false);
    setCurrentIndex((prev) => (prev + 1) % SAMPLE_FLASHCARDS.length);

    setTimeout(() => {
      setLastRatingFeedback(null);
    }, 3000);
  };

  // Keyboard shortcut for Spacebar
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (
        e.target instanceof HTMLInputElement ||
        e.target instanceof HTMLTextAreaElement
      ) {
        return;
      }

      if (e.code === "Space") {
        e.preventDefault();
        handleFlip();
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, []);

  return (
    <div className="max-w-2xl mx-auto bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm">
      <div className="flex items-center justify-between mb-4">
        <span className="text-sm font-bold text-slate-800 dark:text-slate-200">
          ⚡ Spaced Repetition Flashcards (SM-2)
        </span>
        <span className="text-xs text-slate-500 font-medium">
          Reviewed: <strong className="text-emerald-600">{reviewedCount}</strong> • Card {currentIndex + 1} of {SAMPLE_FLASHCARDS.length}
        </span>
      </div>

      {/* Card Body */}
      <div
        onClick={handleFlip}
        className="min-h-[260px] cursor-pointer rounded-2xl bg-gradient-to-br from-slate-50 to-sky-50/30 dark:from-slate-800/80 dark:to-slate-800/30 border-2 border-sky-400/80 dark:border-sky-600/60 p-8 text-center flex flex-col items-center justify-center transition-all hover:-translate-y-0.5 shadow-sm"
      >
        <div className="text-xs font-bold text-sky-600 dark:text-sky-400 uppercase tracking-wider mb-3">
          {currentCard.system}
        </div>

        <p className="text-base font-bold text-slate-900 dark:text-slate-100 leading-relaxed mb-4">
          {currentCard.frontText}
        </p>

        {isFlipped ? (
          <div className="pt-4 border-t border-slate-200 dark:border-slate-700 w-full text-center animate-fadeIn">
            <p className="text-lg font-bold text-emerald-600 dark:text-emerald-400">
              {currentCard.backText}
            </p>
            {currentCard.notes && (
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-2">
                {currentCard.notes}
              </p>
            )}
          </div>
        ) : (
          <p className="text-xs text-slate-400 mt-4 font-medium">
            [Click card or press Spacebar to flip]
          </p>
        )}
      </div>

      {/* Rating Buttons - ONLY visible when flipped */}
      {isFlipped && (
        <div className="flex flex-wrap items-center justify-center gap-3 mt-6 animate-fadeIn">
          <button
            onClick={() => handleRating(1, "1 day")}
            className="px-4 py-2 rounded-xl text-xs font-semibold bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 transition-all"
          >
            Again <span className="text-[10px] opacity-70">(1d)</span>
          </button>
          <button
            onClick={() => handleRating(2, "3 days")}
            className="px-4 py-2 rounded-xl text-xs font-semibold bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 transition-all"
          >
            Hard <span className="text-[10px] opacity-70">(3d)</span>
          </button>
          <button
            onClick={() => handleRating(3, "5 days")}
            className="px-4 py-2 rounded-xl text-xs font-semibold bg-sky-500 hover:bg-sky-600 text-white transition-all shadow-sm"
          >
            Good <span className="text-[10px] opacity-90">(5d)</span>
          </button>
          <button
            onClick={() => handleRating(4, "7 days")}
            className="px-4 py-2 rounded-xl text-xs font-semibold bg-emerald-500 hover:bg-emerald-600 text-white transition-all shadow-sm"
          >
            Easy <span className="text-[10px] opacity-90">(7d)</span>
          </button>
        </div>
      )}

      {lastRatingFeedback && (
        <div className="mt-4 text-center text-xs font-bold text-emerald-600 dark:text-emerald-400">
          ✓ {lastRatingFeedback}
        </div>
      )}
    </div>
  );
};

export default FlashcardDeck;

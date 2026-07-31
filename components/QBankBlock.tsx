"use client";

import React, { useState, useEffect } from "react";

export interface QuestionItem {
  id: string;
  system: string;
  vignette: string;
  options: string[];
  correctChoiceIndex: number;
  explanation: {
    correctReason: string;
    incorrectReasons: string[];
  };
  boardPearl: string;
}

const SAMPLE_QUESTIONS: QuestionItem[] = [
  {
    id: "q-101",
    system: "CARDIOVASCULAR",
    vignette:
      "A 64-year-old male presents to the emergency department complaining of sudden onset palpitations and mild lightheadedness lasting 3 hours. Vital signs: BP 118/74 mmHg, HR 142 bpm (irregular), RR 18/min, SpO2 98% on room air. An ECG reveals irregular narrow-complex tachycardia without distinct P waves. Which of the following is the most appropriate initial management step?",
    options: [
      "A. Immediate synchronized electrical cardioversion at 100J",
      "B. Intravenous Metoprolol or Diltiazem for rate control",
      "C. High-dose oral Amiodarone for 14 days",
      "D. Immediate cardiac catheterization for coronary stenting",
    ],
    correctChoiceIndex: 1,
    explanation: {
      correctReason:
        "In hemodynamically stable Atrial Fibrillation with Rapid Ventricular Response (RVR), initial rate control using IV Beta-blockers (Metoprolol) or Non-dihydropyridine CCBs (Diltiazem) is recommended to control HR < 110 bpm.",
      incorrectReasons: [
        "Synchronized electrical cardioversion is indicated ONLY if the patient exhibits hemodynamic instability (hypotension, altered mental status, acute pulmonary edema, refractory chest pain).",
        "Amiodarone is a rhythm-control option that carries higher toxicities and is not first-line for acute rate control in stable patients.",
        "Cardiac catheterization is indicated for acute ST-elevation myocardial infarction (STEMI), not isolated primary atrial fibrillation without ischemia.",
      ],
    },
    boardPearl:
      "On USMLE & NCLEX, never administer AV-node blocking agents (Metoprolol, Diltiazem, Digoxin) in WPW patients presenting with AFib, as it precipitates Ventricular Fibrillation! Use Procainamide instead.",
  },
  {
    id: "q-102",
    system: "RENAL & ACID-BASE",
    vignette:
      "A 52-year-old female with type 1 diabetes mellitus presents to the emergency department with altered mental status and deep, rapid respirations. Serum labs: Na+ 138 mEq/L, Cl- 100 mEq/L, HCO3- 10 mEq/L, Glucose 480 mg/dL. Arterial Blood Gas: pH 7.20. Which of the following is the underlying acid-base disorder?",
    options: [
      "A. Normal Anion Gap Metabolic Acidosis",
      "B. High Anion Gap Metabolic Acidosis (Anion Gap = 28 mEq/L)",
      "C. Primary Respiratory Acidosis with Metabolic Compensation",
      "D. Metabolic Alkalosis secondary to volume depletion",
    ],
    correctChoiceIndex: 1,
    explanation: {
      correctReason:
        "Anion Gap = Na - (Cl + HCO3) = 138 - (100 + 10) = 28 mEq/L. An anion gap > 12 mEq/L confirms High Anion Gap Metabolic Acidosis (HAGMA), secondary to ketoacidosis (DKA).",
      incorrectReasons: [
        "Normal anion gap acidosis (8-12 mEq/L) occurs with diarrhea or renal tubular acidosis, not ketoacidosis.",
        "Respiratory compensation causes Kussmaul breathing (hyperventilation to lower PaCO2), but the primary disorder is metabolic acidosis.",
        "Metabolic alkalosis features elevated pH (> 7.45) and elevated HCO3- (> 28 mEq/L).",
      ],
    },
    boardPearl:
      "Remember the GOLD MARK or MUDPILES mnemonic for High Anion Gap Metabolic Acidosis: Glycols, Oxoproline, L-lactate, D-lactate, Methanol, Aspirin, Renal failure, Ketoacidosis.",
  },
  {
    id: "q-103",
    system: "PULMONOLOGY",
    vignette:
      "A 28-year-old female 4 days post-op after right femoral fracture fixation suddenly develops pleuritic chest pain and shortness of breath. Vital signs: HR 118 bpm, BP 122/78 mmHg, SpO2 91% on room air. ECG shows sinus tachycardia. What is the most appropriate initial diagnostic imaging test of choice?",
    options: [
      "A. Portable Bedside Chest X-Ray",
      "B. CT Pulmonary Angiography (CTPA)",
      "C. High-Resolution CT (HRCT) of Chest",
      "D. Transthoracic Echocardiogram (TTE)",
    ],
    correctChoiceIndex: 1,
    explanation: {
      correctReason:
        "In patients with high clinical probability for Acute Pulmonary Embolism (Wells Score > 4) and normal renal function without contrast allergy, CT Pulmonary Angiography (CTPA) is the gold-standard initial diagnostic imaging.",
      incorrectReasons: [
        "Chest X-Ray is performed to rule out alternative diagnoses (e.g. pneumothorax), but cannot confirm PE.",
        "HRCT is used to assess interstitial lung disease, not pulmonary vascular perfusion.",
        "TTE evaluates right ventricular strain in massive PE, but is not the primary diagnostic imaging modality for PE.",
      ],
    },
    boardPearl:
      "If CT contrast is contraindicated (e.g. severe renal impairment or pregnancy), order a Ventilation/Perfusion (V/Q) Scan as the next diagnostic step.",
  },
];

export const QBankBlock: React.FC = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState<number | null>(null);
  const [hasSubmitted, setHasSubmitted] = useState<boolean>(false);
  const [isCorrect, setIsCorrect] = useState<boolean | null>(null);
  const [userStreak, setUserStreak] = useState<number>(14);
  const [scoreMetrics, setScoreMetrics] = useState({
    totalAnswered: 40,
    correctCount: 34,
  });

  const currentQ = SAMPLE_QUESTIONS[currentIndex];

  const handleSelectOption = (index: number) => {
    if (!hasSubmitted) {
      setSelectedOption(index);
    }
  };

  const handleSubmit = () => {
    if (selectedOption === null || hasSubmitted) return;

    const correct = selectedOption === currentQ.correctChoiceIndex;
    setHasSubmitted(true);
    setIsCorrect(correct);

    if (correct) {
      setUserStreak((prev) => prev + 1);
      setScoreMetrics((prev) => ({
        totalAnswered: prev.totalAnswered + 1,
        correctCount: prev.correctCount + 1,
      }));
    } else {
      setUserStreak(0);
      setScoreMetrics((prev) => ({
        ...prev,
        totalAnswered: prev.totalAnswered + 1,
      }));
    }
  };

  const handleNextQuestion = () => {
    setSelectedOption(null);
    setHasSubmitted(false);
    setIsCorrect(null);
    setCurrentIndex((prev) => (prev + 1) % SAMPLE_QUESTIONS.length);
  };

  // Keyboard shortcut binding
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (
        e.target instanceof HTMLInputElement ||
        e.target instanceof HTMLTextAreaElement
      ) {
        return;
      }

      if (["1", "2", "3", "4"].includes(e.key)) {
        const optionIdx = parseInt(e.key, 10) - 1;
        if (optionIdx >= 0 && optionIdx < currentQ.options.length) {
          handleSelectOption(optionIdx);
        }
      } else if (e.key === "Enter") {
        if (!hasSubmitted && selectedOption !== null) {
          handleSubmit();
        } else if (hasSubmitted) {
          handleNextQuestion();
        }
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [selectedOption, hasSubmitted, currentQ]);

  const accuracyPercent = Math.round(
    (scoreMetrics.correctCount / (scoreMetrics.totalAnswered || 1)) * 100
  );

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm">
      {/* Header Bar */}
      <div className="flex flex-wrap items-center justify-between gap-3 pb-4 mb-5 border-b border-slate-200 dark:border-slate-800">
        <div className="flex items-center gap-2">
          <span className="text-xs text-slate-500 font-medium">
            Question <strong>{currentIndex + 1}</strong> of {SAMPLE_QUESTIONS.length}
          </span>
          <span className="bg-sky-100 text-sky-800 dark:bg-sky-900/60 dark:text-sky-300 text-xs font-bold px-2.5 py-0.5 rounded-full">
            {currentQ.system}
          </span>
        </div>

        <div className="flex items-center gap-4 text-xs font-semibold text-slate-600 dark:text-slate-400">
          <span>🔥 Streak: <strong className="text-emerald-600">{userStreak}</strong></span>
          <span>Accuracy: <strong className="text-sky-600">{accuracyPercent}%</strong></span>
        </div>
      </div>

      {/* Vignette */}
      <p className="text-slate-900 dark:text-slate-100 text-base leading-relaxed mb-6 font-normal">
        {currentQ.vignette}
      </p>

      {/* Options List */}
      <div className="space-y-3 mb-6">
        {currentQ.options.map((optionText, idx) => {
          const isSelected = selectedOption === idx;
          const isCorrectChoice = idx === currentQ.correctChoiceIndex;

          let optionStyle =
            "border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-800/60 hover:border-sky-500 hover:bg-sky-50/50 dark:hover:bg-slate-800 text-slate-800 dark:text-slate-200";

          if (!hasSubmitted && isSelected) {
            optionStyle =
              "border-sky-500 bg-sky-50 dark:bg-sky-950/40 ring-2 ring-sky-500/20 text-sky-950 dark:text-sky-200 font-semibold";
          } else if (hasSubmitted) {
            if (isCorrectChoice) {
              optionStyle =
                "border-emerald-500 bg-emerald-50 dark:bg-emerald-950/40 text-emerald-900 dark:text-emerald-200 font-bold ring-2 ring-emerald-500/30";
            } else if (isSelected && !isCorrectChoice) {
              optionStyle =
                "border-red-500 bg-red-50 dark:bg-red-950/40 text-red-900 dark:text-red-200 font-medium line-through opacity-90";
            } else {
              optionStyle = "border-slate-200 dark:border-slate-800 opacity-50";
            }
          }

          return (
            <button
              key={idx}
              onClick={() => handleSelectOption(idx)}
              disabled={hasSubmitted}
              className={`w-full text-left p-4 rounded-xl border transition-all flex items-center justify-between text-sm ${optionStyle}`}
            >
              <span>{optionText}</span>
              <span className="bg-slate-100 dark:bg-slate-700/60 text-slate-500 dark:text-slate-400 text-xs px-2 py-0.5 rounded font-mono font-bold">
                Press {idx + 1}
              </span>
            </button>
          );
        })}
      </div>

      {/* Submit Button & Status Indicator */}
      <div className="flex items-center justify-between pt-2">
        <div>
          {hasSubmitted && (
            <span
              className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold ${
                isCorrect
                  ? "bg-emerald-100 text-emerald-800 dark:bg-emerald-900/60 dark:text-emerald-300"
                  : "bg-red-100 text-red-800 dark:bg-red-900/60 dark:text-red-300"
              }`}
            >
              {isCorrect ? "✅ Correct (+10 pts)" : "❌ Incorrect"}
            </span>
          )}
        </div>

        {!hasSubmitted ? (
          <button
            onClick={handleSubmit}
            disabled={selectedOption === null}
            className={`px-6 py-2.5 rounded-xl text-sm font-semibold text-white transition-all ${
              selectedOption !== null
                ? "bg-gradient-to-r from-sky-500 to-sky-600 hover:opacity-90 shadow-md shadow-sky-500/20"
                : "bg-slate-300 dark:bg-slate-800 text-slate-500 cursor-not-allowed"
            }`}
          >
            Submit Answer (Enter)
          </button>
        ) : (
          <button
            onClick={handleNextQuestion}
            className="px-6 py-2.5 rounded-xl text-sm font-semibold text-white bg-gradient-to-r from-emerald-500 to-emerald-600 hover:opacity-90 shadow-md shadow-emerald-500/20 flex items-center gap-2"
          >
            Next Question ➔
          </button>
        )}
      </div>

      {/* Comprehensive Clinical Explanation */}
      {hasSubmitted && (
        <div className="mt-6 p-5 rounded-2xl bg-sky-50 dark:bg-slate-800/80 border border-sky-200 dark:border-sky-900/50 space-y-4">
          <div className="flex items-center gap-2 text-sky-900 dark:text-sky-300 font-bold text-sm">
            <span>🔬 Comprehensive Clinical Explanation</span>
          </div>

          <div className="text-xs text-slate-700 dark:text-slate-300 leading-relaxed space-y-2">
            <p>
              <strong className="text-emerald-600 dark:text-emerald-400">
                ✅ Correct Rationale:
              </strong>{" "}
              {currentQ.explanation.correctReason}
            </p>
            {currentQ.explanation.incorrectReasons.map((reason, rIdx) => (
              <p key={rIdx}>
                <strong className="text-red-500 dark:text-red-400">
                  ❌ Distractor Note:
                </strong>{" "}
                {reason}
              </p>
            ))}
          </div>

          {/* High Yield Pearl */}
          <div className="p-3.5 rounded-xl bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800/50">
            <div className="text-xs font-bold text-emerald-800 dark:text-emerald-300 mb-1">
              ⚡ HIGH-YIELD BOARD PEARL:
            </div>
            <div className="text-xs text-emerald-900 dark:text-emerald-200 leading-normal">
              {currentQ.boardPearl}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default QBankBlock;

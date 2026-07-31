"use client";

import React, { useState } from "react";

export const ClinicalCalculators: React.FC = () => {
  // eGFR State
  const [scr, setScr] = useState<string>("1.2");
  const [age, setAge] = useState<string>("58");
  const [gender, setGender] = useState<"female" | "male">("female");
  const [egfrResult, setEgfrResult] = useState<{
    value: number;
    stage: string;
  } | null>({
    value: 58,
    stage: "CKD Stage 3a (Mildly to Moderately Decreased)",
  });

  // Anion Gap State
  const [na, setNa] = useState<string>("138");
  const [cl, setCl] = useState<string>("100");
  const [hco3, setHco3] = useState<string>("12");
  const [agResult, setAgResult] = useState<{
    gap: number;
    isHigh: boolean;
  } | null>({
    gap: 26,
    isHigh: true,
  });

  // eGFR CKD-EPI 2021 Race-Free Equation
  const calculateEgfr = () => {
    const creatinine = parseFloat(scr);
    const ageNum = parseFloat(age);

    if (isNaN(creatinine) || creatinine <= 0 || isNaN(ageNum) || ageNum <= 0) {
      setEgfrResult(null);
      return;
    }

    let egfr = 0;
    if (gender === "female") {
      const kappa = 0.7;
      const alpha = creatinine <= kappa ? -0.241 : -1.2;
      const minVal = Math.min(creatinine / kappa, 1);
      const maxVal = Math.max(creatinine / kappa, 1);
      egfr = 142 * Math.pow(minVal, alpha) * Math.pow(maxVal, -1.2) * Math.pow(0.9938, ageNum) * 1.012;
    } else {
      const kappa = 0.9;
      const alpha = creatinine <= kappa ? -0.302 : -1.2;
      const minVal = Math.min(creatinine / kappa, 1);
      const maxVal = Math.max(creatinine / kappa, 1);
      egfr = 142 * Math.pow(minVal, alpha) * Math.pow(maxVal, -1.2) * Math.pow(0.9938, ageNum);
    }

    const rounded = Math.round(egfr);
    let stage = "";

    if (rounded >= 90) stage = "CKD Stage 1 (Normal or High GFR)";
    else if (rounded >= 60) stage = "CKD Stage 2 (Mildly Decreased GFR)";
    else if (rounded >= 45) stage = "CKD Stage 3a (Mildly to Moderately Decreased GFR)";
    else if (rounded >= 30) stage = "CKD Stage 3b (Moderately to Severely Decreased GFR)";
    else if (rounded >= 15) stage = "CKD Stage 4 (Severely Decreased GFR)";
    else stage = "CKD Stage 5 (Kidney Failure)";

    setEgfrResult({ value: rounded, stage });
  };

  // Anion Gap Calculation
  const calculateAnionGap = () => {
    const naNum = parseFloat(na);
    const clNum = parseFloat(cl);
    const hco3Num = parseFloat(hco3);

    if (isNaN(naNum) || isNaN(clNum) || isNaN(hco3Num)) {
      setAgResult(null);
      return;
    }

    const gap = Math.round((naNum - (clNum + hco3Num)) * 10) / 10;
    const isHigh = gap > 12;

    setAgResult({ gap, isHigh });
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm">
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-base font-bold text-slate-900 dark:text-slate-100 flex items-center gap-2">
          <span>🧪 Live Clinical Calculators & Lab Converters</span>
        </h3>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* eGFR Calculator */}
        <div className="p-5 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700/80 flex flex-col justify-between">
          <div>
            <div className="text-sm font-bold text-sky-800 dark:text-sky-300 mb-4">
              1. eGFR (CKD-EPI 2021 Race-Free Equation)
            </div>

            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Serum Creatinine (mg/dL):
                </label>
                <input
                  type="number"
                  step="0.1"
                  value={scr}
                  onChange={(e) => setScr(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Age (Years):
                </label>
                <input
                  type="number"
                  value={age}
                  onChange={(e) => setAge(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Biological Gender:
                </label>
                <div className="grid grid-cols-2 gap-2">
                  <button
                    type="button"
                    onClick={() => setGender("female")}
                    className={`py-2 rounded-xl text-xs font-bold border ${
                      gender === "female"
                        ? "bg-sky-500 text-white border-sky-500"
                        : "bg-white dark:bg-slate-900 border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300"
                    }`}
                  >
                    Female
                  </button>
                  <button
                    type="button"
                    onClick={() => setGender("male")}
                    className={`py-2 rounded-xl text-xs font-bold border ${
                      gender === "male"
                        ? "bg-sky-500 text-white border-sky-500"
                        : "bg-white dark:bg-slate-900 border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300"
                    }`}
                  >
                    Male
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div className="mt-5 pt-4 border-t border-slate-200 dark:border-slate-700">
            <button
              onClick={calculateEgfr}
              className="w-full py-2.5 rounded-xl bg-sky-500 hover:bg-sky-600 text-white text-xs font-bold transition-all shadow-sm mb-3"
            >
              Calculate eGFR
            </button>

            {egfrResult && (
              <div className="p-3 rounded-xl bg-sky-100/80 dark:bg-sky-950/60 border border-sky-200 dark:border-sky-800 text-center">
                <div className="text-xl font-extrabold text-sky-900 dark:text-sky-200">
                  {egfrResult.value} <span className="text-xs font-semibold">mL/min/1.73m²</span>
                </div>
                <div className="text-xs font-bold text-sky-800 dark:text-sky-300 mt-1">
                  {egfrResult.stage}
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Anion Gap Calculator */}
        <div className="p-5 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700/80 flex flex-col justify-between">
          <div>
            <div className="text-sm font-bold text-sky-800 dark:text-sky-300 mb-4">
              2. Serum Anion Gap Calculator
            </div>

            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Serum Sodium (Na⁺) [mEq/L]:
                </label>
                <input
                  type="number"
                  value={na}
                  onChange={(e) => setNa(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Serum Chloride (Cl⁻) [mEq/L]:
                </label>
                <input
                  type="number"
                  value={cl}
                  onChange={(e) => setCl(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                  Serum Bicarbonate (HCO₃⁻) [mEq/L]:
                </label>
                <input
                  type="number"
                  value={hco3}
                  onChange={(e) => setHco3(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
                />
              </div>
            </div>
          </div>

          <div className="mt-5 pt-4 border-t border-slate-200 dark:border-slate-700">
            <button
              onClick={calculateAnionGap}
              className="w-full py-2.5 rounded-xl bg-sky-500 hover:bg-sky-600 text-white text-xs font-bold transition-all shadow-sm mb-3"
            >
              Calculate Anion Gap
            </button>

            {agResult && (
              <div
                className={`p-3 rounded-xl border text-center ${
                  agResult.isHigh
                    ? "bg-red-50 dark:bg-red-950/60 border-red-300 dark:border-red-800 text-red-900 dark:text-red-200"
                    : "bg-emerald-50 dark:bg-emerald-950/60 border-emerald-300 dark:border-emerald-800 text-emerald-900 dark:text-emerald-200"
                }`}
              >
                <div className="text-xl font-extrabold">
                  {agResult.gap} <span className="text-xs font-semibold">mEq/L</span>
                </div>
                <div className="text-xs font-bold mt-1">
                  {agResult.isHigh
                    ? "⚠️ High Anion Gap Metabolic Acidosis (> 12 mEq/L)"
                    : "✅ Normal Anion Gap (8-12 mEq/L)"}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ClinicalCalculators;

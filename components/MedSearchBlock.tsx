"use client";

import React, { useState } from "react";

export const MedSearchBlock: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [searchResult, setSearchResult] = useState<string | null>(null);
  const [searchHistory, setSearchHistory] = useState<string[]>([
    "SGLT2 inhibitors in Heart Failure with Preserved Ejection Fraction (HFpEF)",
    "GLP-1 receptor agonists and MACE reduction in Type 2 Diabetes",
    "DOACs vs Warfarin in Non-Valvular Atrial Fibrillation with CKD Stage 4",
  ]);

  const handleSearch = async (queryToRun?: string) => {
    const q = queryToRun || searchQuery;
    if (!q.trim()) return;

    setIsLoading(true);
    setSearchResult(null);

    try {
      const res = await fetch("/api/ai/search", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query: q }),
      });

      const data = await res.json();
      if (data && data.result) {
        setSearchResult(data.result);
        if (!searchHistory.includes(q)) {
          setSearchHistory((prev) => [q, ...prev.slice(0, 4)]);
        }
      } else {
        setSearchResult("⚠️ Search synthesis error. Please try again.");
      }
    } catch (err) {
      setSearchResult("⚠️ Network connection error. Unable to reach clinical synthesis engine.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-6 shadow-sm">
      {/* Header Banner */}
      <div className="flex items-center justify-between pb-4 mb-5 border-b border-slate-200 dark:border-slate-800">
        <div>
          <div className="flex items-center gap-2">
            <span className="text-xl">🔍</span>
            <h2 className="text-lg font-bold text-slate-900 dark:text-slate-100">
              MedSearch-AI • Evidence Synthesis Engine
            </h2>
            <span className="bg-sky-100 text-sky-800 dark:bg-sky-950 dark:text-sky-300 text-xs font-extrabold px-2.5 py-0.5 rounded-full">
              MED-PERPLEXITY
            </span>
          </div>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            Multi-source medical search, OCEBM evidence ranking & inline peer-reviewed citations.
          </p>
        </div>
      </div>

      {/* Quick Search Chips */}
      <div className="mb-4">
        <span className="text-xs font-bold text-slate-500 dark:text-slate-400 block mb-2">
          HIGH-YIELD CLINICAL SEARCHES:
        </span>
        <div className="flex flex-wrap gap-2">
          {searchHistory.map((item, idx) => (
            <button
              key={idx}
              onClick={() => {
                setSearchQuery(item);
                handleSearch(item);
              }}
              className="px-3 py-1.5 rounded-xl bg-slate-100 dark:bg-slate-800 hover:bg-sky-100 dark:hover:bg-sky-950 text-slate-700 dark:text-slate-300 text-xs font-medium transition-all"
            >
              🔎 {item}
            </button>
          ))}
        </div>
      </div>

      {/* Input Form */}
      <div className="flex gap-2 mb-6">
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSearch()}
          placeholder="Ask a complex clinical, pharmacological, or guidelines query..."
          className="flex-1 px-4 py-3 rounded-xl border border-slate-300 dark:border-slate-700 bg-white dark:bg-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
        />
        <button
          onClick={() => handleSearch()}
          disabled={isLoading || !searchQuery.trim()}
          className="px-6 py-3 rounded-xl bg-gradient-to-r from-sky-500 to-sky-600 hover:opacity-90 text-white text-sm font-bold transition-all disabled:opacity-50 shadow-md shadow-sky-500/20"
        >
          {isLoading ? "Synthesizing..." : "Search Evidence"}
        </button>
      </div>

      {/* Search Output View */}
      {isLoading && (
        <div className="p-8 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-800 text-center animate-pulse">
          <div className="text-2xl mb-2">⚡</div>
          <div className="text-sm font-bold text-sky-600 dark:text-sky-400">
            Querying PubMed, FDA, WHO, Cochrane & Guideline Databases...
          </div>
          <div className="text-xs text-slate-400 mt-1">
            Applying Oxford Centre for Evidence-Based Medicine (OCEBM) hierarchy & generating inline citations.
          </div>
        </div>
      )}

      {searchResult && !isLoading && (
        <div className="p-6 rounded-2xl bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 space-y-4">
          <div className="text-xs font-bold text-emerald-600 dark:text-emerald-400 flex items-center gap-1.5 mb-2">
            <span>✅ Synthesized Evidence Response (MedSearch-AI)</span>
          </div>

          <div
            className="prose dark:prose-invert max-w-none text-sm text-slate-800 dark:text-slate-200 leading-relaxed whitespace-pre-wrap font-sans"
            dangerouslySetInnerHTML={{ __html: formatMarkdown(searchResult) }}
          />
        </div>
      )}
    </div>
  );
};

// Helper markdown formatting function for preview
function formatMarkdown(text: string): string {
  let formatted = text
    .replace(/### (.*?)\n/g, '<h3 className="text-base font-bold text-sky-800 dark:text-sky-300 mt-4 mb-2">$1</h3>')
    .replace(/#### (.*?)\n/g, '<h4 className="text-sm font-bold text-slate-800 dark:text-slate-200 mt-3 mb-1">$1</h4>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\* (.*?)\n/g, '• $1<br/>')
    .replace(/\[(\d+)\]/g, '<sup className="text-sky-600 dark:text-sky-400 font-bold px-0.5">[$1]</sup>');

  return formatted;
}

export default MedSearchBlock;

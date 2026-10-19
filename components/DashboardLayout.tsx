import React, { useState, useEffect } from 'react';

export interface DashboardLayoutProps {
  children?: React.ReactNode;
  userRole?: 'STUDENT' | 'CLINICIAN' | 'ADMIN' | 'VIEWER';
  userName?: string;
  daysRemaining?: number;
}

export const DashboardLayout: React.FC<DashboardLayoutProps> = ({
  children,
  userRole = 'STUDENT',
  userName = 'Dr. Ese',
  daysRemaining = 11
}) => {
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [activeTab, setActiveTab] = useState<'dashboard' | 'notes' | 'tutor' | 'qbank' | 'flashcards'>('dashboard');

  const toggleTheme = () => {
    const next = theme === 'light' ? 'dark' : 'light';
    setTheme(next);
    if (typeof document !== 'undefined') {
      document.documentElement.setAttribute('data-theme', next);
    }
  };

  return (
    <div className={`min-h-screen flex flex-col font-sans transition-colors duration-200 ${
      theme === 'dark' ? 'bg-[#0B0F17] text-[#F1F5F9]' : 'bg-[#F8FAFC] text-[#0F172A]'
    }`}>
      {/* PERSISTENT 14-DAY TRIAL COUNTDOWN BANNER */}
      <div className="bg-gradient-to-r from-teal-600 via-sky-600 to-indigo-600 text-white px-4 py-2 text-xs font-semibold flex items-center justify-between shadow-sm z-50">
        <div className="flex items-center gap-2">
          <span className="bg-white/20 px-2 py-0.5 rounded-full text-[10px] uppercase tracking-wider">Free Trial</span>
          <span>⏳ <strong>{daysRemaining} Days Remaining</strong> in your MedColleague Pro Clinical Trial.</span>
        </div>
        <div className="flex items-center gap-3">
          <span className="hidden md:inline text-white/80 text-[11px]">HIPAA-Compliant • 256-bit SSL</span>
          <button 
            className="bg-white text-teal-800 hover:bg-teal-50 px-3 py-1 rounded-md text-xs font-bold transition shadow-sm"
            onClick={() => window.open('/api/stripe/checkout', '_blank')}
          >
            Upgrade to Pro ➔
          </button>
        </div>
      </div>

      <div className="flex flex-1 relative">
        {/* SIDEBAR NAVIGATION */}
        <aside className={`w-64 border-r transition-all duration-300 flex flex-col justify-between ${
          theme === 'dark' ? 'bg-[#151C2C] border-[#1E293B]' : 'bg-[#FFFFFF] border-[#E2E8F0]'
        }`}>
          <div>
            {/* BRAND HEADER */}
            <div className="p-5 border-b border-inherit flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-teal-500 to-sky-600 flex items-center justify-center text-white text-lg font-bold shadow-md">
                🩺
              </div>
              <div>
                <div className="font-extrabold text-sm tracking-tight text-inherit">MedColleague</div>
                <div className="text-[11px] text-gray-500">Clinical & Board Suite</div>
              </div>
            </div>

            {/* LIVE UPTIME & COMPLIANCE BADGE */}
            <div className="mx-4 my-3 px-3 py-1.5 rounded-lg bg-emerald-50 dark:bg-emerald-950/40 border border-emerald-200 dark:border-emerald-800/50 flex items-center gap-2 text-[11px] text-emerald-700 dark:text-emerald-300">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
              <span className="font-semibold">99.98% Systems Active</span>
            </div>

            {/* NAVIGATION MENU */}
            <nav className="p-3 space-y-1">
              {[
                { id: 'dashboard', label: 'Dashboard', icon: '🏠' },
                { id: 'notes', label: 'My Clinical Notes', icon: '📝', badge: 'New' },
                { id: 'tutor', label: 'Dr. Ese AI Tutor', icon: '🎓' },
                { id: 'qbank', label: 'USMLE / NCLEX QBank', icon: '📋' },
                { id: 'flashcards', label: 'SM-2 Flashcards', icon: '⚡' },
              ].map(item => (
                <button
                  key={item.id}
                  onClick={() => setActiveTab(item.id as any)}
                  className={`w-full flex items-center justify-between px-3 py-2.5 rounded-lg text-xs font-semibold transition ${
                    activeTab === item.id
                      ? 'bg-teal-600 text-white shadow-sm'
                      : 'text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-slate-800'
                  }`}
                >
                  <div className="flex items-center gap-2.5">
                    <span className="text-base">{item.icon}</span>
                    <span>{item.label}</span>
                  </div>
                  {item.badge && (
                    <span className="bg-teal-100 text-teal-800 dark:bg-teal-900/60 dark:text-teal-300 px-1.5 py-0.5 rounded text-[10px] font-bold">
                      {item.badge}
                    </span>
                  )}
                </button>
              ))}
            </nav>
          </div>

          {/* SIDEBAR FOOTER & THEME TOGGLE SWITCH */}
          <div className="p-4 border-t border-inherit space-y-3">
            {/* THEME TOGGLE SWITCH */}
            <div className="flex items-center justify-between px-2 py-1.5 rounded-lg bg-gray-100 dark:bg-slate-800">
              <span className="text-xs font-medium text-gray-600 dark:text-gray-300 flex items-center gap-1.5">
                {theme === 'light' ? '☀️ Light' : '🌙 Dark'} Mode
              </span>
              <button
                onClick={toggleTheme}
                aria-label="Toggle theme"
                className={`w-11 h-6 rounded-full transition-colors relative flex items-center px-1 ${
                  theme === 'dark' ? 'bg-teal-600 justify-end' : 'bg-gray-300 justify-start'
                }`}
              >
                <div className="w-4 h-4 rounded-full bg-white shadow-md"></div>
              </button>
            </div>

            {/* USER INFO */}
            <div className="flex items-center gap-2.5 px-2">
              <div className="w-8 h-8 rounded-full bg-teal-600 text-white flex items-center justify-center text-xs font-bold">
                {userName.charAt(0)}
              </div>
              <div className="text-left flex-1 min-w-0">
                <div className="text-xs font-bold truncate text-inherit">{userName}</div>
                <div className="text-[10px] text-teal-600 dark:text-teal-400 font-semibold uppercase">{userRole}</div>
              </div>
            </div>
          </div>
        </aside>

        {/* MAIN BODY CONTENT */}
        <main className="flex-1 p-6 max-w-7xl mx-auto w-full">
          {children}
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;

/**
 * Unified LocalStorage Utility with serialization, deserialization,
 * and error handling for QBank, Flashcards, and user preferences.
 */

export interface QBankState {
  currentQIdx: number;
  selectedChoiceIdx: number | null;
  hasSubmitted: boolean;
  userStreak: number;
  scoreMetrics: {
    totalAnswered: number;
    correctCount: number;
  };
  isFlagged?: boolean;
}

export interface FlashcardState {
  fcIdx: number;
  isFlipped: boolean;
  reviewedCount: number;
  lastRatingFeedback?: string | null;
}

const QBANK_STORAGE_KEY = "medcolleague_qbank_state";
const FLASHCARD_STORAGE_KEY = "medcolleague_flashcard_state";

/**
 * Safely saves data to localStorage.
 */
export function setLocalStorageItem<T>(key: string, value: T): boolean {
  if (typeof window === "undefined") return false;
  try {
    const serialized = JSON.stringify(value);
    window.localStorage.setItem(key, serialized);
    return true;
  } catch (error) {
    console.error(`[Storage Utility] Error saving key "${key}":`, error);
    return false;
  }
}

/**
 * Safely retrieves data from localStorage with fallback default.
 */
export function getLocalStorageItem<T>(key: string, defaultValue: T): T {
  if (typeof window === "undefined") return defaultValue;
  try {
    const item = window.localStorage.getItem(key);
    if (item === null) return defaultValue;
    return JSON.parse(item) as T;
  } catch (error) {
    console.error(`[Storage Utility] Error parsing key "${key}":`, error);
    return defaultValue;
  }
}

/**
 * Removes an item from localStorage.
 */
export function removeLocalStorageItem(key: string): boolean {
  if (typeof window === "undefined") return false;
  try {
    window.localStorage.removeItem(key);
    return true;
  } catch (error) {
    console.error(`[Storage Utility] Error removing key "${key}":`, error);
    return false;
  }
}

// QBank specific persistence helpers
export function saveQBankStorage(state: QBankState): boolean {
  return setLocalStorageItem<QBankState>(QBANK_STORAGE_KEY, state);
}

export function loadQBankStorage(defaultState: QBankState): QBankState {
  return getLocalStorageItem<QBankState>(QBANK_STORAGE_KEY, defaultState);
}

// Flashcard specific persistence helpers
export function saveFlashcardStorage(state: FlashcardState): boolean {
  return setLocalStorageItem<FlashcardState>(FLASHCARD_STORAGE_KEY, state);
}

export function loadFlashcardStorage(defaultState: FlashcardState): FlashcardState {
  return getLocalStorageItem<FlashcardState>(FLASHCARD_STORAGE_KEY, defaultState);
}

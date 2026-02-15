import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export type ReaderTheme = 'light' | 'dark' | 'sepia';
export type PageMode = 'scroll' | 'pagination';

interface ReaderSettings {
  fontSize: number;
  lineHeight: number;
  fontFamily: string;
  theme: ReaderTheme;
  pageMode: PageMode;
}

interface ReaderState {
  // 当前阅读状态
  currentBookId: number | null;
  currentChapter: number;
  scrollPosition: number;
  
  // 设置
  settings: ReaderSettings;
  
  // Actions
  setCurrentBook: (bookId: number, chapter: number) => void;
  setCurrentChapter: (chapter: number) => void;
  setScrollPosition: (position: number) => void;
  updateSettings: (settings: Partial<ReaderSettings>) => void;
  reset: () => void;
}

const defaultSettings: ReaderSettings = {
  fontSize: 18,
  lineHeight: 1.8,
  fontFamily: 'system-ui, sans-serif',
  theme: 'light',
  pageMode: 'scroll',
};

export const useReaderStore = create<ReaderState>()(
  persist(
    (set) => ({
      currentBookId: null,
      currentChapter: 0,
      scrollPosition: 0,
      settings: defaultSettings,

      setCurrentBook: (bookId, chapter) =>
        set({ currentBookId: bookId, currentChapter: chapter, scrollPosition: 0 }),

      setCurrentChapter: (chapter) => set({ currentChapter: chapter }),

      setScrollPosition: (position) => set({ scrollPosition: position }),

      updateSettings: (newSettings) =>
        set((state) => ({
          settings: { ...state.settings, ...newSettings },
        })),

      reset: () =>
        set({
          currentBookId: null,
          currentChapter: 0,
          scrollPosition: 0,
        }),
    }),
    {
      name: 'reader-settings',
    }
  )
);

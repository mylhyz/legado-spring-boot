export interface Book {
  id: number;
  bookUrl: string;
  tocUrl?: string;
  origin: string;
  originName?: string;
  name: string;
  author?: string;
  kind?: string;
  customTag?: string;
  coverUrl?: string;
  customCoverUrl?: string;
  intro?: string;
  customIntro?: string;
  charset?: string;
  type: number;
  groupId: number;
  latestChapterTitle?: string;
  latestChapterTime?: number;
  lastCheckTime?: number;
  totalChapterNum: number;
  durChapterTitle?: string;
  durChapterIndex: number;
  durChapterPos: number;
  durChapterTime?: number;
  wordCount?: string;
  canUpdate: boolean;
  order: number;
  originOrder: number;
  variable?: string;
  readConfig?: ReadConfig;
  createdAt?: string;
  updatedAt?: string;
}

export interface ReadConfig {
  reverseToc?: boolean;
  pageAnim?: number;
  reSegment?: boolean;
  imageStyle?: string;
  useReplaceRule?: boolean;
  ttsEngine?: string;
  splitLongChapter?: boolean;
  readSimulating?: boolean;
  startDate?: string;
  startChapter?: number;
  dailyChapters?: number;
}

export interface BookChapter {
  id: number;
  bookId: number;
  chapterIndex: number;
  title?: string;
  url?: string;
  content?: string;
  wordCount?: number;
  isVip: boolean;
  isPay: boolean;
  createdAt?: string;
}

export interface BookSource {
  id: number;
  sourceName: string;
  sourceUrl: string;
  sourceIcon?: string;
  sourceGroup?: string;
  enabled: boolean;
  enabledExplore: boolean;
  weight: number;
  customOrder: number;
  loginUrl?: string;
  loginUi?: string;
  loginCheckJs?: string;
  bookUrlPattern?: string;
  header?: string;
  searchUrl?: string;
  exploreUrl?: string;
  ruleSearch?: string;
  ruleBookInfo?: string;
  ruleToc?: string;
  ruleContent?: string;
  ruleReview?: string;
  lastUpdateTime?: number;
  respondTime?: number;
  contentReplaceRule?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BookGroup {
  id: number;
  groupName: string;
  groupIcon?: string;
  order: number;
  show: boolean;
}

export interface SearchResult {
  name: string;
  author?: string;
  intro?: string;
  coverUrl?: string;
  bookUrl: string;
  latestChapterTitle?: string;
  wordCount?: string;
  kind?: string;
  sourceName: string;
  sourceUrl: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email?: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface User {
  id: number;
  username: string;
  email?: string;
  nickname?: string;
  avatar?: string;
  enabled: boolean;
  roles: string;
}

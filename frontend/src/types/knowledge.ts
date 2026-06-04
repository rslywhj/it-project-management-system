/** 文章状态 */
export type ArticleStatus = 'draft' | 'published' | 'archived'

/** 文章分类 */
export interface KnowledgeCategory {
  id: number
  projectId?: number
  name: string
  parentId?: number
  sortOrder: number
  icon?: string
  articleCount?: number
  createdAt: string
}

/** 文章分类创建请求 */
export interface KnowledgeCategoryCreateRequest {
  name: string
  parentId?: number
  sortOrder?: number
  icon?: string
}

/** 知识库文章 */
export interface KnowledgeArticle {
  id: number
  projectId?: number
  categoryId?: number
  categoryName?: string
  title: string
  content: string
  summary?: string
  tags?: string[]
  status: ArticleStatus
  viewCount: number
  authorId: number
  authorName?: string
  createdAt: string
  updatedAt: string
}

/** 文章创建请求 */
export interface KnowledgeArticleCreateRequest {
  categoryId?: number
  title: string
  content: string
  summary?: string
  tags?: string[]
  status?: ArticleStatus
}

/** 文章搜索结果 */
export interface ArticleSearchResult {
  id: number
  title: string
  summary?: string
  categoryName?: string
  tags?: string[]
  highlight?: string
  score: number
}

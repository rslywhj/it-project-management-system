/** 文章状态 */
export type ArticleStatus = 'draft' | 'published' | 'archived'

/** 文章类别（后端为字符串枚举，非独立实体） */
export type ArticleCategory = 'experience' | 'best_practice' | 'lesson_learned' | 'template' | 'guide'

/** 知识库文章 */
export interface KnowledgeArticle {
  id: number
  projectId?: number
  title: string
  content: string
  category?: ArticleCategory
  tags?: string
  status: ArticleStatus
  viewCount?: number
  authorId?: number
  createdAt: string
  updatedAt?: string
}

/** 文章创建请求 */
export interface KnowledgeArticleCreateRequest {
  title: string
  content?: string
  category?: ArticleCategory
  tags?: string
}

/** 模板 */
export interface KnowledgeTemplate {
  id: number
  name: string
  description?: string
  type: string
  content?: string
  status?: string
  isSystem?: boolean
  createdAt: string
}

/** 模板创建请求 */
export interface KnowledgeTemplateCreateRequest {
  name: string
  description?: string
  type: string
  content?: string
}

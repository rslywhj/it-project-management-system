<template>
  <div class="knowledge-list">
    <el-row :gutter="16">
      <!-- 左侧类别筛选 -->
      <el-col :span="5">
        <el-card shadow="never" class="category-card">
          <template #header>
            <div class="category-header">
              <span>知识类别</span>
            </div>
          </template>
          <div class="category-list">
            <div
              class="category-item"
              :class="{ active: !selectedCategory }"
              @click="handleCategorySelect(null)"
            >
              <span>全部</span>
            </div>
            <div
              v-for="cat in categoryOptions"
              :key="cat.value"
              class="category-item"
              :class="{ active: selectedCategory === cat.value }"
              @click="handleCategorySelect(cat.value)"
            >
              <span>{{ cat.label }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧文章列表 -->
      <el-col :span="19">
        <el-card shadow="never">
          <template #header>
            <div class="toolbar">
              <div class="toolbar-left">
                <el-input v-model="searchKeyword" placeholder="搜索文章..." prefix-icon="Search" clearable style="width: 260px" @clear="handleSearch" @keyup.enter="handleSearch" />
                <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 100px" @change="handleSearch">
                  <el-option label="草稿" value="draft" />
                  <el-option label="已发布" value="published" />
                  <el-option label="已归档" value="archived" />
                </el-select>
              </div>
              <el-button type="primary" size="small" @click="handleCreateArticle">
                <el-icon><EditPen /></el-icon>写文章
              </el-button>
            </div>
          </template>

          <div v-loading="loading">
            <div v-for="article in articleList" :key="article.id" class="article-item" @click="handleViewArticle(article)">
              <div class="article-header">
                <h3 class="article-title">{{ article.title }}</h3>
                <el-tag :type="articleStatusType(article.status)" size="small">{{ articleStatusLabel(article.status) }}</el-tag>
              </div>
              <div class="article-meta">
                <span v-if="article.category" class="meta-item">
                  <el-icon><Folder /></el-icon> {{ categoryLabel(article.category) }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon> {{ article.viewCount ?? 0 }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon> {{ article.createdAt }}
                </span>
                <span v-if="article.tags" class="meta-item">
                  <el-tag v-for="tag in parseTags(article.tags)" :key="tag" size="small" type="info" style="margin-right: 4px">{{ tag }}</el-tag>
                </span>
              </div>
            </div>
            <el-empty v-if="!loading && articleList.length === 0" description="暂无文章" />
          </div>

          <div class="pagination-wrapper">
            <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSearch" @current-change="handleSearch" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 文章编辑对话框 -->
    <el-dialog v-model="editorVisible" :title="isEditArticle ? '编辑文章' : '写文章'" width="800px" destroy-on-close top="5vh">
      <el-form ref="articleFormRef" :model="articleFormData" :rules="articleFormRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="articleFormData.title" placeholder="文章标题" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="articleFormData.category" placeholder="选择类别" clearable>
            <el-option v-for="cat in categoryOptions" :key="cat.value" :label="cat.label" :value="cat.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="articleFormData.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="articleFormData.content" type="textarea" :rows="12" placeholder="文章内容（支持 Markdown）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="articleSubmitLoading" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 文章详情抽屉 -->
    <el-drawer v-model="detailDrawerVisible" :title="currentArticle?.title" size="600px">
      <div v-if="currentArticle" class="article-detail">
        <div class="detail-meta">
          <el-tag :type="articleStatusType(currentArticle.status)" size="small">{{ articleStatusLabel(currentArticle.status) }}</el-tag>
          <span v-if="currentArticle.category">{{ categoryLabel(currentArticle.category) }}</span>
          <span>{{ currentArticle.createdAt }}</span>
          <span>浏览 {{ currentArticle.viewCount ?? 0 }}</span>
        </div>
        <div v-if="currentArticle.tags" class="detail-tags">
          <el-tag v-for="tag in parseTags(currentArticle.tags)" :key="tag" size="small" type="info">{{ tag }}</el-tag>
        </div>
        <el-divider />
        <div class="detail-content">{{ currentArticle.content }}</div>
        <div class="detail-actions">
          <el-button v-permission="'knowledge:edit'" type="primary" @click="handleEditArticle(currentArticle!)">编辑</el-button>
          <el-button v-if="currentArticle.status === 'published'" @click="handleArchiveArticle(currentArticle!)">归档</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getArticleList, createArticle, updateArticle, archiveArticle } from '@/api/knowledge'
import type { KnowledgeArticle, KnowledgeArticleCreateRequest, ArticleCategory } from '@/types/knowledge'

const props = defineProps<{ projectId: number }>()

const categoryOptions = [
  { value: 'experience', label: '经验分享' },
  { value: 'best_practice', label: '最佳实践' },
  { value: 'lesson_learned', label: '经验教训' },
  { value: 'template', label: '模板' },
  { value: 'guide', label: '指南' },
]

const categoryMap: Record<string, string> = {
  experience: '经验分享', best_practice: '最佳实践', lesson_learned: '经验教训', template: '模板', guide: '指南',
}

function categoryLabel(c: string) { return categoryMap[c] ?? c }

function parseTags(tags: string): string[] {
  return tags.split(',').map(t => t.trim()).filter(Boolean)
}

const selectedCategory = ref<string | null>(null)

// 文章列表
const loading = ref(false)
const articleList = ref<KnowledgeArticle[]>([])
const total = ref(0)
const searchKeyword = ref('')
const queryParams = reactive({ page: 1, size: 10, status: '', category: '' as string })

// 编辑器
const editorVisible = ref(false)
const isEditArticle = ref(false)
const articleSubmitLoading = ref(false)
const articleFormRef = ref<FormInstance>()
const editingArticleId = ref<number | null>(null)
const articleFormData = reactive<KnowledgeArticleCreateRequest>({
  title: '', content: '', category: undefined, tags: '',
})
const articleFormRules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
}

// 详情
const detailDrawerVisible = ref(false)
const currentArticle = ref<KnowledgeArticle | null>(null)

const articleStatusMap: Record<string, string> = { draft: '草稿', published: '已发布', archived: '已归档' }
const articleStatusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = { draft: 'info', published: 'success', archived: 'danger' }

function articleStatusLabel(s: string) { return articleStatusMap[s] ?? s }
function articleStatusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return articleStatusTypeMap[s] ?? 'info' }

async function loadArticles() {
  loading.value = true
  try {
    if (selectedCategory.value) queryParams.category = selectedCategory.value
    else queryParams.category = ''
    if (searchKeyword.value) (queryParams as any).keyword = searchKeyword.value
    else delete (queryParams as any).keyword
    const data = await getArticleList(props.projectId, queryParams)
    articleList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadArticles() }

function handleCategorySelect(cat: string | null) {
  selectedCategory.value = cat
  handleSearch()
}

function handleCreateArticle() {
  isEditArticle.value = false
  editingArticleId.value = null
  Object.assign(articleFormData, { title: '', content: '', category: selectedCategory.value ?? undefined, tags: '' })
  editorVisible.value = true
}

function handleEditArticle(article: KnowledgeArticle) {
  isEditArticle.value = true
  editingArticleId.value = article.id
  Object.assign(articleFormData, {
    title: article.title, content: article.content,
    category: article.category, tags: article.tags ?? '',
  })
  detailDrawerVisible.value = false
  editorVisible.value = true
}

async function handleSave() {
  const valid = await articleFormRef.value?.validate().catch(() => false)
  if (!valid) return
  articleSubmitLoading.value = true
  try {
    if (isEditArticle.value && editingArticleId.value) {
      await updateArticle(editingArticleId.value, articleFormData)
      ElMessage.success('更新成功')
    } else {
      await createArticle(props.projectId, articleFormData)
      ElMessage.success('创建成功')
    }
    editorVisible.value = false
    loadArticles()
  } catch { /* handled */ } finally { articleSubmitLoading.value = false }
}

function handleViewArticle(article: KnowledgeArticle) {
  currentArticle.value = article
  detailDrawerVisible.value = true
}

async function handleArchiveArticle(article: KnowledgeArticle) {
  try {
    await ElMessageBox.confirm(`确定归档「${article.title}」？`, '提示', { type: 'warning' })
    await archiveArticle(article.id)
    ElMessage.success('已归档')
    detailDrawerVisible.value = false
    loadArticles()
  } catch { /* cancelled */ }
}

onMounted(() => { loadArticles() })
</script>

<style scoped lang="scss">
.category-card { height: calc(100vh - 200px); overflow-y: auto; }
.category-header { display: flex; justify-content: space-between; align-items: center; }
.category-list { display: flex; flex-direction: column; gap: 4px; }
.category-item {
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  &:hover { background: #f5f7fa; }
  &.active { background: #ecf5ff; color: #409eff; font-weight: 500; }
}

.toolbar { display: flex; justify-content: space-between; align-items: center; .toolbar-left { display: flex; gap: 8px; } }

.article-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  &:hover { background: #fafafa; }
  &:last-child { border-bottom: none; }

  .article-header { display: flex; align-items: center; gap: 8px; }
  .article-title { margin: 0; font-size: 16px; color: #303133; flex: 1; }
  .article-meta {
    margin-top: 8px;
    display: flex;
    gap: 16px;
    color: #909399;
    font-size: 13px;
    .meta-item { display: flex; align-items: center; gap: 4px; }
  }
}

.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 16px; }

.article-detail {
  .detail-meta { display: flex; gap: 12px; align-items: center; color: #909399; font-size: 13px; }
  .detail-tags { margin-top: 8px; display: flex; gap: 4px; }
  .detail-content { white-space: pre-wrap; line-height: 1.8; color: #303133; }
  .detail-actions { margin-top: 24px; }
}
</style>

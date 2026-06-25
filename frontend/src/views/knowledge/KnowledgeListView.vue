<template>
  <div class="knowledge-list">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>

    <template v-else>
    <el-row :gutter="16">
      <!-- 左侧分类树 -->
      <el-col :span="5">
        <el-card shadow="never" class="category-card">
          <template #header>
            <div class="category-header">
              <span>知识分类</span>
              <el-button link type="primary" size="small" @click="handleCreateCategory">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </template>
          <el-tree
            :data="categoryTree"
            node-key="id"
            default-expand-all
            highlight-current
            :props="{ children: 'children', label: 'name' }"
            @node-click="handleCategoryClick"
          >
            <template #default="{ data }">
              <div class="category-node">
                <span>{{ data.name }}</span>
                <span class="count">({{ data.articleCount ?? 0 }})</span>
              </div>
            </template>
          </el-tree>
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
              <p v-if="article.summary" class="article-summary">{{ article.summary }}</p>
              <div class="article-meta">
                <span v-if="article.categoryName" class="meta-item">
                  <el-icon><Folder /></el-icon> {{ article.categoryName }}
                </span>
                <span class="meta-item">
                  <el-icon><User /></el-icon> {{ article.authorName }}
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon> {{ article.viewCount }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon> {{ article.updatedAt }}
                </span>
                <span v-if="article.tags?.length" class="meta-item">
                  <el-tag v-for="tag in article.tags" :key="tag" size="small" type="info" style="margin-right: 4px">{{ tag }}</el-tag>
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
        <el-form-item label="分类">
          <el-select v-model="articleFormData.categoryId" placeholder="选择分类" clearable>
            <el-option v-for="cat in flatCategories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="articleFormData.summary" type="textarea" :rows="2" placeholder="文章摘要" />
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="articleFormData.tags" multiple filterable allow-create placeholder="输入标签后回车" style="width: 100%">
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="articleFormData.content" type="textarea" :rows="12" placeholder="文章内容（支持 Markdown）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button @click="handleSaveDraft">保存草稿</el-button>
        <el-button type="primary" :loading="articleSubmitLoading" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>

    <!-- 文章详情抽屉 -->
    <el-drawer v-model="detailDrawerVisible" :title="currentArticle?.title" size="600px">
      <div v-if="currentArticle" class="article-detail">
        <div class="detail-meta">
          <el-tag :type="articleStatusType(currentArticle.status)" size="small">{{ articleStatusLabel(currentArticle.status) }}</el-tag>
          <span>{{ currentArticle.authorName }}</span>
          <span>{{ currentArticle.updatedAt }}</span>
          <span>浏览 {{ currentArticle.viewCount }}</span>
        </div>
        <div v-if="currentArticle.tags?.length" class="detail-tags">
          <el-tag v-for="tag in currentArticle.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
        </div>
        <el-divider />
        <div class="detail-content">{{ currentArticle.content }}</div>
        <div class="detail-actions">
          <el-button v-permission="'knowledge:edit'" type="primary" @click="handleEditArticle(currentArticle!)">编辑</el-button>
          <el-button v-if="currentArticle.status === 'published'" @click="handleArchiveArticle(currentArticle!)">归档</el-button>
        </div>
      </div>
    </el-drawer>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getCategoryTree, createCategory, getArticleList, createArticle, updateArticle, archiveArticle } from '@/api/knowledge'
import type { KnowledgeCategory, KnowledgeArticle, KnowledgeArticleCreateRequest } from '@/types/knowledge'

const props = defineProps<{ projectId?: number }>()

const hasProject = computed(() => !!props.projectId && props.projectId > 0)

// 分类
const categoryTree = ref<KnowledgeCategory[]>([])
const selectedCategoryId = ref<number | null>(null)

const flatCategories = computed(() => {
  const result: KnowledgeCategory[] = []
  const flatten = (items: KnowledgeCategory[]) => {
    items.forEach((item) => {
      result.push(item)
      if (item.children) flatten(item.children)
    })
  }
  flatten(categoryTree.value)
  return result
})

// 文章列表
const loading = ref(false)
const articleList = ref<KnowledgeArticle[]>([])
const total = ref(0)
const searchKeyword = ref('')
const queryParams = reactive({ page: 1, size: 10, status: '', categoryId: undefined as number | undefined })

// 编辑器
const editorVisible = ref(false)
const isEditArticle = ref(false)
const articleSubmitLoading = ref(false)
const articleFormRef = ref<FormInstance>()
const editingArticleId = ref<number | null>(null)
const articleFormData = reactive<KnowledgeArticleCreateRequest>({
  title: '', content: '', summary: '', categoryId: undefined, tags: [], status: 'draft',
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

async function loadCategories() {
  if (!props.projectId) return
  try {
    const data = await getCategoryTree(props.projectId)
    categoryTree.value = Array.isArray(data) ? data : []
  } catch { /* handled */ }
}

async function loadArticles() {
  if (!props.projectId) return
  loading.value = true
  try {
    const data = await getArticleList(props.projectId, {
      page: queryParams.page,
      size: queryParams.size,
      status: queryParams.status,
      categoryId: selectedCategoryId.value ?? undefined,
      keyword: searchKeyword.value || undefined,
    })
    articleList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadArticles() }

function handleCategoryClick(data: KnowledgeCategory) {
  selectedCategoryId.value = data.id
  handleSearch()
}

function handleCreateCategory() {
  if (!props.projectId) return
  ElMessageBox.prompt('请输入分类名称', '新建分类', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async ({ value }) => {
    await createCategory(props.projectId!, { name: value })
    ElMessage.success('分类创建成功')
    loadCategories()
  }).catch(() => {})
}

function handleCreateArticle() {
  isEditArticle.value = false
  editingArticleId.value = null
  Object.assign(articleFormData, { title: '', content: '', summary: '', categoryId: selectedCategoryId.value, tags: [], status: 'draft' })
  editorVisible.value = true
}

function handleEditArticle(article: KnowledgeArticle) {
  isEditArticle.value = true
  editingArticleId.value = article.id
  Object.assign(articleFormData, {
    title: article.title, content: article.content, summary: article.summary,
    categoryId: article.categoryId, tags: article.tags ?? [], status: article.status,
  })
  detailDrawerVisible.value = false
  editorVisible.value = true
}

async function handleSaveDraft() {
  articleFormData.status = 'draft'
  await doSave()
}

async function handlePublish() {
  articleFormData.status = 'published'
  await doSave()
}

async function doSave() {
  const valid = await articleFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!isEditArticle.value && !props.projectId) {
    ElMessage.warning('请先选择一个项目')
    return
  }
  articleSubmitLoading.value = true
  try {
    if (isEditArticle.value && editingArticleId.value) {
      await updateArticle(editingArticleId.value, articleFormData)
      ElMessage.success('更新成功')
    } else {
      await createArticle(props.projectId!, articleFormData)
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

onMounted(() => {
  loadCategories()
  loadArticles()
})
</script>

<style scoped lang="scss">
.category-card { height: calc(100vh - 200px); overflow-y: auto; }
.category-header { display: flex; justify-content: space-between; align-items: center; }
.category-node { display: flex; align-items: center; gap: 4px; .count { color: #909399; font-size: 12px; } }

.toolbar { display: flex; justify-content: space-between; align-items: center; .toolbar-left { display: flex; gap: 8px; } }

.article-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  &:hover { background: #fafafa; }
  &:last-child { border-bottom: none; }

  .article-header { display: flex; align-items: center; gap: 8px; }
  .article-title { margin: 0; font-size: 16px; color: #303133; flex: 1; }
  .article-summary { margin: 8px 0 0; color: #606266; font-size: 14px; line-height: 1.5; }
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

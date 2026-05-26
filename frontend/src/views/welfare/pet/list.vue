<template>
  <div class="pet-wrap">
    <div class="header-row">
      <div class="page-title">宠物管理</div>
      <el-button type="primary" @click="goCreate">
        <el-icon style="margin-right: 6px;"><Plus /></el-icon>
        添加宠物
      </el-button>
    </div>

    <el-card shadow="hover" class="panel-card">
      <!-- 搜索栏 -->
      <div class="search-row">
        <el-input
          v-model="query.keyword"
          class="search-input"
          placeholder="搜索宠物名称..."
          clearable
          @keyup.enter="onSearch"
        />
        <el-select v-model="query.status" class="search-select" placeholder="全部状态" clearable>
          <el-option label="待领养" value="pending" />
          <el-option label="已领养" value="adopted" />
        </el-select>
        <el-select v-model="query.breed" class="search-select" placeholder="全部品种" clearable>
          <el-option v-for="b in breedOptions" :key="b" :label="b" :value="b" />
        </el-select>
        <el-button type="primary" class="search-btn" @click="onSearch">
          <el-icon style="margin-right: 6px;"><Search /></el-icon>
          搜索
        </el-button>
      </div>

      <!-- 统计 + 批量按钮 -->
      <div class="toolbar-row">
        <div class="count-text">共找到 <b>{{ total }}</b> 只宠物</div>
        <div class="toolbar-actions">
          <el-button :disabled="!multipleSelection.length" @click="onBatch">
            批量操作
          </el-button>
          <el-button @click="onExport">导出数据</el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        :data="pagedList"
        style="width: 100%;"
        row-key="id"
        @selection-change="handleSelectionChange"
        :header-cell-style="{ background: '#fff', color: '#111827', fontWeight: '600' }"
      >
        <el-table-column type="selection" width="48" />

        <el-table-column label="宠物信息" min-width="260">
          <template #default="{ row }">
            <div class="pet-info">
              <div class="avatar" :style="{ background: row.avatarBg }" />
              <div class="pet-meta">
                <div class="pet-name">{{ row.name }}</div>
                <div class="pet-sub">{{ row.breed }} · {{ row.genderText }} · {{ row.ageText }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag
              v-if="row.status === 'pending'"
              class="status-tag pending"
              effect="plain"
              round
            >
              待领养
            </el-tag>
            <el-tag
              v-else
              class="status-tag adopted"
              effect="plain"
              round
            >
              已领养
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="热度" width="140">
          <template #default="{ row }">
            <div class="hot-wrap">
              <div class="hot-top">
                <span class="hot-fire">🔥</span>
                <span class="hot-num">{{ row.hot }}</span>
              </div>
              <div class="hot-sub">
                <span>👍 {{ row.likes }}</span>
                <span class="dot">•</span>
                <span>💬 {{ row.comments }}</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="最后更新" width="150">
          <template #default="{ row }">
            <div class="time-wrap">
              <div class="date">{{ row.updatedDate }}</div>
              <div class="time">{{ row.updatedTime }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button type="primary" link @click="goEdit(row.id)">编辑</el-button>
            <el-button type="danger" link @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pager-row">
        <div class="pager-left">显示第 {{ pageStart }}-{{ pageEnd }} 条，共 {{ total }} 条</div>
        <el-pagination
          v-model:current-page="page.pageNum"
          v-model:page-size="page.pageSize"
          :total="total"
          layout="prev, pager, next"
          :pager-count="5"
          @current-change="onPageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, Search } from "@element-plus/icons-vue";

const router = useRouter();

/** 查询条件 */
const query = reactive({
  keyword: "",
  status: "",
  breed: ""
});

const breedOptions = ["橘猫", "三花", "黑猫", "狸花", "奶牛猫", "英短", "美短"];

/** 分页 */
const page = reactive({
  pageNum: 1,
  pageSize: 10
});

/** mock 列表（后面接接口时，把 list 改为接口返回 rows） */
const list = ref([
  {
    id: 1,
    name: "小橘",
    breed: "橘猫",
    genderText: "公",
    ageText: "约2岁",
    status: "pending",
    hot: 9999,
    likes: 888,
    comments: 222,
    updatedDate: "2024-01-20",
    updatedTime: "14:30",
    avatarBg: "linear-gradient(135deg,#f7d794,#546de5)",
  },
  {
    id: 2,
    name: "小花",
    breed: "三花",
    genderText: "母",
    ageText: "约1.5岁",
    status: "pending",
    hot: 8888,
    likes: 777,
    comments: 111,
    updatedDate: "2024-01-19",
    updatedTime: "18:00",
    avatarBg: "linear-gradient(135deg,#f78fb3,#c44569)",
  },
  {
    id: 3,
    name: "小黑",
    breed: "黑猫",
    genderText: "公",
    ageText: "约3岁",
    status: "adopted",
    hot: 7777,
    likes: 666,
    comments: 99,
    updatedDate: "2024-01-18",
    updatedTime: "16:20",
    avatarBg: "linear-gradient(135deg,#2ed573,#1e90ff)",
  },
  // 你可以继续加数据测试分页
]);

/** 选择 */
const multipleSelection = ref([]);
function handleSelectionChange(val) {
  multipleSelection.value = val;
}

/** 过滤后的列表 */
const filteredList = computed(() => {
  const kw = query.keyword.trim();
  return list.value.filter((x) => {
    const hitKw = !kw || x.name.includes(kw) || x.breed.includes(kw);
    const hitStatus = !query.status || x.status === query.status;
    const hitBreed = !query.breed || x.breed === query.breed;
    return hitKw && hitStatus && hitBreed;
  });
});

const total = computed(() => filteredList.value.length);

/** 当前页数据 */
const pagedList = computed(() => {
  const start = (page.pageNum - 1) * page.pageSize;
  return filteredList.value.slice(start, start + page.pageSize);
});

const pageStart = computed(() => (total.value === 0 ? 0 : (page.pageNum - 1) * page.pageSize + 1));
const pageEnd = computed(() => Math.min(page.pageNum * page.pageSize, total.value));

function onSearch() {
  page.pageNum = 1;
}

function onPageChange() {
  // 这里留空即可（computed 会自动更新）
}

function onBatch() {
  ElMessage.info(`已选择 ${multipleSelection.value.length} 条，后续接入批量功能`);
}

function onExport() {
  ElMessage.success("导出数据（mock），后续接入真实导出接口");
}

function goCreate() {
  router.push("/welfare/pet/create");
}

function goEdit(id) {
  router.push(`/welfare/pet/edit/${id}`);
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」吗？此操作不可撤销。`, "删除确认", {
      type: "warning",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
    });
    list.value = list.value.filter((x) => x.id !== row.id);
    ElMessage.success("已删除");
    if (pagedList.value.length === 0 && page.pageNum > 1) page.pageNum -= 1;
  } catch {
    // cancel
  }
}
</script>

<style scoped>
.pet-wrap {
  padding: 16px 18px 24px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}

.panel-card {
  border-radius: 12px;
  border: none;
}

.search-row {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 10px 6px 14px;
}

.search-input :deep(.el-input__wrapper) {
  background: #2f343a;
  border-radius: 8px;
  box-shadow: none;
}
.search-input :deep(.el-input__inner) {
  color: #fff;
}
.search-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.65);
}

.search-input {
  flex: 1;
}

.search-select {
  width: 150px;
}

.search-btn {
  width: 110px;
}

.toolbar-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 6px 12px;
}

.count-text {
  color: #374151;
  font-size: 13px;
}
.count-text b {
  font-size: 16px;
  color: #111827;
}

.toolbar-actions {
  display: flex;
  gap: 10px;
}

.pet-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 10px;
}

.pet-name {
  font-weight: 700;
  color: #111827;
}

.pet-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.status-tag.pending {
  color: #ff8a00;
  border-color: #ffe6c7;
  background: #fff7ed;
}
.status-tag.adopted {
  color: #16a34a;
  border-color: #dcfce7;
  background: #f0fdf4;
}

.hot-wrap {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.hot-top {
  display: flex;
  align-items: center;
  gap: 6px;
}
.hot-num {
  font-weight: 700;
  color: #111827;
}
.hot-sub {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 6px;
}
.dot {
  opacity: 0.5;
}

.time-wrap .date {
  color: #111827;
  font-size: 13px;
}
.time-wrap .time {
  margin-top: 4px;
  color: #6b7280;
  font-size: 12px;
}

.pager-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 6px 6px;
}

.pager-left {
  font-size: 12px;
  color: #6b7280;
}

/* 响应式 */
@media (max-width: 992px) {
  .search-row {
    flex-wrap: wrap;
  }
  .search-select,
  .search-btn {
    width: 100%;
  }
  .search-input {
    min-width: 100%;
  }
}
</style>

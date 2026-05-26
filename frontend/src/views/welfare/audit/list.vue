<template>
  <div class="audit-wrap">
    <div class="page-title">内容审核</div>

    <el-card shadow="hover" class="panel">
      <div class="tabs-row">
        <div class="tab" :class="{ active: tab === 'all' }" @click="setTab('all')">
          全部 ({{ total }})
        </div>
        <div class="tab" :class="{ active: tab === 'photo' }" @click="setTab('photo')">
          照片 ({{ total }})
        </div>
        <div class="tab disabled">
          博客 (0)
        </div>
      </div>

      <div class="divider"></div>

      <div class="search-row">
        <el-input
          v-model="query.keyword"
          class="search-input"
          placeholder="搜索上传人、宠物、地点或描述..."
          clearable
          @keyup.enter="getList"
        />
        <el-select v-model="query.status" class="search-select" placeholder="全部状态" clearable @change="getList">
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>

        <el-button type="primary" class="search-btn" @click="getList">
          <el-icon class="btn-icon"><Search /></el-icon>
          搜索
        </el-button>
      </div>
    </el-card>

    <el-card shadow="hover" class="panel list-panel">
      <div v-loading="loading">
        <div v-if="rows.length === 0" class="empty">暂无数据</div>

        <div
          v-for="item in rows"
          :key="item.id"
          class="audit-item"
          :class="item.status"
        >
          <div class="audit-left">
            <el-image
              v-if="item.cover"
              class="thumb"
              :src="item.cover"
              :preview-src-list="item.imageList"
              preview-teleported
              fit="cover"
            />
            <div v-else class="thumb empty-thumb">无图</div>
          </div>

          <div class="audit-mid">
            <div class="title-line">
              <span class="title">{{ item.uploaderName || `用户 ${item.userId}` }} 上传的照片</span>
            </div>

            <div class="meta">关联宠物：{{ item.petName || '-' }} · {{ item.location || '-' }}</div>
            <div class="meta">上传时间：{{ item.createdAt || item.shootTime || '-' }}</div>
            <div v-if="item.description" class="meta desc">描述：{{ item.description }}</div>

            <div class="tags-row">
              <span class="status-tag" :class="item.status">{{ statusText(item.status) }}</span>
              <span class="type-tag">照片</span>
              <span v-if="item.isDailyWinner === 1" class="featured-tag">精选</span>
            </div>
          </div>

          <div class="audit-right">
            <template v-if="item.status === 'pending'">
              <el-button type="success" @click="approve(item)">通过</el-button>
              <el-button type="danger" @click="reject(item)">拒绝</el-button>
            </template>
          </div>
        </div>

        <div class="pager-row">
          <div class="pager-left">显示第 {{ pageStart }}-{{ pageEnd }} 条，共 {{ total }} 条</div>
          <el-pagination
            v-model:current-page="query.pageNum"
            v-model:page-size="query.pageSize"
            :total="total"
            layout="prev, pager, next"
            :pager-count="5"
            @current-change="getList"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import { listAuditPhotos, reviewAuditPhoto } from "@/api/welfare/audit";

const tab = ref("photo");
const loading = ref(false);
const rows = ref([]);
const total = ref(0);

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  status: "",
});

const pageStart = computed(() => (total.value === 0 ? 0 : (query.pageNum - 1) * query.pageSize + 1));
const pageEnd = computed(() => Math.min(query.pageNum * query.pageSize, total.value));

function setTab(v) {
  if (v === "blog") return;
  tab.value = v;
  query.pageNum = 1;
  getList();
}

function statusText(status) {
  if (status === "pending") return "待审核";
  if (status === "approved") return "已通过";
  if (status === "rejected") return "已拒绝";
  return status || "-";
}

function parseImages(images) {
  if (!images) return [];
  if (Array.isArray(images)) return images;
  try {
    const parsed = JSON.parse(images);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

async function getList() {
  loading.value = true;
  try {
    const res = await listAuditPhotos(query);
    rows.value = (res.rows || []).map((item) => {
      const imageList = parseImages(item.images);
      return {
        ...item,
        imageList,
        cover: imageList[0] || "",
      };
    });
    total.value = res.total || 0;
  } finally {
    loading.value = false;
  }
}

async function approve(item) {
  await reviewAuditPhoto(item.id, { status: "approved" });
  ElMessage.success("已通过");
  getList();
}

async function reject(item) {
  try {
    const { value } = await ElMessageBox.prompt("请输入拒绝原因", "拒绝确认", {
      confirmButtonText: "拒绝",
      cancelButtonText: "取消",
      inputPlaceholder: "可选",
      type: "warning",
    });
    await reviewAuditPhoto(item.id, { status: "rejected", rejectReason: value || "" });
    ElMessage.success("已拒绝");
    getList();
  } catch {}
}

onMounted(getList);
</script>

<style scoped>
.audit-wrap {
  padding: 16px 18px 24px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 12px;
}

.panel {
  border-radius: 8px;
}

.tabs-row {
  display: flex;
  gap: 10px;
}

.tab {
  padding: 8px 16px;
  border-radius: 8px;
  background: #f3f4f6;
  color: #374151;
  font-weight: 600;
  cursor: pointer;
}

.tab.active {
  background: #2563eb;
  color: #fff;
}

.tab.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.divider {
  height: 1px;
  background: #e5e7eb;
  margin: 14px 0;
}

.search-row {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  flex: 1;
}

.search-select {
  width: 180px;
}

.search-btn {
  min-width: 110px;
}

.btn-icon {
  margin-right: 6px;
}

.list-panel {
  margin-top: 14px;
}

.empty {
  padding: 42px 0;
  color: #909399;
  text-align: center;
}

.audit-item {
  display: flex;
  align-items: center;
  gap: 22px;
  padding: 16px 18px;
  margin-bottom: 14px;
  border-left: 4px solid #ff8a00;
  border-radius: 8px;
  background: #fff7ed;
}

.audit-item.approved {
  border-left-color: #16a34a;
  background: #f0fdf4;
}

.audit-item.rejected {
  border-left-color: #ef4444;
  background: #fff1f2;
}

.audit-left {
  width: 76px;
  flex: 0 0 76px;
}

.thumb {
  width: 76px;
  height: 76px;
  border-radius: 8px;
  display: block;
  overflow: hidden;
  background: #e5e7eb;
}

.empty-thumb {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-size: 13px;
}

.audit-mid {
  min-width: 0;
  flex: 1;
}

.title {
  color: #111827;
  font-weight: 700;
}

.meta {
  margin-top: 6px;
  color: #4b5563;
  font-size: 13px;
}

.desc {
  max-width: 760px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tags-row {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 10px;
}

.status-tag,
.type-tag,
.featured-tag {
  padding: 3px 8px;
  border-radius: 999px;
  font-size: 12px;
  line-height: 18px;
}

.status-tag.pending {
  color: #b45309;
  background: #fef3c7;
}

.status-tag.approved {
  color: #15803d;
  background: #dcfce7;
}

.status-tag.rejected {
  color: #b91c1c;
  background: #fee2e2;
}

.type-tag {
  color: #2563eb;
  background: #dbeafe;
}

.featured-tag {
  color: #9333ea;
  background: #f3e8ff;
}

.audit-right {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  width: 190px;
}

.pager-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 14px;
}

.pager-left {
  color: #6b7280;
  font-size: 13px;
}
</style>

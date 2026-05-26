<template>
  <div class="adopt-wrap">
    <div class="page-title">领养申请管理</div>

    <el-card shadow="hover" class="panel">
      <!-- tabs -->
      <div class="tabs-row">
        <div class="tab" :class="{ active: tab === 'pending' }" @click="setTab('pending')">
          待审核 ({{ countPending }})
        </div>
        <div class="tab" :class="{ active: tab === 'approved' }" @click="setTab('approved')">
          已通过 ({{ countApproved }})
        </div>
        <div class="tab" :class="{ active: tab === 'rejected' }" @click="setTab('rejected')">
          已拒绝 ({{ countRejected }})
        </div>
        <div class="tab" :class="{ active: tab === 'done' }" @click="setTab('done')">
          已完成 ({{ countDone }})
        </div>
      </div>

      <div class="divider"></div>

      <!-- search -->
      <div class="search-row">
        <el-input
          v-model="query.keyword"
          class="search-input"
          placeholder="搜索申请人或宠物名称..."
          clearable
          @keyup.enter="onSearch"
        />
        <el-select v-model="query.status" class="search-select" placeholder="全部状态" clearable>
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
          <el-option label="已完成" value="done" />
        </el-select>
        <el-button type="primary" class="search-btn" @click="onSearch">
          <el-icon style="margin-right: 6px;"><Search /></el-icon>
          搜索
        </el-button>
      </div>
    </el-card>

    <!-- list -->
    <el-card shadow="hover" class="panel" style="margin-top: 14px;">
      <div v-if="pagedList.length === 0" class="empty">暂无数据</div>

      <div
        v-for="item in pagedList"
        :key="item.id"
        class="apply-item"
        :style="{ '--bar': item.barColor, '--bg': item.bgColor }"
      >
        <div class="left">
          <div class="thumb" :style="{ background: item.thumbBg }"></div>
        </div>

        <div class="mid">
          <div class="line1">申请人：<b>{{ item.applicant }}</b></div>

          <div class="line2">
            申请领养：
            <span class="link" @click="goPet(item.petId)">{{ item.petName }}</span>
            · {{ item.location }}
          </div>

          <div class="line2">申请时间：{{ item.time }}</div>

          <div class="tags-row">
            <span class="status-tag" :class="item.status">{{ statusText(item.status) }}</span>
            <span class="phone-tag">📱 {{ item.phoneMasked }}</span>
          </div>
        </div>

        <div class="right">
          <template v-if="item.status === 'pending'">
            <el-button type="success" @click="approve(item)">✓ 通过</el-button>
            <el-button type="danger" @click="reject(item)">✕ 拒绝</el-button>
            <el-button type="primary" @click="contact(item)">💬 联系</el-button>
          </template>
          <el-button @click="goDetail(item.id)">查看详情</el-button>
        </div>
      </div>

      <!-- pager -->
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
import { Search } from "@element-plus/icons-vue";

const router = useRouter();

const tab = ref("pending"); // pending/approved/rejected/done
const query = reactive({
  keyword: "",
  status: "",
});

const page = reactive({
  pageNum: 1,
  pageSize: 10,
});

/** mock 数据：按你截图的两条（待审核橙色、已通过绿色） */
const list = ref([
  {
    id: 1,
    applicant: "张三",
    petId: 1,
    petName: "小橘",
    location: "北京大学",
    time: "2024-01-20 10:30",
    status: "pending",
    phoneMasked: "138****8888",
    barColor: "#ff8a00",
    bgColor: "#fff7ed",
    thumbBg: "linear-gradient(135deg,#f7d794,#546de5)",
  },
  {
    id: 2,
    applicant: "李四",
    petId: 2,
    petName: "小花",
    location: "清华大学",
    time: "2024-01-19 15:20",
    status: "approved",
    phoneMasked: "139****9999",
    barColor: "#16a34a",
    bgColor: "#f0fdf4",
    thumbBg: "linear-gradient(135deg,#f78fb3,#c44569)",
  },
]);

/** tab 统计 */
const countPending = computed(() => list.value.filter((x) => x.status === "pending").length);
const countApproved = computed(() => list.value.filter((x) => x.status === "approved").length);
const countRejected = computed(() => list.value.filter((x) => x.status === "rejected").length);
const countDone = computed(() => list.value.filter((x) => x.status === "done").length);

function setTab(v) {
  tab.value = v;
  page.pageNum = 1;
}

function statusText(s) {
  return s === "pending"
    ? "待审核"
    : s === "approved"
    ? "已通过"
    : s === "rejected"
    ? "已拒绝"
    : "已完成";
}

/** 过滤 */
const filteredList = computed(() => {
  const kw = query.keyword.trim();
  return list.value.filter((x) => {
    const hitTab = x.status === tab.value;
    const hitStatus = !query.status || x.status === query.status;
    const hitKw =
      !kw ||
      x.applicant.includes(kw) ||
      x.petName.includes(kw) ||
      x.location.includes(kw);
    return hitTab && hitStatus && hitKw;
  });
});

const total = computed(() => filteredList.value.length);

const pagedList = computed(() => {
  const start = (page.pageNum - 1) * page.pageSize;
  return filteredList.value.slice(start, start + page.pageSize);
});

const pageStart = computed(() => (total.value === 0 ? 0 : (page.pageNum - 1) * page.pageSize + 1));
const pageEnd = computed(() => Math.min(page.pageNum * page.pageSize, total.value));

function onSearch() {
  page.pageNum = 1;
}
function onPageChange() {}

function goPet(petId) {
  router.push(`/welfare/pet/edit/${petId}`);
}

function goDetail(id) {
  router.push(`/welfare/adoption/detail/${id}`);
}

function approve(item) {
  item.status = "approved";
  item.barColor = "#16a34a";
  item.bgColor = "#f0fdf4";
  ElMessage.success("已通过（mock）");
}

async function reject(item) {
  try {
    await ElMessageBox.confirm("确定拒绝该申请吗？", "拒绝确认", {
      type: "warning",
      confirmButtonText: "拒绝",
      cancelButtonText: "取消",
    });
    item.status = "rejected";
    item.barColor = "#ef4444";
    item.bgColor = "#fff1f2";
    ElMessage.success("已拒绝（mock）");
  } catch {}
}

function contact(item) {
  // 后续可以弹出联系弹窗（电话/微信/站内信），这里先提示
  ElMessage.info(`联系申请人：${item.applicant}（${item.phoneMasked}）`);
}
</script>

<style scoped>
.adopt-wrap {
  padding: 16px 18px 24px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

.page-title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 12px;
}

.panel {
  border-radius: 12px;
  border: none;
}

/* tabs */
.tabs-row {
  display: flex;
  gap: 10px;
  padding: 10px 8px 6px;
}

.tab {
  height: 36px;
  padding: 0 14px;
  border-radius: 10px;
  background: #f3f4f6;
  color: #374151;
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  font-weight: 700;
  user-select: none;
}

.tab.active {
  background: #2f7bff;
  color: #fff;
}

.divider {
  height: 1px;
  background: #e5e7eb;
  margin: 8px 8px 10px;
}

/* search */
.search-row {
  display: flex;
  gap: 12px;
  align-items: center;
  padding: 0 8px 12px;
}

.search-input {
  flex: 1;
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

.search-select {
  width: 160px;
}

.search-btn {
  width: 110px;
}

/* item */
.apply-item {
  position: relative;
  border-radius: 12px;
  background: var(--bg);
  padding: 16px 16px 16px 18px;
  display: grid;
  grid-template-columns: 92px 1fr auto;
  gap: 14px;
  margin: 14px 8px;
  overflow: hidden;
}

.apply-item::before {
  content: "";
  position: absolute;
  left: 0;
  top: 12px;
  bottom: 12px;
  width: 4px;
  border-radius: 4px;
  background: var(--bar);
}

.thumb {
  width: 74px;
  height: 74px;
  border-radius: 12px;
}

.line1 {
  font-size: 14px;
  color: #111827;
  font-weight: 700;
}

.line2 {
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
}

.tags-row {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  align-items: center;
}

.status-tag {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.65);
  font-weight: 800;
}
.status-tag.pending {
  color: #ff8a00;
}
.status-tag.approved {
  color: #16a34a;
}
.status-tag.rejected {
  color: #ef4444;
}
.status-tag.done {
  color: #2563eb;
}

.phone-tag {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.65);
  color: #111827;
  font-weight: 700;
}

.link {
  color: #2f7bff;
  cursor: pointer;
  font-weight: 800;
}
.link:hover {
  text-decoration: underline;
}

.right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* pager */
.pager-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 8px 8px;
}

.pager-left {
  font-size: 12px;
  color: #6b7280;
}

.empty {
  padding: 30px 10px;
  text-align: center;
  color: #9ca3af;
}

/* responsive */
@media (max-width: 992px) {
  .search-row {
    flex-wrap: wrap;
  }
  .search-select,
  .search-btn {
    width: 100%;
  }
  .apply-item {
    grid-template-columns: 92px 1fr;
  }
  .right {
    grid-column: 1 / -1;
    justify-content: flex-end;
    margin-top: 10px;
    flex-wrap: wrap;
  }
}
</style>


<template>
  <div class="detail-wrap">
    <!-- 顶部条 -->
    <div class="topbar">
      <el-button class="back-btn" @click="goBack">← 返回</el-button>
      <div class="topbar-title">审核内容详情</div>
      <div class="topbar-actions">
        <el-button type="success" @click="onApprove">✓ 通过</el-button>
        <el-button type="danger" @click="onReject">✕ 拒绝</el-button>
        <el-button type="primary" @click="toggleFeatured">
          ⭐ {{ detail.featured ? "已标记精选" : "标记精选" }}
        </el-button>
      </div>
    </div>

    <!-- 主内容卡片 -->
    <el-card shadow="hover" class="panel">
      <div class="head">
        <div class="big-thumb" :style="{ background: detail.thumbBg }"></div>

        <div class="head-right">
          <div class="content-title">{{ detail.title }}</div>

          <div class="user-row">
            <div class="user-avatar"></div>
            <div class="user-meta">
              <div class="user-name">{{ detail.userName }}</div>
              <div class="user-id">ID: {{ detail.userId }}</div>
            </div>
          </div>

          <div class="tag-row">
            <span class="status-tag" :class="detail.status">{{ statusText(detail.status) }}</span>
            <span class="type-tag">
              <span class="type-icon">{{ detail.type === "photo" ? "🖼️" : "📝" }}</span>
              {{ detail.type === "photo" ? "照片" : "博客" }}
            </span>
          </div>

          <div class="meta-line">
            关联宠物：
            <span class="link" @click="goPet(detail.petId)">{{ detail.petName }}</span>
            · {{ detail.location }}
          </div>

          <div class="meta-line">上传时间：{{ detail.uploadTime }}</div>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 描述 -->
      <div class="section-title">照片描述</div>
      <div class="desc-box">{{ detail.description }}</div>

      <!-- 拍摄信息 -->
      <div class="section-title" style="margin-top: 16px;">拍摄信息</div>
      <div class="info-grid">
        <div class="info-card">
          <div class="info-label">拍摄时间</div>
          <div class="info-value">{{ detail.shotTime }}</div>
        </div>
        <div class="info-card">
          <div class="info-label">拍摄地点</div>
          <div class="info-value">
            <span class="pin">📍</span>
            {{ detail.shotLocation }}
          </div>
        </div>
      </div>

      <!-- 审核意见 -->
      <div class="review-box">
        <div class="review-title">审核意见</div>
        <el-input
          v-model="reviewNote"
          type="textarea"
          :rows="4"
          placeholder="请输入审核意见（拒绝时必填）..."
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();
const route = useRoute();

const id = Number(route.params.id);

/**
 * mock：后面接后端时这里换成 getAuditDetail(id)
 * 根据你的截图，这里先按 photo 类型写
 */
const detail = reactive({
  id,
  type: "photo",
  title: "用户A 上传的照片",
  userName: "用户A",
  userId: "user123",
  status: "pending", // pending|approved|rejected
  featured: false,

  petId: 1,
  petName: "小橘",
  location: "北京大学",

  uploadTime: "2024-01-20 14:30",

  description: "今天在图书馆门口看到小橘，它正在晒太阳，看起来很惬意~",

  shotTime: "2024-01-20 14:30",
  shotLocation: "北京大学图书馆",

  thumbBg: "linear-gradient(135deg,#f7d794,#546de5)",
});

const reviewNote = ref("");

function statusText(s) {
  return s === "pending" ? "待审核" : s === "approved" ? "已通过" : "已拒绝";
}

function goBack() {
  router.back();
}

function goPet(petId) {
  // 这里先跳到宠物编辑页（你也可以改成宠物详情页）
  router.push(`/welfare/pet/edit/${petId}`);
}

async function onApprove() {
  if (detail.status === "approved") {
    ElMessage.info("该内容已通过");
    return;
  }
  detail.status = "approved";
  ElMessage.success("已通过（mock）");
}

async function onReject() {
  if (detail.status === "rejected") {
    ElMessage.info("该内容已拒绝");
    return;
  }
  if (!reviewNote.value.trim()) {
    ElMessage.warning("拒绝时必须填写审核意见");
    return;
  }

  try {
    await ElMessageBox.confirm("确定拒绝该内容吗？", "拒绝确认", {
      type: "warning",
      confirmButtonText: "拒绝",
      cancelButtonText: "取消",
    });
    detail.status = "rejected";
    ElMessage.success("已拒绝（mock）");
  } catch {
    // cancel
  }
}

function toggleFeatured() {
  detail.featured = !detail.featured;
  ElMessage.success(detail.featured ? "已标记精选" : "已取消精选");
}
</script>

<style scoped>
.detail-wrap {
  padding: 14px 18px 24px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

/* 顶部条 */
.topbar {
  display: grid;
  grid-template-columns: 90px 1fr auto;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.back-btn {
  height: 36px;
  border-radius: 8px;
}

.topbar-title {
  font-size: 18px;
  font-weight: 800;
  color: #111827;
}

.topbar-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 主卡片 */
.panel {
  border-radius: 12px;
  border: none;
}

.head {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 18px;
  padding: 4px 2px 0;
}

.big-thumb {
  width: 180px;
  height: 180px;
  border-radius: 14px;
}

.head-right {
  padding-top: 6px;
}

.content-title {
  font-size: 18px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 12px;
}

.user-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.user-avatar {
  width: 42px;
  height: 42px;
  border-radius: 999px;
  background: #22c55e;
}

.user-name {
  font-weight: 800;
  color: #111827;
}

.user-id {
  margin-top: 4px;
  font-size: 12px;
  color: #9ca3af;
}

.tag-row {
  display: flex;
  gap: 10px;
  margin: 10px 0 10px;
}

.status-tag {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  background: #fff;
  font-weight: 700;
}

.status-tag.pending {
  color: #ff8a00;
  background: #fff7ed;
  border: 1px solid #ffe6c7;
}

.status-tag.approved {
  color: #16a34a;
  background: #f0fdf4;
  border: 1px solid #dcfce7;
}

.status-tag.rejected {
  color: #ef4444;
  background: #fff1f2;
  border: 1px solid #fecdd3;
}

.type-tag {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #111827;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 700;
}

.type-icon {
  font-size: 13px;
}

.meta-line {
  margin-top: 8px;
  font-size: 13px;
  color: #6b7280;
}

.link {
  color: #2f7bff;
  cursor: pointer;
  font-weight: 700;
}
.link:hover {
  text-decoration: underline;
}

.divider {
  height: 1px;
  background: #e5e7eb;
  margin: 18px 2px 14px;
}

.section-title {
  font-size: 14px;
  font-weight: 800;
  color: #111827;
  margin: 0 2px 10px;
}

.desc-box {
  background: #f3f4f6;
  border-radius: 10px;
  padding: 12px 12px;
  color: #374151;
  font-size: 13px;
  margin: 0 2px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin: 0 2px;
}

.info-card {
  background: #f9fafb;
  border-radius: 12px;
  padding: 14px;
  border: 1px solid #eef2f7;
}

.info-label {
  font-size: 12px;
  color: #9ca3af;
}

.info-value {
  margin-top: 10px;
  font-weight: 800;
  color: #111827;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.pin {
  opacity: 0.9;
}

.review-box {
  margin: 16px 2px 2px;
  background: #fff7ed;
  border-radius: 12px;
  padding: 14px;
  border-left: 4px solid #ff8a00;
}

.review-title {
  font-weight: 900;
  color: #111827;
  margin-bottom: 10px;
}

/* responsive */
@media (max-width: 992px) {
  .head {
    grid-template-columns: 1fr;
  }
  .big-thumb {
    width: 100%;
    height: 220px;
  }
  .topbar {
    grid-template-columns: 90px 1fr;
  }
  .topbar-actions {
    grid-column: 1 / -1;
    justify-content: flex-end;
    flex-wrap: wrap;
  }
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>

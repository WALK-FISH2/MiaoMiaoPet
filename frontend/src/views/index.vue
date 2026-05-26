<template>
  <div class="dashboard-wrap">
    <!-- 标题（若依本身有面包屑/标题，这里保留简单标题即可） -->
    <div class="page-title">工作台</div>

    <!-- 待处理事项 -->
    <div class="section">
      <div class="section-title">待处理事项</div>
      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :md="6" v-for="item in todoCards" :key="item.key">
          <el-card shadow="hover" class="todo-card" :style="{ '--bar-color': item.color }">
            <div class="todo-inner">
              <div class="todo-left">
                <div class="todo-name">{{ item.title }}</div>
                <div class="todo-num" :style="{ color: item.color }">{{ item.value }}</div>
                <div class="todo-sub">{{ item.sub }}</div>
              </div>
              <div class="todo-right">
                <div class="todo-icon" :style="{ backgroundColor: item.iconBg }">
                  <component :is="item.icon" class="todo-icon-svg" :style="{ color: item.color }" />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据概览 -->
    <div class="section">
      <div class="section-title">数据概览</div>
      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :md="6" v-for="item in overviewCards" :key="item.key">
          <el-card shadow="hover" class="overview-card">
            <div class="overview-name">{{ item.title }}</div>
            <div class="overview-num">{{ item.value }}</div>
            <div class="overview-sub" :class="item.subClass">
              <span v-if="item.trend === 'up'">↑</span>
              <span v-else-if="item.trend === 'down'">↓</span>
              {{ item.sub }}
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快捷操作 -->
    <div class="section">
      <div class="section-title">快捷操作</div>
      <el-card shadow="hover" class="quick-card">
        <div class="quick-grid">
          <div class="quick-item" v-for="q in quickActions" :key="q.key" @click="onQuick(q.key)">
            <div class="quick-icon">
              <component :is="q.icon" class="quick-icon-svg" />
            </div>
            <div class="quick-text">{{ q.title }}</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import {
  Document,
  WarningFilled,
  ChatDotRound,
  Files,
  Plus,
  EditPen,
  DataAnalysis,
  Setting,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();

/**
 * 待处理事项（上面一排 4 卡）
 * 颜色/图标/图标底色尽量贴近你截图风格
 */
const todoCards = [
  {
    key: "audit",
    title: "待审核内容",
    value: 12,
    sub: "条待处理",
    color: "#ff5b5b",
    iconBg: "#ffecec",
    icon: Document,
  },
  {
    key: "apply",
    title: "待处理申请",
    value: 8,
    sub: "条待处理",
    color: "#2f7bff",
    iconBg: "#eaf1ff",
    icon: Files,
  },
  {
    key: "report",
    title: "内容举报",
    value: 3,
    sub: "条待处理",
    color: "#2bb673",
    iconBg: "#eaf8f1",
    icon: WarningFilled,
  },
  {
    key: "msg",
    title: "系统消息",
    value: 5,
    sub: "条未读",
    color: "#ff8a00",
    iconBg: "#fff3e6",
    icon: ChatDotRound,
  },
];

/**
 * 数据概览（中间一排 4 卡）
 */
const overviewCards = [
  { key: "petTotal", title: "宠物总数", value: 25, sub: "较上月 +2", trend: "up", subClass: "ok" },
  { key: "adopting", title: "待领养", value: 5, sub: "需关注", trend: "", subClass: "warn" },
  { key: "todayUpload", title: "今日上传", value: 8, sub: "待审核", trend: "", subClass: "info" },
  { key: "likeTotal", title: "总点赞数", value: "8,888", sub: "较昨日 +156", trend: "up", subClass: "ok" },
];

/**
 * 快捷操作（下面一排 4 块）
 */
const quickActions = [
  { key: "addPet", title: "添加宠物", icon: Plus },
  { key: "notice", title: "发布公告", icon: EditPen },
  { key: "stats", title: "查看统计", icon: DataAnalysis },
  { key: "setting", title: "系统设置", icon: Setting },
];

function onQuick(key) {
  // 这里先给个默认跳转，你后面做完页面后再把路径换成你的实际路由
  const map = {
    addPet: "/welfare/pet/create",
    notice: "/welfare/system", // 你也可以改成公告页
    stats: "/welfare/statistics",
    setting: "/welfare/system",
  };

  const target = map[key];
  if (target) router.push(target);
  else ElMessage.info("功能开发中");
}
</script>

<style scoped>
/* 整体背景与间距，贴近截图：浅灰底 + 卡片白底 */
.dashboard-wrap {
  padding: 16px 18px 24px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 12px;
}

.section {
  margin-top: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  margin: 10px 0 12px;
}

/* 待处理事项卡片 */
.todo-card {
  border-radius: 12px;
  border: none;
  overflow: hidden;
  position: relative;
  min-height: 122px;
}

.todo-card::before {
  content: "";
  position: absolute;
  left: 0;
  top: 12px;
  bottom: 12px;
  width: 4px;
  background: var(--bar-color);
  border-radius: 4px;
}

.todo-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-left: 10px;
}

.todo-left {
  padding: 10px 6px 10px 6px;
}

.todo-name {
  font-size: 13px;
  color: #374151;
  margin-bottom: 8px;
}

.todo-num {
  font-size: 30px;
  font-weight: 700;
  line-height: 1;
}

.todo-sub {
  margin-top: 8px;
  font-size: 12px;
  color: #9ca3af;
}

.todo-right {
  padding-right: 8px;
}

.todo-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.todo-icon-svg {
  width: 20px;
  height: 20px;
}

/* 数据概览卡片 */
.overview-card {
  border-radius: 12px;
  border: none;
  min-height: 112px;
}

.overview-name {
  font-size: 13px;
  color: #374151;
  margin-bottom: 10px;
}

.overview-num {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  line-height: 1;
}

.overview-sub {
  margin-top: 10px;
  font-size: 12px;
  display: inline-flex;
  gap: 4px;
}

.overview-sub.ok {
  color: #16a34a;
}
.overview-sub.warn {
  color: #ef4444;
}
.overview-sub.info {
  color: #2563eb;
}

/* 快捷操作 */
.quick-card {
  border-radius: 12px;
  border: none;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  padding: 10px 6px;
}

.quick-item {
  cursor: pointer;
  user-select: none;
  border-radius: 12px;
  background: #f6f7f9;
  height: 86px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: transform 0.12s ease, box-shadow 0.12s ease;
}

.quick-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 18px rgba(17, 24, 39, 0.08);
  background: #f4f6ff;
}

.quick-icon {
  width: 38px;
  height: 38px;
  border-radius: 12px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-icon-svg {
  width: 20px;
  height: 20px;
  color: #6366f1;
}

.quick-text {
  margin-top: 8px;
  font-size: 13px;
  color: #111827;
}

/* 响应式：小屏变两列/一列 */
@media (max-width: 992px) {
  .quick-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 520px) {
  .quick-grid {
    grid-template-columns: 1fr;
  }
}
</style>

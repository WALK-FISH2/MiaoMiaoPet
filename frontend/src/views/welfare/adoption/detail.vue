<template>
  <div class="detail-wrap">
    <!-- 顶部条 -->
    <div class="topbar">
      <el-button class="back-btn" @click="goBack">← 返回</el-button>
      <div class="topbar-title">领养申请详情</div>
      <div class="topbar-actions">
        <el-button type="success" @click="onApprove">✓ 通过申请</el-button>
        <el-button type="danger" @click="onReject">✕ 拒绝申请</el-button>
        <el-button type="primary" @click="onContact">💬 联系申请人</el-button>
      </div>
    </div>

    <el-card shadow="hover" class="panel">
      <!-- 宠物信息头部 -->
      <div class="pet-head">
        <div class="pet-thumb" :style="{ background: data.pet.thumbBg }"></div>

        <div class="pet-info">
          <div class="pet-name">{{ data.pet.name }}</div>

          <div class="pet-tags">
            <span class="pill pill-orange">🐱 {{ data.pet.breed }}</span>
            <span class="pill pill-blue">{{ data.pet.gender === "male" ? "♂ 公" : "♀ 母" }}</span>
            <span class="pill pill-green">{{ data.pet.ageText }}</span>
          </div>

          <div class="pet-loc">📍 {{ data.pet.location }}</div>
          <div class="pet-status">✅ {{ data.pet.statusText }}</div>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 申请人信息 -->
      <div class="section">
        <div class="section-title">申请人信息</div>
        <div class="grid2">
          <InfoCard label="姓名" :value="data.applicant.name" />
          <InfoCard label="手机号" :value="data.applicant.phoneMasked" />
          <InfoCard label="微信号" :value="data.applicant.wechat" />
          <InfoCard label="所在城市" :value="data.applicant.city" />
          <InfoCard label="详细地址" :value="data.applicant.address" />
          <InfoCard label="年龄" :value="data.applicant.age + '岁'" />
          <InfoCard label="职业" :value="data.applicant.job" />
          <InfoCard label="月收入" :value="data.applicant.income" />
          <InfoCard label="申请时间" :value="data.applyTime" />
        </div>
      </div>

      <!-- 申请理由 -->
      <div class="section">
        <div class="section-title">申请理由</div>
        <div class="text-box">{{ data.reason }}</div>
      </div>

      <!-- 居住环境 -->
      <div class="section">
        <div class="section-title">居住环境</div>
        <div class="grid2">
          <InfoCard label="是否自有住房" :value="yesNo(data.env.ownHouse)" :ok="data.env.ownHouse" />
          <InfoCard label="住房类型" :value="data.env.houseType" />
          <InfoCard label="住房面积" :value="data.env.area" />
          <InfoCard label="是否允许养宠" :value="yesNo(data.env.allowPets)" :ok="data.env.allowPets" />
          <InfoCard label="是否封窗" :value="data.env.windowSafeText" :ok="data.env.windowSafe" />
          <InfoCard label="家庭成员" :value="data.env.familyMembers" />
          <InfoCard label="是否养过宠物" :value="yesNo(data.env.raisedBefore)" :ok="data.env.raisedBefore" />
          <InfoCard label="现有宠物" :value="data.env.currentPets" />
          <InfoCard label="养宠经验" :value="data.env.expYears" />
          <InfoCard label="能否接受定期回访" :value="yesNo(data.env.acceptVisit)" :ok="data.env.acceptVisit" />
        </div>
      </div>

      <!-- 居住环境照片 -->
      <div class="section">
        <div class="section-title">居住环境照片</div>
        <div class="photo-grid">
          <div v-for="p in data.envPhotos" :key="p.id" class="env-photo" :style="{ background: p.bg }"></div>
        </div>
      </div>

      <!-- 其他说明 -->
      <div class="section">
        <div class="section-title">其他说明</div>
        <div class="text-box">{{ data.extra }}</div>
      </div>

      <!-- 审核记录 -->
      <div class="section">
        <div class="section-title">审核记录</div>

        <div class="record-box">
          <div class="record-line">
            当前状态：
            <span class="record-status" :class="data.status">{{ statusText(data.status) }}</span>
          </div>
          <div class="record-detail">
            <div>• 申请提交时间：{{ data.applyTime }}</div>
            <div v-if="data.status === 'pending'">• 等待审核中...</div>
            <div v-else-if="data.status === 'approved'">• 已通过审核，等待签署领养协议/交接</div>
            <div v-else-if="data.status === 'rejected'">• 已拒绝：{{ data.reviewNote || '（无）' }}</div>
            <div v-else>• 领养流程已完成</div>
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
import { computed, defineComponent, h, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();
const route = useRoute();
const id = Number(route.params.id);

const reviewNote = ref("");

/** mock 数据（后续接后端：getAdoptionDetail(id)） */
const data = reactive({
  id,
  status: "pending", // pending|approved|rejected|done
  applyTime: "2024-01-20 10:30",
  reviewNote: "",

  pet: {
    id: 1,
    name: "小橘",
    breed: "橘猫",
    gender: "male",
    ageText: "约2岁",
    location: "北京大学 - 图书馆附近",
    statusText: "待领养",
    thumbBg: "linear-gradient(135deg, #ffd89b 0%, #19547b 100%)",
  },

  applicant: {
    name: "张三",
    phoneMasked: "138****8888",
    wechat: "wx_zhangsan",
    city: "北京市",
    address: "海淀区中关村大街1号",
    age: 28,
    job: "软件工程师",
    income: "15000-20000元",
  },

  reason:
    "我一直很喜欢小动物，特别是猫咪。在平台上看到小橘的照片后，被它可爱的样子深深吸引。我目前有稳定的工作和收入，居住环境也适合养猫，有足够的空间和时间来照顾它。我会给它一个温暖的家，定期带它体检，保证它的健康。希望能有机会领养小橘，让它成为我的家人。",

  env: {
    ownHouse: true,
    houseType: "商品房",
    area: "80平方米",
    allowPets: true,
    windowSafe: true,
    windowSafeText: "✓ 已封窗",
    familyMembers: "2人（本人+配偶）",
    raisedBefore: true,
    currentPets: "无",
    expYears: "3-5年",
    acceptVisit: true,
  },

  envPhotos: [
    { id: 1, bg: "linear-gradient(135deg, #ffd89b 0%, #19547b 100%)" },
    { id: 2, bg: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)" },
    { id: 3, bg: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)" },
  ],

  extra:
    "我家里已经准备好了猫砂盆、猫粮、猫玩具等所有必需品，并且已经了解了猫咪的基本护理知识。我会定期带它去体检，保证它的健康。如果遇到任何问题，我会及时联系平台寻求帮助。",
});

function goBack() {
  router.back();
}

function yesNo(v) {
  return v ? "✓ 是" : "✕ 否";
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

async function onApprove() {
  if (data.status === "approved") return ElMessage.info("该申请已通过");
  if (data.status === "done") return ElMessage.info("该申请已完成");
  data.status = "approved";
  data.reviewNote = reviewNote.value.trim();
  ElMessage.success("已通过申请（mock）");
}

async function onReject() {
  if (data.status === "rejected") return ElMessage.info("该申请已拒绝");
  if (!reviewNote.value.trim()) return ElMessage.warning("拒绝时必须填写审核意见");

  try {
    await ElMessageBox.confirm("确定拒绝该申请吗？", "拒绝确认", {
      type: "warning",
      confirmButtonText: "拒绝",
      cancelButtonText: "取消",
    });
    data.status = "rejected";
    data.reviewNote = reviewNote.value.trim();
    ElMessage.success("已拒绝申请（mock）");
  } catch {}
}

function onContact() {
  ElMessage.info(`联系申请人：${data.applicant.name}（${data.applicant.phoneMasked}）`);
}

/** 小组件：信息卡 */
const InfoCard = defineComponent({
  props: {
    label: { type: String, required: true },
    value: { type: String, required: true },
    ok: { type: Boolean, default: null }, // true/false/null
  },
  setup(props) {
    const valueCls = computed(() => (props.ok === true ? "ok" : props.ok === false ? "bad" : ""));
    return () =>
      h("div", { class: "info-card" }, [
        h("div", { class: "info-label" }, props.label),
        h("div", { class: ["info-value", valueCls.value] }, props.value),
      ]);
  },
});
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
  font-weight: 900;
  color: #111827;
}

.topbar-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  flex-wrap: wrap;
}

.panel {
  border-radius: 12px;
  border: none;
}

/* 宠物头部 */
.pet-head {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 18px;
}

.pet-thumb {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  flex-shrink: 0;
}

.pet-name {
  font-size: 20px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 10px;
}

.pet-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 8px;
}

.pill {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}
.pill-orange {
  background: #fff3e0;
  color: #e65100;
}
.pill-blue {
  background: #e3f2fd;
  color: #1976d2;
}
.pill-green {
  background: #e8f5e9;
  color: #388e3c;
}

.pet-loc {
  font-size: 13px;
  color: #6b7280;
}

.pet-status {
  margin-top: 8px;
  font-size: 13px;
  color: #16a34a;
  font-weight: 900;
}

.divider {
  height: 1px;
  background: #e5e7eb;
  margin: 0 2px 18px;
}

/* 分段 */
.section {
  margin-bottom: 22px;
}

.section-title {
  font-size: 15px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.grid2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-card {
  padding: 16px;
  background: #f9fafb;
  border-radius: 10px;
}

.info-label {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 6px;
}

.info-value {
  font-size: 14px;
  font-weight: 700;
  color: #111827;
  word-break: break-word;
}

.info-value.ok {
  color: #16a34a;
}
.info-value.bad {
  color: #ef4444;
}

.text-box {
  padding: 16px;
  background: #f9fafb;
  border-radius: 10px;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.9;
}

/* 环境照片 */
.photo-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.env-photo {
  aspect-ratio: 1;
  border-radius: 10px;
}

/* 审核记录 */
.record-box {
  padding: 16px;
  background: #fff7ed;
  border-left: 4px solid #ff8a00;
  border-radius: 10px;
}

.record-line {
  font-size: 14px;
  font-weight: 800;
  color: #111827;
  margin-bottom: 10px;
}

.record-status {
  font-weight: 900;
  margin-left: 6px;
}
.record-status.pending {
  color: #ff4d4f;
}
.record-status.approved {
  color: #16a34a;
}
.record-status.rejected {
  color: #ef4444;
}
.record-status.done {
  color: #2563eb;
}

.record-detail {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.8;
}

/* 审核意见 */
.review-box {
  margin-top: 8px;
  padding: 16px;
  background: #fff7ed;
  border-left: 4px solid #ff8a00;
  border-radius: 10px;
}

.review-title {
  font-size: 14px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 10px;
}

/* responsive */
@media (max-width: 992px) {
  .topbar {
    grid-template-columns: 90px 1fr;
  }
  .topbar-actions {
    grid-column: 1 / -1;
    justify-content: flex-end;
  }
  .grid2 {
    grid-template-columns: 1fr;
  }
  .photo-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<template>
  <div class="detail-wrap">
    <!-- 顶部条 -->
    <div class="topbar">
      <el-button class="back-btn" @click="goBack">← 返回</el-button>
      <div class="topbar-title">领养申请详情</div>
      <div class="topbar-actions">
        <el-button v-if="data.status === 'pending'" type="success" @click="onApprove">✓ 通过申请</el-button>
        <el-button v-if="data.status === 'pending'" type="danger" @click="onReject">✕ 拒绝申请</el-button>
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
            <div v-if="data.reviewedAt">• 审核时间：{{ data.reviewedAt }}</div>
            <div v-if="data.reviewNote">• 审核意见：{{ data.reviewNote }}</div>
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
import { computed, defineComponent, h, onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getAdoptionApplication,
  reviewAdoptionApplication,
} from "@/api/welfare/adoption";

const router = useRouter();
const route = useRoute();
const id = Number(route.params.id);

const reviewNote = ref("");

const data = reactive({
  id,
  status: "pending", // pending|approved|rejected|completed
  applyTime: "",
  reviewNote: "",
  reviewedAt: "",

  pet: {
    id: undefined,
    name: "",
    breed: "",
    gender: "male",
    ageText: "",
    location: "",
    statusText: "",
    thumbBg: "linear-gradient(135deg, #ffd89b 0%, #19547b 100%)",
  },

  applicant: {
    name: "",
    phoneMasked: "",
    phone: "",
    wechat: "",
    city: "",
    address: "",
    age: "",
    job: "",
    income: "",
  },

  reason: "",

  env: {
    ownHouse: false,
    houseType: "",
    area: "",
    allowPets: false,
    windowSafe: false,
    windowSafeText: "未填写",
    familyMembers: "未填写",
    raisedBefore: false,
    currentPets: "无",
    expYears: "未填写",
    acceptVisit: false,
  },

  envPhotos: [],

  extra: "",
});

onMounted(() => {
  loadDetail();
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
  if (data.status === "completed") return ElMessage.info("该申请已完成");
  await reviewAdoptionApplication(id, {
    status: "approved",
    adminNotes: reviewNote.value.trim() || "审批通过",
  });
  ElMessage.success("已通过申请");
  loadDetail();
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
    await reviewAdoptionApplication(id, {
      status: "rejected",
      rejectReason: reviewNote.value.trim(),
      adminNotes: reviewNote.value.trim(),
    });
    ElMessage.success("已拒绝申请");
    loadDetail();
  } catch {}
}

function onContact() {
  ElMessage.info(`联系申请人：${data.applicant.name}（${data.applicant.phoneMasked}）`);
}

async function loadDetail() {
  const res = await getAdoptionApplication(id);
  fillDetail(res.data || {});
}

function fillDetail(row) {
  data.id = row.id;
  data.status = row.status || "pending";
  data.applyTime = row.createdAt || "";
  data.reviewNote = row.rejectReason || row.adminNotes || "";
  data.reviewedAt = row.reviewedAt || "";
  reviewNote.value = data.reviewNote;

  data.pet.id = row.petId;
  data.pet.name = row.petName || "-";
  data.pet.breed = row.petBreed || "-";
  data.pet.gender = row.petGender || "male";
  data.pet.ageText = row.petAge || "-";
  data.pet.location = row.locationName || "-";
  data.pet.statusText = petStatusText(row.petStatus);
  data.pet.thumbBg = row.petMainImage
    ? `url("${row.petMainImage}") center/cover no-repeat`
    : "linear-gradient(135deg, #ffd89b 0%, #19547b 100%)";

  data.applicant.name = row.name || "-";
  data.applicant.phone = row.phone || "";
  data.applicant.phoneMasked = maskPhone(row.phone);
  data.applicant.wechat = row.wechat || "-";
  data.applicant.city = row.city || "-";
  data.applicant.address = row.address || "-";
  data.applicant.age = row.age || "-";
  data.applicant.job = row.occupation || "-";
  data.applicant.income = row.income ? `${row.income}元` : "-";

  data.reason = row.adoptionReason || "未填写";
  data.env.ownHouse = row.hasHouse === 1;
  data.env.houseType = row.houseType || "-";
  data.env.area = row.houseArea ? `${row.houseArea}平方米` : "-";
  data.env.allowPets = row.allowPet === 1;
  data.env.windowSafe = false;
  data.env.windowSafeText = "未填写";
  data.env.familyMembers = "未填写";
  data.env.raisedBefore = row.hasPets === 1;
  data.env.currentPets = row.currentPets || "无";
  data.env.expYears = row.petExperience || "未填写";
  data.env.acceptVisit = row.acceptRegularVisit === 1;
  data.envPhotos = parseJsonArray(row.houseImages).map((url, index) => ({
    id: index + 1,
    bg: `url("${url}") center/cover no-repeat`,
  }));
  data.extra = row.otherInfo || "未填写";
}

function petStatusText(status) {
  if (status === "available") return "待领养";
  if (status === "adopting") return "领养中";
  if (status === "adopted") return "已领养";
  return "-";
}

function parseJsonArray(value) {
  if (!value) return [];
  try {
    const parsed = JSON.parse(value);
    return Array.isArray(parsed) ? parsed.filter(Boolean) : [];
  } catch {
    return [];
  }
}

function maskPhone(phone = "") {
  return phone.replace(/^(\d{3})\d{4}(\d+)$/, "$1****$2") || "-";
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
.record-status.completed {
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

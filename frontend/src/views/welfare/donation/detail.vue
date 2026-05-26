<template>
  <div class="detail-wrap">
    <!-- top bar -->
    <div class="topbar">
      <div class="left">
        <el-button class="btn-back" @click="goBack">← 返回</el-button>
        <div class="title">捐款详情</div>
      </div>
      <div class="actions">
        <el-button type="success" class="btn" @click="markSuccess">✓ 标记成功</el-button>
        <el-button type="danger" class="btn" @click="refund">✗ 退款</el-button>
        <el-button type="primary" class="btn" @click="exportOne">📥 导出</el-button>
      </div>
    </div>

    <!-- 基本信息 -->
    <el-card shadow="hover" class="panel">
      <div class="panel-title">捐款信息</div>

      <div class="grid-3">
        <InfoItem label="订单号" :value="detail.orderNo" mono />
        <InfoItem label="捐款金额" :value="`¥ ${detail.amount}`" strong />
        <InfoItem label="捐款状态">
          <span class="tag" :class="statusClass(detail.status)">
            {{ statusText(detail.status) }}
          </span>
        </InfoItem>

        <InfoItem label="捐款项目" :value="projectText(detail.project)" />
        <InfoItem label="支付方式" :value="payText(detail.payType)" />
        <InfoItem label="是否匿名" :value="detail.anonymous ? '是' : '否'" />

        <InfoItem label="支付时间" :value="detail.payTime" />
        <InfoItem label="创建时间" :value="detail.createTime" />
        <InfoItem label="交易流水号" :value="detail.tradeNo || '-'" mono />
      </div>
    </el-card>

    <!-- 捐款人 -->
    <el-card shadow="hover" class="panel">
      <div class="panel-title">捐款人信息</div>

      <div class="user-box">
        <div class="avatar" :style="{ background: detail.avatarBg }">
          {{ detail.donor.slice(0, 1) }}
        </div>
        <div class="user-meta">
          <div class="name">
            {{ detail.anonymous ? '匿名用户' : detail.donor }}
            <span v-if="detail.anonymous" class="mini-tag">匿名</span>
          </div>
          <div class="phone">手机号：{{ maskPhone(detail.phone) }}</div>
          <div class="note" v-if="detail.note">留言：{{ detail.note }}</div>
        </div>
      </div>
    </el-card>

    <!-- 流转记录 -->
    <el-card shadow="hover" class="panel">
      <div class="panel-title">捐款流转记录</div>

      <div class="timeline">
        <div
          v-for="(item, idx) in detail.logs"
          :key="idx"
          class="timeline-item"
        >
          <div class="dot"></div>
          <div class="log">
            <div class="log-title">{{ item.title }}</div>
            <div class="log-time">{{ item.time }}</div>
            <div class="log-desc" v-if="item.desc">{{ item.desc }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 审核 / 处理意见 -->
    <el-card shadow="hover" class="panel warn">
      <div class="panel-title">处理意见</div>

      <el-input
        v-model="opinion"
        type="textarea"
        :rows="4"
        placeholder="请输入处理/退款说明（退款时必填）"
      />
    </el-card>
  </div>
</template>

<script setup>
import { defineComponent, h, reactive, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();
const route = useRoute();

const id = route.params.id;

/** mock detail */
const detail = reactive({
  id,
  orderNo: "DN202401200001",
  amount: 100,
  project: "medical",
  payType: "wechat",
  status: "success", // pending / success / refunded
  anonymous: false,

  donor: "张三",
  phone: "13888888888",
  note: "希望用于小橘的医疗",

  createTime: "2024-01-20 10:28",
  payTime: "2024-01-20 10:30",
  tradeNo: "WX2024012000098765",

  avatarBg: "linear-gradient(135deg,#667eea 0%,#764ba2 100%)",

  logs: [
    { title: "创建捐款订单", time: "2024-01-20 10:28" },
    { title: "支付成功", time: "2024-01-20 10:30", desc: "微信支付完成" },
  ],
});

const opinion = ref("");

function goBack() {
  router.back();
}

function exportOne() {
  ElMessage.success("已导出该笔捐款（mock）");
}

function markSuccess() {
  if (detail.status === "success") {
    ElMessage.info("该捐款已是成功状态");
    return;
  }
  detail.status = "success";
  detail.logs.push({
    title: "管理员标记成功",
    time: new Date().toLocaleString(),
  });
  ElMessage.success("已标记为成功");
}

async function refund() {
  if (!opinion.value.trim()) {
    ElMessage.warning("退款时必须填写处理说明");
    return;
  }

  try {
    await ElMessageBox.confirm(
      "确定对该捐款执行退款操作吗？",
      "退款确认",
      { type: "warning" }
    );
    detail.status = "refunded";
    detail.logs.push({
      title: "管理员退款",
      time: new Date().toLocaleString(),
      desc: opinion.value,
    });
    ElMessage.success("退款完成（mock）");
  } catch {}
}

/** helpers */
function maskPhone(p) {
  if (!p) return "-";
  return `${p.slice(0, 3)}****${p.slice(-4)}`;
}
function statusText(v) {
  if (v === "pending") return "待支付";
  if (v === "success") return "成功";
  return "已退款";
}
function statusClass(v) {
  if (v === "success") return "tag-ok";
  if (v === "pending") return "tag-warn";
  return "tag-bad";
}
function projectText(v) {
  if (v === "medical") return "医疗救助";
  if (v === "food") return "猫粮补给";
  if (v === "tnr") return "绝育计划";
  return "其他";
}
function payText(v) {
  if (v === "wechat") return "微信支付";
  if (v === "alipay") return "支付宝";
  return "线下转账";
}

/** Info item component */
const InfoItem = defineComponent({
  props: {
    label: String,
    value: String,
    mono: Boolean,
    strong: Boolean,
  },
  setup(props, { slots }) {
    return () =>
      h("div", { class: "info-item" }, [
        h("div", { class: "info-label" }, props.label),
        slots.default
          ? slots.default()
          : h(
              "div",
              {
                class: [
                  "info-value",
                  props.mono && "mono",
                  props.strong && "strong",
                ],
              },
              props.value
            ),
      ]);
  },
});
</script>

<style scoped>
.detail-wrap {
  padding: 16px 18px 28px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

/* top */
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}
.left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.title {
  font-size: 18px;
  font-weight: 900;
}
.actions {
  display: flex;
  gap: 10px;
}
.btn {
  border-radius: 10px;
  font-weight: 900;
}
.btn-back {
  border-radius: 10px;
}

/* panels */
.panel {
  border-radius: 12px;
  border: none;
  margin-bottom: 16px;
}
.panel.warn {
  background: #fff7e6;
  border-left: 4px solid #fa8c16;
}
.panel-title {
  font-size: 15px;
  font-weight: 900;
  margin-bottom: 14px;
}

/* info grid */
.grid-3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px 18px;
}
.info-item {
  background: #f9fafb;
  border-radius: 10px;
  padding: 14px;
}
.info-label {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 6px;
  font-weight: 800;
}
.info-value {
  font-size: 14px;
  font-weight: 800;
  color: #111827;
}
.info-value.strong {
  font-size: 18px;
}
.mono {
  font-family: ui-monospace, Menlo, Consolas, monospace;
}

/* user */
.user-box {
  display: flex;
  gap: 16px;
  align-items: center;
}
.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  color: #fff;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
.user-meta .name {
  font-weight: 900;
  margin-bottom: 6px;
}
.phone {
  font-size: 13px;
  color: #6b7280;
}
.note {
  margin-top: 6px;
  font-size: 13px;
  color: #374151;
}
.mini-tag {
  margin-left: 8px;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  background: #f3f4f6;
}

/* timeline */
.timeline {
  padding-left: 12px;
}
.timeline-item {
  display: flex;
  gap: 12px;
  margin-bottom: 14px;
}
.dot {
  width: 10px;
  height: 10px;
  background: #1677ff;
  border-radius: 50%;
  margin-top: 6px;
}
.log-title {
  font-weight: 900;
}
.log-time {
  font-size: 12px;
  color: #6b7280;
}
.log-desc {
  font-size: 13px;
  color: #374151;
}

/* tags */
.tag {
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 900;
}
.tag-ok {
  background: #e8f5e9;
  color: #16a34a;
}
.tag-warn {
  background: #fff3e0;
  color: #fa8c16;
}
.tag-bad {
  background: #ffe8e8;
  color: #ff4d4f;
}

/* responsive */
@media (max-width: 992px) {
  .grid-3 {
    grid-template-columns: 1fr;
  }
  .topbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>

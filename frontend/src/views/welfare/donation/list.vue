<template>
  <div class="don-wrap">
    <!-- topbar -->
    <div class="topbar">
      <div class="title">捐款管理</div>
      <div class="actions">
        <el-button class="btn" @click="resetQuery">重置</el-button>
        <el-button type="primary" class="btn" @click="exportExcel">📥 导出报表</el-button>
        <el-button type="primary" class="btn btn-add" @click="goCreate">＋ 新增捐款</el-button>
      </div>
    </div>

    <!-- KPI -->
    <div class="kpi-grid">
      <KpiCard label="今日捐款金额" :value="kpi.todayAmount" suffix="元" trend="+8.2%" trendType="up" />
      <KpiCard label="近7天捐款金额" :value="kpi.weekAmount" suffix="元" trend="+12.5%" trendType="up" />
      <KpiCard label="累计捐款金额" :value="kpi.totalAmount" suffix="元" valueType="primary" />
      <KpiCard label="成功笔数" :value="kpi.successCount" suffix="笔" valueType="success" />
    </div>

    <!-- filters -->
    <el-card shadow="hover" class="panel">
      <div class="panel-title">筛选条件</div>

      <div class="filters">
        <div class="f-item">
          <div class="f-label">关键词</div>
          <el-input
            v-model="query.keyword"
            placeholder="订单号 / 捐款人 / 手机号"
            clearable
            class="f-ctrl"
          />
        </div>

        <div class="f-item">
          <div class="f-label">时间范围</div>
          <el-select v-model="query.range" class="f-ctrl" clearable placeholder="全部">
            <el-option label="近7天" value="7" />
            <el-option label="近30天" value="30" />
            <el-option label="近90天" value="90" />
            <el-option label="近一年" value="365" />
          </el-select>
        </div>

        <div class="f-item">
          <div class="f-label">捐款项目</div>
          <el-select v-model="query.project" class="f-ctrl" clearable placeholder="全部">
            <el-option label="猫粮补给" value="food" />
            <el-option label="医疗救助" value="medical" />
            <el-option label="绝育计划" value="tnr" />
            <el-option label="临时安置" value="temp" />
            <el-option label="其他" value="other" />
          </el-select>
        </div>

        <div class="f-item">
          <div class="f-label">支付方式</div>
          <el-select v-model="query.payType" class="f-ctrl" clearable placeholder="全部">
            <el-option label="微信" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="线下转账" value="offline" />
          </el-select>
        </div>

        <div class="f-item">
          <div class="f-label">状态</div>
          <el-select v-model="query.status" class="f-ctrl" clearable placeholder="全部">
            <el-option label="待支付" value="pending" />
            <el-option label="成功" value="success" />
            <el-option label="失败" value="failed" />
            <el-option label="退款中" value="refunding" />
            <el-option label="已退款" value="refunded" />
          </el-select>
        </div>

        <div class="f-item">
          <div class="f-label">匿名</div>
          <el-select v-model="query.anonymous" class="f-ctrl" clearable placeholder="全部">
            <el-option label="匿名" value="yes" />
            <el-option label="非匿名" value="no" />
          </el-select>
        </div>

        <div class="f-actions">
          <el-button type="primary" class="btn" @click="doSearch">查询</el-button>
        </div>
      </div>
    </el-card>

    <!-- table -->
    <el-card shadow="hover" class="panel" style="margin-top: 16px;">
      <div class="panel-title">捐款记录</div>

      <el-table :data="pagedRows" border class="table" style="width: 100%">
        <el-table-column label="订单号" min-width="170">
          <template #default="{ row }">
            <div class="mono">{{ row.orderNo }}</div>
          </template>
        </el-table-column>

        <el-table-column label="捐款人" min-width="180">
          <template #default="{ row }">
            <div class="usercell">
              <div class="avatar" :style="{ background: row.avatarBg }">{{ row.donor.slice(0,1) }}</div>
              <div class="uinfo">
                <div class="uname">
                  {{ row.anonymous ? '匿名用户' : row.donor }}
                  <span v-if="row.anonymous" class="mini-tag">匿名</span>
                </div>
                <div class="uphone">{{ maskPhone(row.phone) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="金额" width="110" align="center">
          <template #default="{ row }">
            <div class="amt">¥ {{ row.amount }}</div>
          </template>
        </el-table-column>

        <el-table-column label="项目" width="110" align="center">
          <template #default="{ row }">
            <span class="tag tag-project">{{ projectText(row.project) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="方式" width="100" align="center">
          <template #default="{ row }">
            <span class="tag" :class="payTagClass(row.payType)">{{ payText(row.payType) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="110" align="center">
          <template #default="{ row }">
            <span class="tag" :class="statusTagClass(row.status)">{{ statusText(row.status) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="时间" width="160" align="center">
          <template #default="{ row }">
            {{ row.time }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <div class="ops">
              <el-button size="small" class="btn-op btn-view" @click="goDetail(row)">详情</el-button>
              <el-button size="small" class="btn-op btn-flag" @click="markException(row)">标记异常</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <div class="pager-left">
          显示第 {{ startIndex }}-{{ endIndex }} 条，共 {{ filteredRows.length }} 条
        </div>
        <el-pagination
          background
          layout="prev, pager, next"
          :page-size="page.size"
          :total="filteredRows.length"
          v-model:current-page="page.current"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, defineComponent, h, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();

/** query */
const query = reactive({
  keyword: "",
  range: "7",
  project: "",
  payType: "",
  status: "",
  anonymous: "",
});

/** pagination */
const page = reactive({
  current: 1,
  size: 8,
});

/** KPI mock */
const kpi = reactive({
  todayAmount: 1280,
  weekAmount: 16890,
  totalAmount: 256780,
  successCount: 892,
});

/** rows mock */
const rows = ref([
  {
    id: 101,
    orderNo: "DN202401200001",
    donor: "张三",
    phone: "13888888888",
    amount: 50,
    project: "food",
    payType: "wechat",
    status: "success",
    anonymous: false,
    time: "2024-01-20 10:30",
    avatarBg: "linear-gradient(135deg,#667eea 0%,#764ba2 100%)",
  },
  {
    id: 102,
    orderNo: "DN202401200002",
    donor: "李四",
    phone: "13912345678",
    amount: 100,
    project: "medical",
    payType: "alipay",
    status: "success",
    anonymous: true,
    time: "2024-01-20 11:10",
    avatarBg: "linear-gradient(135deg,#f093fb 0%,#f5576c 100%)",
  },
  {
    id: 103,
    orderNo: "DN202401190019",
    donor: "王五",
    phone: "13700001111",
    amount: 20,
    project: "tnr",
    payType: "wechat",
    status: "pending",
    anonymous: false,
    time: "2024-01-19 20:05",
    avatarBg: "linear-gradient(135deg,#4facfe 0%,#00f2fe 100%)",
  },
  {
    id: 104,
    orderNo: "DN202401180007",
    donor: "赵六",
    phone: "13622223333",
    amount: 200,
    project: "temp",
    payType: "offline",
    status: "success",
    anonymous: false,
    time: "2024-01-18 09:22",
    avatarBg: "linear-gradient(135deg,#43e97b 0%,#38f9d7 100%)",
  },
  {
    id: 105,
    orderNo: "DN202401170003",
    donor: "匿名",
    phone: "13566667777",
    amount: 30,
    project: "other",
    payType: "wechat",
    status: "failed",
    anonymous: true,
    time: "2024-01-17 08:40",
    avatarBg: "linear-gradient(135deg,#fa709a 0%,#fee140 100%)",
  },
  {
    id: 106,
    orderNo: "DN202401160012",
    donor: "周七",
    phone: "13199990000",
    amount: 500,
    project: "medical",
    payType: "alipay",
    status: "refunded",
    anonymous: false,
    time: "2024-01-16 13:18",
    avatarBg: "linear-gradient(135deg,#30cfd0 0%,#330867 100%)",
  },
  {
    id: 107,
    orderNo: "DN202401150009",
    donor: "孙八",
    phone: "13211112222",
    amount: 80,
    project: "food",
    payType: "wechat",
    status: "refunding",
    anonymous: false,
    time: "2024-01-15 18:46",
    avatarBg: "linear-gradient(135deg,#f7971e 0%,#ffd200 100%)",
  },
  {
    id: 108,
    orderNo: "DN202401140002",
    donor: "钱九",
    phone: "13333334444",
    amount: 60,
    project: "tnr",
    payType: "offline",
    status: "success",
    anonymous: false,
    time: "2024-01-14 09:05",
    avatarBg: "linear-gradient(135deg,#00c6ff 0%,#0072ff 100%)",
  },
]);

/** filtering */
const filteredRows = computed(() => {
  const k = query.keyword.trim().toLowerCase();
  return rows.value.filter((r) => {
    // keyword
    const hitK =
      !k ||
      r.orderNo.toLowerCase().includes(k) ||
      (r.donor || "").toLowerCase().includes(k) ||
      (r.phone || "").includes(k);

    const hitProject = !query.project || r.project === query.project;
    const hitPay = !query.payType || r.payType === query.payType;
    const hitStatus = !query.status || r.status === query.status;

    const hitAnonymous =
      !query.anonymous ||
      (query.anonymous === "yes" ? r.anonymous : !r.anonymous);

    // range（mock：按字符串日期简单筛选；后续接后端即可）
    const hitRange = !!query.range ? true : true;

    return hitK && hitProject && hitPay && hitStatus && hitAnonymous && hitRange;
  });
});

const pagedRows = computed(() => {
  const start = (page.current - 1) * page.size;
  return filteredRows.value.slice(start, start + page.size);
});

const startIndex = computed(() => {
  if (filteredRows.value.length === 0) return 0;
  return (page.current - 1) * page.size + 1;
});
const endIndex = computed(() => {
  const end = page.current * page.size;
  return Math.min(end, filteredRows.value.length);
});

/** actions */
function doSearch() {
  page.current = 1;
  ElMessage.success("已查询（mock）");
}

function resetQuery() {
  query.keyword = "";
  query.range = "7";
  query.project = "";
  query.payType = "";
  query.status = "";
  query.anonymous = "";
  page.current = 1;
  ElMessage.info("已重置");
}

function exportExcel() {
  ElMessage.success("已开始导出（mock）");
}

function goDetail(row) {
  router.push(`/welfare/donation/detail/${row.id}`);
}

function goCreate() {
  router.push(`/welfare/donation/create`);
}

function markException(row) {
  ElMessage.warning(`已标记异常：${row.orderNo}（mock）`);
}

/** helpers */
function maskPhone(p) {
  if (!p) return "-";
  if (p.length < 7) return p;
  return `${p.slice(0, 3)}****${p.slice(-4)}`;
}

function projectText(v) {
  if (v === "food") return "猫粮";
  if (v === "medical") return "医疗";
  if (v === "tnr") return "绝育";
  if (v === "temp") return "安置";
  return "其他";
}
function payText(v) {
  if (v === "wechat") return "微信";
  if (v === "alipay") return "支付宝";
  return "线下";
}
function statusText(v) {
  if (v === "pending") return "待支付";
  if (v === "success") return "成功";
  if (v === "failed") return "失败";
  if (v === "refunding") return "退款中";
  return "已退款";
}

function payTagClass(v) {
  if (v === "wechat") return "tag-wx";
  if (v === "alipay") return "tag-ali";
  return "tag-off";
}
function statusTagClass(v) {
  if (v === "success") return "tag-ok";
  if (v === "pending") return "tag-warn";
  if (v === "failed") return "tag-bad";
  if (v === "refunding") return "tag-warn";
  return "tag-muted";
}

/** KPI Card component */
const KpiCard = defineComponent({
  props: {
    label: { type: String, required: true },
    value: { type: [String, Number], required: true },
    suffix: { type: String, default: "" },
    trend: { type: String, default: "" },
    trendType: { type: String, default: "up" }, // up/down
    valueType: { type: String, default: "" }, // primary/success/danger
  },
  setup(props) {
    const vCls = computed(() => (props.valueType ? `v-${props.valueType}` : ""));
    const tCls = computed(() => (props.trendType === "down" ? "t-down" : "t-up"));
    const arrow = computed(() => (props.trendType === "down" ? "↓" : "↑"));
    return () =>
      h("div", { class: "kpi" }, [
        h("div", { class: "kpi-label" }, props.label),
        h("div", { class: ["kpi-value", vCls.value] }, `${props.value}${props.suffix}`),
        props.trend
          ? h("div", { class: ["kpi-trend", tCls.value] }, `${arrow.value} 较上期 ${props.trend}`)
          : h("div", { class: "kpi-trend kpi-empty" }, " "),
      ]);
  },
});
</script>

<style scoped>
.don-wrap {
  padding: 16px 18px 28px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

/* top */
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.title {
  font-size: 18px;
  font-weight: 900;
  color: #111827;
}
.actions {
  display: flex;
  gap: 10px;
}
.btn {
  border-radius: 10px;
  height: 36px;
  font-weight: 900;
}
.btn-add {
  background: #1677ff;
  border-color: #1677ff;
}

/* KPI */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}
.kpi {
  background: #fff;
  border-radius: 12px;
  padding: 18px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.kpi-label {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 10px;
  font-weight: 800;
}
.kpi-value {
  font-size: 26px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 6px;
}
.kpi-value.v-primary { color: #1677ff; }
.kpi-value.v-success { color: #16a34a; }
.kpi-trend {
  font-size: 12px;
  font-weight: 800;
}
.kpi-trend.t-up { color: #16a34a; }
.kpi-trend.t-down { color: #ff4d4f; }
.kpi-empty { color: transparent; }

/* panels */
.panel {
  border-radius: 12px;
  border: none;
}
.panel-title {
  font-size: 15px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e5e7eb;
}

/* filters */
.filters {
  display: grid;
  grid-template-columns: repeat(3, minmax(220px, 1fr));
  gap: 14px 16px;
}
.f-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.f-label {
  width: 70px;
  font-size: 13px;
  font-weight: 900;
  color: #374151;
}
.f-ctrl {
  flex: 1;
}
.f-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

/* table */
.table :deep(.el-table__cell) {
  padding-top: 14px;
  padding-bottom: 14px;
}
.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-weight: 800;
  color: #111827;
}
.usercell {
  display: flex;
  gap: 12px;
  align-items: center;
}
.avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  color: #fff;
  font-weight: 900;
  display: flex;
  align-items: center;
  justify-content: center;
}
.uinfo .uname {
  font-size: 14px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.mini-tag {
  font-size: 12px;
  font-weight: 900;
  padding: 2px 8px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #6b7280;
}
.uinfo .uphone {
  font-size: 12px;
  color: #6b7280;
}

.amt {
  font-weight: 900;
  color: #111827;
}

.tag {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 900;
  line-height: 1;
}
.tag-project { background: #f3f4f6; color: #6b7280; }
.tag-wx { background: #e8f5e9; color: #16a34a; }
.tag-ali { background: #e8f1ff; color: #1677ff; }
.tag-off { background: #fff3e0; color: #fa8c16; }

.tag-ok { background: #e8f5e9; color: #16a34a; }
.tag-warn { background: #fff3e0; color: #fa8c16; }
.tag-bad { background: #ffe8e8; color: #ff4d4f; }
.tag-muted { background: #f3f4f6; color: #6b7280; }

.ops {
  display: flex;
  justify-content: center;
  gap: 8px;
}
.btn-op {
  border-radius: 8px;
  font-weight: 900;
  border: none;
}
.btn-view { background: #e8f1ff; color: #1677ff; }
.btn-flag { background: #fff3e0; color: #fa8c16; }

/* pager */
.pager {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #6b7280;
  font-size: 12px;
}

/* responsive */
@media (max-width: 1200px) {
  .kpi-grid { grid-template-columns: repeat(2, 1fr); }
  .filters { grid-template-columns: repeat(2, minmax(220px, 1fr)); }
}
@media (max-width: 640px) {
  .kpi-grid { grid-template-columns: 1fr; }
  .filters { grid-template-columns: 1fr; }
  .topbar { flex-direction: column; align-items: flex-start; gap: 10px; }
  .actions { width: 100%; flex-wrap: wrap; }
}
</style>

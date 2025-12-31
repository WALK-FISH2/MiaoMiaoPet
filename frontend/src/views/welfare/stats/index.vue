<template>
  <div class="stats-wrap">
    <div class="topbar">
      <div class="title">数据统计</div>
      <div class="actions">
        <el-select v-model="range" class="range" placeholder="近7天">
          <el-option label="近7天" value="7" />
          <el-option label="近30天" value="30" />
          <el-option label="近90天" value="90" />
          <el-option label="近一年" value="365" />
          <el-option label="全部" value="all" />
        </el-select>
        <el-button type="primary" @click="exportReport">📥 导出报表</el-button>
      </div>
    </div>

    <!-- 4 KPI -->
    <div class="kpi-grid">
      <KpiCard label="总宠物数" :value="156" trend="+12" trendType="up" />
      <KpiCard label="待领养" :value="23" trend="-5" trendType="down" valueType="danger" />
      <KpiCard label="已领养" :value="133" trend="+17" trendType="up" valueType="success" />
      <KpiCard label="领养率" value="85.3%" trend="+2.1%" trendType="up" valueType="primary" />
    </div>

    <!-- charts row 1 -->
    <div class="grid2">
      <el-card shadow="hover" class="panel">
        <div class="panel-title">宠物数量趋势</div>
        <div ref="lineRef" class="chart" />
      </el-card>

      <el-card shadow="hover" class="panel">
        <div class="panel-title">领养状态分布</div>
        <div ref="pieRef" class="chart" />
      </el-card>
    </div>

    <!-- charts row 2 -->
    <div class="grid2">
      <el-card shadow="hover" class="panel">
        <div class="panel-title">用户活跃度</div>
        <div ref="barRef" class="chart" />
      </el-card>

      <el-card shadow="hover" class="panel">
        <div class="panel-title">内容上传趋势</div>
        <div ref="areaRef" class="chart" />
      </el-card>
    </div>

    <!-- detail stats -->
    <el-card shadow="hover" class="panel" style="margin-top: 16px;">
      <div class="panel-title">详细数据统计</div>

      <div class="detail-grid">
        <MiniCard label="总用户数" value="2,458" trend="+156" />
        <MiniCard label="总上传数" value="8,923" trend="+423" />
        <MiniCard label="总点赞数" value="45,678" trend="+2,345" />
        <MiniCard label="总评论数" value="3,456" trend="+189" />
        <MiniCard label="待审核内容" value="28" status="todo" sub="需处理" />
        <MiniCard label="待审核申请" value="12" status="todo" sub="需处理" />
      </div>
    </el-card>

    <!-- top10 -->
    <el-card shadow="hover" class="panel" style="margin-top: 16px;">
      <div class="panel-title">热门宠物TOP10</div>

      <div class="top-list">
        <div v-for="(p, idx) in top10" :key="p.id" class="top-item">
          <div class="thumb" :style="{ background: p.bg }"></div>

          <div class="mid">
            <div class="name">{{ p.name }}</div>
            <div class="loc">📍 {{ p.location }}</div>
          </div>

          <div class="right">
            <div class="hot">{{ p.hot.toLocaleString() }}</div>
            <div class="hot-label">热度</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, defineComponent, h, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from "vue";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";

const range = ref("7");

const top10 = ref([
  { id: 1, name: "小橘", location: "北京大学", hot: 9999, bg: "linear-gradient(135deg,#ffd89b 0%,#19547b 100%)" },
  { id: 2, name: "小花", location: "清华大学", hot: 8888, bg: "linear-gradient(135deg,#f093fb 0%,#f5576c 100%)" },
  { id: 3, name: "小黑", location: "北京大学", hot: 7777, bg: "linear-gradient(135deg,#4facfe 0%,#00f2fe 100%)" },
]);

function exportReport() {
  ElMessage.success("已开始导出（mock）");
}

/** charts */
const lineRef = ref();
const pieRef = ref();
const barRef = ref();
const areaRef = ref();

let lineChart, pieChart, barChart, areaChart;

function buildXAxis(days = 7) {
  const arr = [];
  for (let i = days - 1; i >= 0; i--) arr.push(`-${i}d`);
  return arr;
}

function makeOptions(days) {
  const x = buildXAxis(days);

  const lineOpt = {
    grid: { left: 40, right: 18, top: 20, bottom: 30 },
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: x, boundaryGap: false },
    yAxis: { type: "value" },
    series: [
      {
        type: "line",
        data: x.map((_, i) => 120 + i * 2 + (i % 2 ? 3 : 0)),
        smooth: true,
        areaStyle: undefined,
      },
    ],
  };

  const pieOpt = {
    tooltip: { trigger: "item" },
    legend: { bottom: 0 },
    series: [
      {
        type: "pie",
        radius: ["45%", "70%"],
        itemStyle: { borderRadius: 8, borderWidth: 2, borderColor: "#fff" },
        data: [
          { value: 23, name: "待领养" },
          { value: 133, name: "已领养" },
        ],
      },
    ],
  };

  const barOpt = {
    grid: { left: 40, right: 18, top: 20, bottom: 30 },
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: x },
    yAxis: { type: "value" },
    series: [
      {
        type: "bar",
        data: x.map((_, i) => 60 + i * 4 + (i % 3) * 6),
        barMaxWidth: 26,
      },
    ],
  };

  const areaOpt = {
    grid: { left: 40, right: 18, top: 20, bottom: 30 },
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: x, boundaryGap: false },
    yAxis: { type: "value" },
    series: [
      {
        type: "line",
        data: x.map((_, i) => 200 + i * 8 + (i % 2 ? 15 : 0)),
        smooth: true,
        areaStyle: {},
      },
    ],
  };

  return { lineOpt, pieOpt, barOpt, areaOpt };
}

function daysFromRange() {
  if (range.value === "7") return 7;
  if (range.value === "30") return 12; // 为了图不太密，mock 取 12 个点
  if (range.value === "90") return 14;
  if (range.value === "365") return 16;
  return 18;
}

function initCharts() {
  const d = daysFromRange();
  const { lineOpt, pieOpt, barOpt, areaOpt } = makeOptions(d);

  lineChart = echarts.init(lineRef.value);
  pieChart = echarts.init(pieRef.value);
  barChart = echarts.init(barRef.value);
  areaChart = echarts.init(areaRef.value);

  lineChart.setOption(lineOpt);
  pieChart.setOption(pieOpt);
  barChart.setOption(barOpt);
  areaChart.setOption(areaOpt);
}

function resizeAll() {
  lineChart?.resize();
  pieChart?.resize();
  barChart?.resize();
  areaChart?.resize();
}

watch(range, async () => {
  await nextTick();
  const d = daysFromRange();
  const { lineOpt, pieOpt, barOpt, areaOpt } = makeOptions(d);
  lineChart?.setOption(lineOpt, true);
  pieChart?.setOption(pieOpt, true);
  barChart?.setOption(barOpt, true);
  areaChart?.setOption(areaOpt, true);
});

onMounted(() => {
  initCharts();
  window.addEventListener("resize", resizeAll);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeAll);
  lineChart?.dispose();
  pieChart?.dispose();
  barChart?.dispose();
  areaChart?.dispose();
});

/** components */
const KpiCard = defineComponent({
  props: {
    label: { type: String, required: true },
    value: { type: [String, Number], required: true },
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
        h("div", { class: ["kpi-value", vCls.value] }, String(props.value)),
        h("div", { class: ["kpi-trend", tCls.value] }, `${arrow.value} 较上月 ${props.trend}`),
      ]);
  },
});

const MiniCard = defineComponent({
  props: {
    label: { type: String, required: true },
    value: { type: String, required: true },
    trend: { type: String, default: "" },
    status: { type: String, default: "" }, // todo
    sub: { type: String, default: "" },
  },
  setup(props) {
    const isTodo = computed(() => props.status === "todo");
    return () =>
      h("div", { class: "mini" }, [
        h("div", { class: "mini-label" }, props.label),
        h("div", { class: ["mini-value", isTodo.value ? "mini-danger" : ""] }, props.value),
        props.status === "todo"
          ? h("div", { class: "mini-sub mini-danger" }, props.sub || "需处理")
          : h("div", { class: "mini-sub mini-up" }, `↑ ${props.trend}`),
      ]);
  },
});
</script>

<style scoped>
.stats-wrap {
  padding: 16px 18px 28px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

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
  align-items: center;
}

.range {
  width: 120px;
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
  padding: 18px 18px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.kpi-label {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 10px;
  font-weight: 700;
}

.kpi-value {
  font-size: 28px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 6px;
}

.kpi-value.v-danger { color: #ff4d4f; }
.kpi-value.v-success { color: #16a34a; }
.kpi-value.v-primary { color: #1677ff; }

.kpi-trend {
  font-size: 12px;
  font-weight: 700;
}
.kpi-trend.t-up { color: #16a34a; }
.kpi-trend.t-down { color: #ff4d4f; }

/* panels */
.grid2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

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

.chart {
  height: 300px;
  border-radius: 10px;
  background: #f9fafb;
}

/* detail grid */
.detail-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.mini {
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
}

.mini-label {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 10px;
  font-weight: 700;
}

.mini-value {
  font-size: 24px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 6px;
}

.mini-sub {
  font-size: 12px;
  font-weight: 800;
}

.mini-up { color: #16a34a; }
.mini-danger { color: #ff4d4f; }

/* top list */
.top-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 10px;
}

.thumb {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  flex-shrink: 0;
}

.mid {
  flex: 1;
}

.name {
  font-size: 14px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 4px;
}

.loc {
  font-size: 12px;
  color: #6b7280;
}

.right {
  text-align: right;
}

.hot {
  font-size: 16px;
  font-weight: 900;
  color: #ff6b6b;
  margin-bottom: 4px;
}

.hot-label {
  font-size: 12px;
  color: #9ca3af;
}

/* responsive */
@media (max-width: 1200px) {
  .kpi-grid { grid-template-columns: repeat(2, 1fr); }
  .detail-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 992px) {
  .grid2 { grid-template-columns: 1fr; }
}
@media (max-width: 640px) {
  .kpi-grid { grid-template-columns: 1fr; }
  .detail-grid { grid-template-columns: 1fr; }
  .topbar { flex-direction: column; align-items: flex-start; gap: 10px; }
}
</style>

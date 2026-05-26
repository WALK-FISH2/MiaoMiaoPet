<template>
  <div class="members-wrap">
    <div class="topbar">
      <div class="title">人员管理</div>
      <div class="actions">
        <el-button type="primary" class="btn-add" @click="openAdd">＋ 添加成员</el-button>
      </div>
    </div>

    <div class="layout">
      <!-- left org -->
      <el-card shadow="hover" class="panel org">
        <div class="panel-title">组织信息</div>

        <div class="org-hero">
          <div class="org-logo"></div>
          <div class="org-name">北京大学流浪猫管理</div>
          <div class="org-id">组织ID: ORG001</div>
        </div>

        <div class="org-info">
          <div class="info-item">
            <div class="k">组织类型</div>
            <div class="v">学校</div>
          </div>
          <div class="info-item">
            <div class="k">所在地区</div>
            <div class="v">北京市海淀区</div>
          </div>
          <div class="info-item">
            <div class="k">成员总数</div>
            <div class="v"><b>12</b> 人</div>
          </div>
          <div class="info-item">
            <div class="k">创建时间</div>
            <div class="v">2023-01-15</div>
          </div>
        </div>

        <el-button class="btn-edit-org" type="primary" plain @click="editOrg">
          ✏️ 编辑组织信息
        </el-button>
      </el-card>

      <!-- right list -->
      <el-card shadow="hover" class="panel list">
        <div class="panel-title-row">
          <div class="panel-title">成员列表</div>
          <div class="filters">
            <el-input
              v-model="query.keyword"
              class="search"
              placeholder="搜索成员..."
              clearable
            />
            <el-select v-model="query.role" class="role" placeholder="全部角色" clearable>
              <el-option label="超级管理员" value="super" />
              <el-option label="管理员" value="admin" />
              <el-option label="审核员" value="reviewer" />
              <el-option label="普通成员" value="member" />
            </el-select>
          </div>
        </div>

        <el-table
          :data="pagedRows"
          class="table"
          border
          style="width: 100%"
        >
          <el-table-column type="selection" width="46" />
          <el-table-column label="成员信息" min-width="260">
            <template #default="{ row }">
              <div class="usercell">
                <div class="avatar" :style="{ background: row.avatarBg }">{{ row.name.slice(0,1) }}</div>
                <div class="uinfo">
                  <div class="uname">{{ row.name }}</div>
                  <div class="umail">{{ row.email }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="角色" width="80" align="center">
            <template #default="{ row }">
              <span :class="['tag', roleTagClass(row.role)]">{{ roleText(row.role) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="状态" width="80" align="center">
            <template #default="{ row }">
              <span :class="['tag', row.enabled ? 'tag-ok' : 'tag-ban']">
                {{ row.enabled ? '正常' : '已禁用' }}
              </span>
            </template>
          </el-table-column>

          <el-table-column label="加入时间" width="110" align="center">
            <template #default="{ row }">
              {{ row.joinDate }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <div class="ops">
                <el-button size="small" class="btn-op btn-edit" @click="editRow(row)">编辑</el-button>
                <el-button size="small" class="btn-op btn-del" @click="delRow(row)">删除</el-button>
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

    <!-- mock dialog -->
    <el-dialog v-model="dialog.open" :title="dialog.mode === 'add' ? '添加成员' : '编辑成员'" width="520px">
      <el-form label-width="90px">
        <el-form-item label="姓名">
          <el-input v-model="dialog.form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="dialog.form.email" placeholder="name@example.com" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="dialog.form.role" style="width: 100%">
            <el-option label="超级管理员" value="super" />
            <el-option label="管理员" value="admin" />
            <el-option label="审核员" value="reviewer" />
            <el-option label="普通成员" value="member" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="dialog.form.enabled" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialog.open=false">取消</el-button>
        <el-button type="primary" @click="saveDialog">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";

const query = reactive({
  keyword: "",
  role: "",
});

const page = reactive({
  current: 1,
  size: 5,
});

const rows = ref([
  { id: 1, name: "张三", email: "zhangsan@example.com", role: "super", enabled: true, joinDate: "2023-01-15", avatarBg: "linear-gradient(135deg,#667eea 0%,#764ba2 100%)" },
  { id: 2, name: "李四", email: "lisi@example.com", role: "admin", enabled: true, joinDate: "2023-02-20", avatarBg: "linear-gradient(135deg,#f093fb 0%,#f5576c 100%)" },
  { id: 3, name: "王五", email: "wangwu@example.com", role: "reviewer", enabled: true, joinDate: "2023-03-10", avatarBg: "linear-gradient(135deg,#4facfe 0%,#00f2fe 100%)" },
  { id: 4, name: "赵六", email: "zhaoliu@example.com", role: "member", enabled: true, joinDate: "2023-04-05", avatarBg: "linear-gradient(135deg,#43e97b 0%,#38f9d7 100%)" },
  { id: 5, name: "孙七", email: "sunqi@example.com", role: "reviewer", enabled: false, joinDate: "2023-05-12", avatarBg: "linear-gradient(135deg,#fa709a 0%,#fee140 100%)" },
]);

const filteredRows = computed(() => {
  const k = query.keyword.trim().toLowerCase();
  return rows.value.filter((r) => {
    const hitK =
      !k ||
      r.name.toLowerCase().includes(k) ||
      r.email.toLowerCase().includes(k);
    const hitRole = !query.role || r.role === query.role;
    return hitK && hitRole;
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

function roleText(role) {
  if (role === "super") return "超级管理员";
  if (role === "admin") return "管理员";
  if (role === "reviewer") return "审核员";
  return "普通成员";
}
function roleTagClass(role) {
  if (role === "super") return "tag-super";
  if (role === "admin") return "tag-admin";
  if (role === "reviewer") return "tag-review";
  return "tag-member";
}

/** dialog */
const dialog = reactive({
  open: false,
  mode: "add",
  form: { id: null, name: "", email: "", role: "member", enabled: true },
});

function openAdd() {
  dialog.mode = "add";
  dialog.form = { id: null, name: "", email: "", role: "member", enabled: true };
  dialog.open = true;
}
function editRow(row) {
  dialog.mode = "edit";
  dialog.form = { ...row };
  dialog.open = true;
}
function saveDialog() {
  if (!dialog.form.name || !dialog.form.email) {
    ElMessage.warning("姓名和邮箱必填");
    return;
  }
  if (dialog.mode === "add") {
    const id = Math.max(...rows.value.map((x) => x.id)) + 1;
    rows.value.unshift({
      ...dialog.form,
      id,
      joinDate: new Date().toISOString().slice(0, 10),
      avatarBg: "linear-gradient(135deg,#667eea 0%,#764ba2 100%)",
    });
    ElMessage.success("已添加（mock）");
  } else {
    const idx = rows.value.findIndex((x) => x.id === dialog.form.id);
    if (idx >= 0) rows.value[idx] = { ...rows.value[idx], ...dialog.form };
    ElMessage.success("已保存（mock）");
  }
  dialog.open = false;
}

async function delRow(row) {
  try {
    await ElMessageBox.confirm(`确定删除成员「${row.name}」吗？`, "提示", { type: "warning" });
    rows.value = rows.value.filter((x) => x.id !== row.id);
    ElMessage.success("已删除（mock）");
  } catch {}
}

/** left */
function editOrg() {
  ElMessage.info("编辑组织信息（mock）");
}
</script>

<style scoped>
.members-wrap {
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

.btn-add {
  border-radius: 10px;
  height: 36px;
  font-weight: 800;
}

.layout {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 16px;
  align-items: start;
}

/* cards */
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

.panel-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e5e7eb;
}

.filters {
  display: flex;
  align-items: center;
  gap: 10px;
}
.search {
  width: 260px;
}
.role {
  width: 120px;
}

/* left org */
.org-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px 16px;
  gap: 8px;
}

.org-logo {
  width: 110px;
  height: 110px;
  border-radius: 12px;
  background: linear-gradient(135deg,#6a7eea 0%,#6b53c6 100%);
}

.org-name {
  font-size: 18px;
  font-weight: 900;
  color: #111827;
  margin-top: 6px;
  text-align: center;
}

.org-id {
  font-size: 13px;
  color: #6b7280;
}

.org-info {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-item {
  background: #f9fafb;
  border-radius: 12px;
  padding: 14px 14px;
}
.info-item .k {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 800;
  margin-bottom: 8px;
}
.info-item .v {
  font-size: 14px;
  color: #111827;
  font-weight: 800;
}

.btn-edit-org {
  width: 100%;
  margin-top: 14px;
  border-radius: 12px;
  height: 40px;
  font-weight: 900;
}

/* table */
.table :deep(.el-table__cell) {
  padding-top: 14px;
  padding-bottom: 14px;
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
}
.uinfo .umail {
  font-size: 12px;
  color: #6b7280;
}

.tag {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 900;
  line-height: 1;
}

.tag-super { background: #ffe8e8; color: #ff4d4f; }
.tag-admin { background: #e8f1ff; color: #1677ff; }
.tag-review { background: #fff3e0; color: #fa8c16; }
.tag-member { background: #f3f4f6; color: #6b7280; }

.tag-ok { background: #e8f5e9; color: #16a34a; }
.tag-ban { background: #ffe8e8; color: #ff4d4f; }

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
.btn-edit {
  background: #e8f1ff;
  color: #1677ff;
}
.btn-del {
  background: #ffe8e8;
  color: #ff4d4f;
}

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
  .layout { grid-template-columns: 1fr; }
  .search { width: 220px; }
}
@media (max-width: 640px) {
  .panel-title-row { flex-direction: column; align-items: flex-start; }
  .filters { width: 100%; }
  .search { width: 100%; }
  .role { width: 100%; }
}
</style>

<template>
  <div class="sys-wrap">
    <div class="topbar">
      <div class="title">系统管理</div>
      <div class="right">
        <!-- 这里右上角头像/铃铛若依Layout一般自带；页面里不强做 -->
      </div>
    </div>

    <div class="grid">
      <!-- Left: 通知管理 -->
      <el-card shadow="hover" class="panel notice">
        <div class="panel-head">
          <div class="panel-title">
            <span class="ico">📣</span>
            通知管理
          </div>
          <el-button type="primary" class="btn-send" @click="openSend">
            ＋ 发送通知
          </el-button>
        </div>

        <div class="notice-list">
          <div
            v-for="item in notices"
            :key="item.id"
            class="notice-item"
            @click="viewNotice(item)"
          >
            <div class="n-left">
              <div class="n-icon" :style="{ background: item.iconBg }">📣</div>
              <div class="n-text">
                <div class="n-title">{{ item.title }}</div>
                <div class="n-time">{{ item.time }}</div>
              </div>
            </div>
            <span :class="['n-badge', item.read ? 'read' : 'unread']">
              {{ item.read ? '已读' : '未读' }}
            </span>
          </div>
        </div>

        <div class="notice-footer">
          <a class="link" href="javascript:;" @click="viewAll">查看全部通知 →</a>
        </div>
      </el-card>

      <!-- Right: 系统设置 -->
      <el-card shadow="hover" class="panel settings">
        <div class="panel-title-row">
          <div class="panel-title">
            <span class="ico">⚙️</span>
            系统设置
          </div>
        </div>

        <div class="section">
          <div class="section-title">基础设置</div>

          <div class="form-row">
            <div class="label">平台名称</div>
            <el-input v-model="form.platformName" class="dark-input" />
          </div>

          <div class="form-row">
            <div class="label">平台Logo</div>
            <div class="logo-row">
              <el-input v-model="form.logoName" class="dark-input" disabled />
              <el-upload
                class="upload"
                :show-file-list="false"
                :before-upload="beforeUpload"
              >
                <el-button class="btn-upload">上传</el-button>
              </el-upload>
            </div>
          </div>

          <div class="form-row">
            <div class="label">客服电话</div>
            <el-input v-model="form.servicePhone" class="dark-input" />
          </div>
        </div>

        <div class="divider" />

        <div class="section">
          <div class="section-title">内容审核设置</div>
          <div class="switch-row">
            <div class="label">自动审核</div>
            <el-switch v-model="form.autoReview" />
          </div>
          <div class="switch-row">
            <div class="label">敏感词过滤</div>
            <el-switch v-model="form.sensitiveFilter" />
          </div>
        </div>

        <div class="divider" />

        <div class="section">
          <div class="section-title">通知设置</div>
          <div class="switch-row">
            <div class="label">邮件通知</div>
            <el-switch v-model="form.emailNotice" />
          </div>
          <div class="switch-row">
            <div class="label">短信通知</div>
            <el-switch v-model="form.smsNotice" />
          </div>
          <div class="switch-row">
            <div class="label">系统消息推送</div>
            <el-switch v-model="form.sysPush" />
          </div>
        </div>

        <div class="divider" />

        <div class="section">
          <div class="section-title">安全设置</div>

          <div class="form-row">
            <div class="label">密码强度要求</div>
            <el-select v-model="form.passwordLevel" class="dark-select" style="width: 160px">
              <el-option label="低" value="low" />
              <el-option label="中等" value="mid" />
              <el-option label="高" value="high" />
            </el-select>
          </div>

          <div class="switch-row">
            <div class="label">登录失败锁定</div>
            <el-switch v-model="form.loginLock" />
          </div>

          <div class="form-row">
            <div class="label">操作日志保留</div>
            <el-select v-model="form.logKeepDays" class="dark-select" style="width: 160px">
              <el-option label="30天" :value="30" />
              <el-option label="60天" :value="60" />
              <el-option label="90天" :value="90" />
              <el-option label="180天" :value="180" />
            </el-select>
          </div>
        </div>

        <div class="actions">
          <el-button class="btn-reset" @click="resetForm">重置</el-button>
          <el-button type="primary" class="btn-save" @click="saveForm">💾 保存设置</el-button>
        </div>
      </el-card>
    </div>

    <!-- 发送通知 -->
    <el-dialog v-model="dlg.open" title="发送通知" width="520px">
      <el-form label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="dlg.form.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="dlg.form.content" type="textarea" :rows="5" placeholder="请输入通知内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg.open=false">取消</el-button>
        <el-button type="primary" @click="sendNotice">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";

const notices = ref([
  {
    id: 1,
    title: "系统维护通知",
    time: "2024-01-20 10:00",
    read: false,
    iconBg: "#fff3e0",
  },
  {
    id: 2,
    title: "新功能上线通知",
    time: "2024-01-19 15:30",
    read: true,
    iconBg: "#e3f2fd",
  },
  {
    id: 3,
    title: "领养申请审核提醒",
    time: "2024-01-18 09:00",
    read: true,
    iconBg: "#e8f5e9",
  },
]);

const defaultForm = {
  platformName: "宠爱有家",
  logoName: "",
  servicePhone: "400-123-4567",

  autoReview: true,
  sensitiveFilter: true,

  emailNotice: true,
  smsNotice: false,
  sysPush: true,

  passwordLevel: "mid",
  loginLock: true,
  logKeepDays: 90,
};

const form = reactive({ ...defaultForm });

function beforeUpload(file) {
  form.logoName = file.name;
  ElMessage.success(`已选择文件：${file.name}（mock）`);
  return false; // 阻止真实上传
}

function resetForm() {
  Object.assign(form, { ...defaultForm });
  ElMessage.info("已重置（mock）");
}

function saveForm() {
  ElMessage.success("保存成功（mock）");
}

function viewNotice(item) {
  item.read = true;
  ElMessage.info(`查看通知：${item.title}（mock）`);
}

function viewAll() {
  ElMessage.info("跳转到通知列表（mock）");
}

/** dialog send */
const dlg = reactive({
  open: false,
  form: { title: "", content: "" },
});

function openSend() {
  dlg.form = { title: "", content: "" };
  dlg.open = true;
}

function sendNotice() {
  if (!dlg.form.title.trim()) return ElMessage.warning("标题必填");
  notices.value.unshift({
    id: Date.now(),
    title: dlg.form.title,
    time: new Date().toISOString().slice(0, 16).replace("T", " "),
    read: false,
    iconBg: "#fff3e0",
  });
  dlg.open = false;
  ElMessage.success("已发送（mock）");
}
</script>

<style scoped>
.sys-wrap {
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

.grid {
  display: grid;
  grid-template-columns: 1fr 1.15fr;
  gap: 16px;
  align-items: start;
}

.panel {
  border-radius: 12px;
  border: none;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e5e7eb;
}

.panel-title-row {
  margin-bottom: 12px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e5e7eb;
}

.panel-title {
  font-size: 15px;
  font-weight: 900;
  color: #111827;
  display: flex;
  align-items: center;
  gap: 10px;
}

.ico { font-size: 16px; }

.btn-send {
  border-radius: 10px;
  font-weight: 900;
  height: 34px;
}

/* notice list */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 4px 0;
}

.notice-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #f9fafb;
  border-radius: 12px;
  padding: 14px 14px;
  cursor: pointer;
}

.n-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.n-icon {
  width: 40px;
  height: 40px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.n-text { min-width: 0; }

.n-title {
  font-size: 14px;
  font-weight: 900;
  color: #111827;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.n-time {
  font-size: 12px;
  color: #9ca3af;
}

.n-badge {
  font-size: 12px;
  font-weight: 900;
  padding: 6px 10px;
  border-radius: 10px;
  flex-shrink: 0;
}

.unread { background: #ffe8e8; color: #ff4d4f; }
.read { background: #e8f5e9; color: #16a34a; }

.notice-footer {
  margin-top: 16px;
  padding-top: 10px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: center;
}

.link {
  color: #1677ff;
  font-weight: 900;
  font-size: 13px;
  text-decoration: none;
}

/* settings */
.section { padding: 8px 2px 6px; }
.section-title {
  font-size: 13px;
  font-weight: 900;
  color: #6b7280;
  margin-bottom: 10px;
}

.divider {
  height: 1px;
  background: #e5e7eb;
  margin: 10px 0;
}

.form-row {
  display: grid;
  grid-template-columns: 110px 1fr;
  align-items: center;
  gap: 14px;
  padding: 8px 0;
}

.switch-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}

.label {
  font-size: 13px;
  font-weight: 900;
  color: #374151;
}

.logo-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-upload {
  border-radius: 10px;
  height: 34px;
  font-weight: 900;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  color: #6b7280;
}

/* 让输入框看起来像截图里的深色块 */
.dark-input :deep(.el-input__wrapper) {
  background: #2f343a;
  box-shadow: none;
  border-radius: 10px;
}
.dark-input :deep(.el-input__inner) {
  color: #fff;
  font-weight: 800;
}
.dark-input :deep(.el-input__inner::placeholder) {
  color: rgba(255,255,255,.55);
}

/* select 同样深色 */
.dark-select :deep(.el-select__wrapper) {
  background: #2f343a;
  box-shadow: none;
  border-radius: 10px;
}
.dark-select :deep(.el-select__selected-item) {
  color: #fff;
  font-weight: 800;
}

.actions {
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-reset {
  border-radius: 10px;
  height: 36px;
  font-weight: 900;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  color: #6b7280;
}

.btn-save {
  border-radius: 10px;
  height: 36px;
  font-weight: 900;
  
}

/* responsive */
@media (max-width: 1200px) {
  .grid { grid-template-columns: 1fr; }
}
</style>

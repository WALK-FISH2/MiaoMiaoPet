<template>
  <div class="create-wrap">
    <!-- top bar -->
    <div class="topbar">
      <div class="left">
        <el-button class="btn-back" @click="goBack">← 返回</el-button>
        <div class="title">新增捐款</div>
      </div>
      <div class="actions">
        <el-button class="btn" @click="resetForm">重置</el-button>
        <el-button type="primary" class="btn" @click="submit">提交捐款</el-button>
      </div>
    </div>

    <!-- 捐款信息 -->
    <el-card shadow="hover" class="panel">
      <div class="panel-title">捐款信息</div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <div class="grid-2">
          <el-form-item label="捐款项目" prop="project">
            <el-select v-model="form.project" placeholder="请选择捐款项目">
              <el-option label="猫粮补给" value="food" />
              <el-option label="医疗救助" value="medical" />
              <el-option label="绝育计划" value="tnr" />
              <el-option label="临时安置" value="temp" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>

          <el-form-item label="捐款金额" prop="amount">
            <el-input
              v-model.number="form.amount"
              placeholder="请输入金额"
              type="number"
            >
              <template #append>元</template>
            </el-input>
          </el-form-item>

          <el-form-item label="支付方式" prop="payType">
            <el-select v-model="form.payType" placeholder="请选择支付方式">
              <el-option label="微信支付" value="wechat" />
              <el-option label="支付宝" value="alipay" />
              <el-option label="线下转账" value="offline" />
            </el-select>
          </el-form-item>

          <el-form-item label="捐款状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option label="成功" value="success" />
              <el-option label="待支付" value="pending" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 捐款人 -->
    <el-card shadow="hover" class="panel">
      <div class="panel-title">捐款人信息</div>

      <el-form :model="form" :rules="rules" ref="formRef2" label-width="100px">
        <div class="grid-2">
          <el-form-item label="捐款人" prop="donor">
            <el-input
              v-model="form.donor"
              placeholder="捐款人姓名 / 昵称"
              :disabled="form.anonymous"
            />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="用于联系 / 留档"
              :disabled="form.anonymous"
            />
          </el-form-item>

          <el-form-item label="是否匿名">
            <el-switch v-model="form.anonymous" />
          </el-form-item>

          <el-form-item label="留言备注">
            <el-input
              v-model="form.note"
              type="textarea"
              :rows="3"
              placeholder="可填写备注信息"
            />
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- 凭证 -->
    <el-card shadow="hover" class="panel">
      <div class="panel-title">支付凭证（线下）</div>

      <el-upload
        class="upload"
        action="#"
        :auto-upload="false"
        :limit="3"
        list-type="picture-card"
      >
        <el-icon><Plus /></el-icon>
      </el-upload>

      <div class="upload-tip">
        仅用于线下转账凭证留存（mock，不实际上传）
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";

const router = useRouter();
const formRef = ref();
const formRef2 = ref();

/** form */
const form = reactive({
  project: "",
  amount: null,
  payType: "",
  status: "success",

  donor: "",
  phone: "",
  anonymous: false,
  note: "",
});

/** rules */
const rules = {
  project: [{ required: true, message: "请选择捐款项目", trigger: "change" }],
  amount: [
    { required: true, message: "请输入捐款金额", trigger: "blur" },
    {
      validator: (_, v, cb) =>
        v > 0 ? cb() : cb(new Error("金额必须大于 0")),
      trigger: "blur",
    },
  ],
  payType: [{ required: true, message: "请选择支付方式", trigger: "change" }],
  status: [{ required: true, message: "请选择状态", trigger: "change" }],
  donor: [
    {
      validator: (_, v, cb) =>
        form.anonymous || v ? cb() : cb(new Error("请输入捐款人")),
      trigger: "blur",
    },
  ],
  phone: [
    {
      validator: (_, v, cb) =>
        form.anonymous || !v || /^1\d{10}$/.test(v)
          ? cb()
          : cb(new Error("手机号格式不正确")),
      trigger: "blur",
    },
  ],
};

/** watch anonymous */
watch(
  () => form.anonymous,
  (v) => {
    if (v) {
      form.donor = "";
      form.phone = "";
    }
  }
);

function goBack() {
  router.back();
}

function resetForm() {
  Object.assign(form, {
    project: "",
    amount: null,
    payType: "",
    status: "success",
    donor: "",
    phone: "",
    anonymous: false,
    note: "",
  });
  ElMessage.info("已重置");
}

function submit() {
  formRef.value.validate((v1) => {
    if (!v1) return;
    formRef2.value.validate((v2) => {
      if (!v2) return;

      // mock submit
      ElMessage.success("新增捐款成功（mock）");
      router.push("/welfare/donation");
    });
  });
}
</script>

<style scoped>
.create-wrap {
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
.panel-title {
  font-size: 15px;
  font-weight: 900;
  margin-bottom: 14px;
}

/* grid */
.grid-2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px 24px;
}

/* upload */
.upload {
  margin-bottom: 8px;
}
.upload-tip {
  font-size: 12px;
  color: #6b7280;
}

/* responsive */
@media (max-width: 992px) {
  .grid-2 {
    grid-template-columns: 1fr;
  }
  .topbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>

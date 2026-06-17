<template>
  <div class="edit-wrap">
    <!-- 顶部条 -->
    <div class="topbar">
      <el-button class="back-btn" @click="goBack">← 返回</el-button>
      <div class="topbar-title">编辑宠物信息 - {{ form.name || "未命名" }}</div>
      <div class="topbar-actions">
        <el-button type="success" @click="onSave">
          <el-icon style="margin-right: 6px;"><FolderChecked /></el-icon>
          保存
        </el-button>
      </div>
    </div>

    <!-- 表单卡片 -->
    <el-card shadow="hover" class="panel">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-row :gutter="16">
          <el-col :xs="24" :md="12">
            <el-form-item label="宠物名称" prop="name">
              <el-input v-model="form.name" class="dark-input" placeholder="请输入宠物名称" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" class="dark-input" placeholder="可选" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="所属地点" prop="locationId">
              <el-select v-model="form.locationId" class="dark-input" placeholder="请选择所属地点" filterable>
                <el-option v-for="item in locationOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" class="dark-input" placeholder="请选择性别">
                <el-option label="公" value="male" />
                <el-option label="母" value="female" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="年龄" prop="ageText">
              <el-input v-model="form.ageText" class="dark-input" placeholder="例如：约2岁" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="品种" prop="breed">
              <el-input v-model="form.breed" class="dark-input" placeholder="例如：橘猫" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="毛色" prop="color">
              <el-input v-model="form.color" class="dark-input" placeholder="例如：橘白相间" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" class="dark-input" placeholder="请选择状态">
                <el-option label="待领养" value="available" />
                <el-option label="领养中" value="adopting" />
                <el-option label="已领养" value="adopted" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="发现时间" prop="foundAt">
              <el-date-picker
                v-model="form.foundAt"
                type="date"
                class="dark-input"
                value-format="YYYY-MM-DD"
                format="YYYY/MM/DD"
                placeholder="选择日期"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :md="12">
            <el-form-item label="发现地点" prop="foundLocation">
              <el-input v-model="form.foundLocation" class="dark-input" placeholder="如：图书馆门口" />
            </el-form-item>
          </el-col>

          <el-col :xs="24">
            <el-form-item label="性格描述" prop="temperament">
              <el-input
                v-model="form.temperament"
                type="textarea"
                :rows="4"
                placeholder="例如：温顺亲人，喜欢被摸头，不怕生人"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24">
            <el-form-item label="常出没地点（每行一个）" prop="places">
              <el-input
                v-model="form.places"
                type="textarea"
                :rows="4"
                placeholder="例如：图书馆\n食堂\n教学楼A座"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 图片卡片 -->
    <el-card shadow="hover" class="panel" style="margin-top: 14px;">
      <template #header>
        <div class="card-header">宠物照片</div>
      </template>

      <div class="photo-grid">
        <!-- 已有照片 -->
        <div v-for="p in photos" :key="p.id" class="photo-item" :style="{ background: p.bg }">
          <div class="photo-actions">
            <div class="circle-btn" title="删除" @click="removePhoto(p.id)">×</div>
          </div>

          <div v-if="p.isCover" class="cover-badge">封面</div>
          <div v-else class="set-cover" @click="setCover(p.id)">设为封面</div>
        </div>

        <!-- 添加照片 -->
        <label class="photo-add">
          <input class="file-input" type="file" accept="image/*" multiple @change="onPickFiles" />
          <div class="add-inner">
            <div class="camera">📷</div>
            <div class="add-text">添加照片</div>
          </div>
        </label>
      </div>

      <!-- <div class="photo-tip">说明：当前为前端预览版；后续对接后端后会改为真实上传。</div> -->
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { FolderChecked } from "@element-plus/icons-vue";
import { getPet, listPetLocations, updatePet, uploadPetImage } from "@/api/welfare/pet";

const router = useRouter();
const route = useRoute();
const formRef = ref();

const petId = Number(route.params.id);

/** 表单数据 */
const form = reactive({
  id: petId,
  name: "",
  nickname: "",
  locationId: undefined,
  gender: "",
  ageText: "",
  breed: "",
  color: "",
  status: "available",
  foundAt: "",
  foundLocation: "",
  temperament: "",
  places: "",
});

const rules = {
  name: [{ required: true, message: "请输入宠物名称", trigger: "blur" }],
  locationId: [{ required: true, message: "请选择所属地点", trigger: "change" }],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  ageText: [{ required: true, message: "请输入年龄", trigger: "blur" }],
  breed: [{ required: true, message: "请输入品种", trigger: "blur" }],
  status: [{ required: true, message: "请选择状态", trigger: "change" }],
  foundAt: [{ required: true, message: "请选择发现时间", trigger: "change" }],
};

const photos = ref([]);
const locationOptions = ref([]);

onMounted(async () => {
  const [locationRes, petRes] = await Promise.all([listPetLocations(), getPet(petId)]);
  locationOptions.value = locationRes.data || [];
  fillForm(petRes.data);
});

function fillForm(data = {}) {
  form.name = data.name || "";
  form.nickname = data.nickname || "";
  form.locationId = data.locationId;
  form.gender = data.gender || "";
  form.ageText = data.age || "";
  form.breed = data.breed || "";
  form.color = data.color || "";
  form.status = data.status || "available";
  form.foundAt = data.foundDate || "";
  form.foundLocation = data.foundLocation || "";
  form.temperament = data.personality || "";
  form.places = parseJsonArray(data.frequentLocations).join("\n");

  const imageUrls = parseJsonArray(data.images);
  const urls = imageUrls.length ? imageUrls : data.mainImage ? [data.mainImage] : [];
  photos.value = urls.map((url, index) => ({
    id: index + 1,
    url,
    isCover: url === data.mainImage || (!data.mainImage && index === 0),
    bg: `url(${url}) center/cover no-repeat`,
  }));
}

function goBack() {
  router.back();
}

function setCover(id) {
  photos.value = photos.value.map((p) => ({ ...p, isCover: p.id === id }));
  ElMessage.success("已设为封面");
}

async function removePhoto(id) {
  try {
    await ElMessageBox.confirm("确定删除这张照片吗？", "删除确认", {
      type: "warning",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
    });
    const wasCover = photos.value.find((p) => p.id === id)?.isCover;
    photos.value = photos.value.filter((p) => p.id !== id);
    if (wasCover && photos.value.length) photos.value[0].isCover = true;
    ElMessage.success("已删除");
  } catch {
    // cancel
  }
}

/** 前端预览：把用户选择的图片转成 blob URL，作为背景图显示 */
function onPickFiles(e) {
  const files = e.target.files;
  if (!files || files.length === 0) return;

  const startId = Math.max(0, ...photos.value.map((p) => p.id)) + 1;

  const newOnes = Array.from(files).map((f, idx) => {
    const url = URL.createObjectURL(f);
    return {
      id: startId + idx,
      file: f,
      url,
      isCover: false,
      bg: `url(${url}) center/cover no-repeat`,
    };
  });

  photos.value.push(...newOnes);
  ElMessage.success(`已添加 ${newOnes.length} 张照片（预览）`);

  // 清空 input，允许重复选择同一文件触发 change
  e.target.value = "";
}

async function onSave() {
  try {
    await formRef.value?.validate();
    const imagePhotos = await uploadNewPhotos();
    await updatePet(buildPayload(imagePhotos));
    ElMessage.success("保存成功");
    router.push("/welfare/pet");
  } catch {
    // 校验失败
  }
}

async function uploadNewPhotos() {
  const result = [];
  for (const photo of photos.value) {
    if (photo.file) {
      const res = await uploadPetImage(photo.file);
      result.push({ ...photo, url: res.url });
    } else {
      result.push(photo);
    }
  }
  return result;
}

function buildPayload(imagePhotos) {
  const locations = form.places
    .split(/\r?\n/)
    .map((item) => item.trim())
    .filter(Boolean);
  const cover = imagePhotos.find((item) => item.isCover) || imagePhotos[0];
  return {
    id: form.id,
    locationId: form.locationId,
    name: form.name,
    nickname: form.nickname,
    gender: form.gender,
    age: form.ageText,
    breed: form.breed,
    color: form.color,
    personality: form.temperament,
    status: form.status,
    foundDate: form.foundAt,
    foundLocation: form.foundLocation || locations[0] || "",
    frequentLocations: JSON.stringify(locations),
    mainImage: cover?.url || "",
    images: JSON.stringify(imagePhotos.map((item) => item.url)),
    photoCount: imagePhotos.length,
  };
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
</script>

<style scoped>
.edit-wrap {
  padding: 14px 18px 24px;
  background: #f3f5f7;
  min-height: calc(100vh - 84px);
}

/* 顶部条 */
.topbar {
  display: grid;
  grid-template-columns: 90px 1fr 120px;
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
  font-weight: 700;
  color: #111827;
}

.topbar-actions {
  display: flex;
  justify-content: flex-end;
}

/* 卡片 */
.panel {
  border-radius: 12px;
  border: none;
}

/* 深色输入（贴图风格） */
.dark-input :deep(.el-input__wrapper),
.dark-input :deep(.el-select__wrapper),
.dark-input :deep(.el-date-editor) {
  background: #2f343a;
  border-radius: 8px;
  box-shadow: none;
}

.dark-input :deep(.el-input__inner),
.dark-input :deep(.el-select__selected-item),
.dark-input :deep(.el-date-editor .el-input__inner) {
  color: #fff;
}

.dark-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.65);
}

/* 图片区 */
.card-header {
  font-weight: 700;
  color: #111827;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  padding: 10px 4px 4px;
}

.photo-item {
  height: 138px;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.photo-actions {
  position: absolute;
  top: 8px;
  left: 8px;
}

.circle-btn {
  width: 22px;
  height: 22px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  display: grid;
  place-items: center;
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
}

.cover-badge {
  position: absolute;
  left: 10px;
  bottom: 10px;
  padding: 2px 8px;
  font-size: 12px;
  color: #fff;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.45);
}

.set-cover {
  position: absolute;
  right: 10px;
  bottom: 10px;
  padding: 2px 8px;
  font-size: 12px;
  color: #fff;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.45);
  cursor: pointer;
}

/* 添加照片 */
.photo-add {
  height: 138px;
  border-radius: 12px;
  border: 2px dashed #d1d5db;
  display: grid;
  place-items: center;
  background: #fafafa;
  cursor: pointer;
}
.file-input {
  display: none;
}
.add-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #6b7280;
}
.camera {
  font-size: 22px;
}
.add-text {
  font-size: 13px;
}

.photo-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #9ca3af;
}

/* 响应式 */
@media (max-width: 992px) {
  .topbar {
    grid-template-columns: 90px 1fr;
    grid-template-areas:
      "back title"
      "actions actions";
  }
  .topbar-actions {
    justify-content: flex-start;
  }
  .photo-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>

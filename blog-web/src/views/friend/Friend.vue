<template>
  <CommonLayout :title="t('friend')" :bgImg="friendLetterMiddle" />
  <div class="bg">
    <div class="page-container">
      <div class="form-wrap" :style="formWrapStyle" @click="toggleEnvelope">
        <!-- 信封头部 -->
        <ImageWithFallback class="before-img" :src="friendLetterTop" />

        <!-- 信封中间 -->
        <div class="envelope">
          <div class="form-main">
            <!-- 信封中间的图片 -->
            <ImageWithFallback :src="friendLetterMiddle" class="middle-img" />

            <div class="form-content">
              <h3 style="text-align: center">有朋自远方来</h3>
              <el-form class="form-friend" :model="friend" :rules="rules" ref="formRef" autocomplete="off">
                <el-form-item class="form-input text" label="名称：" prop="title">
                  <el-input class="text" v-model="friend.title" clearable></el-input>
                </el-form-item>
                <el-form-item class="form-input text" label="简介：" prop="introduction">
                  <el-input class="text" v-model="friend.introduction" clearable></el-input>
                </el-form-item>
                <el-form-item class="form-input text" label="封面：" prop="cover">
                  <el-input class="text" v-model="friend.cover" clearable></el-input>
                </el-form-item>
                <el-form-item class="form-input text" label="网址：" prop="url">
                  <el-input class="text" v-model="friend.url" clearable></el-input>
                </el-form-item>
                <el-form-item>
                  <proButton class="myCenter" :info="'提交'" @click="submitFriend()" before="#9cd0ed" after="#ed6ea0">
                  </proButton>

                </el-form-item>
                <!-- <el-form-item>
                  <el-button class="myCenter" type="primary" @click="submitFriend">提交</el-button>
                </el-form-item> -->
              </el-form>

              <img :src="friendLetterBiLi" style="width: 100%;margin: 25px auto 0" />
              <p style="font-size: 12px;text-align: center;color: #999">欢迎交换友链</p>
            </div>
          </div>
        </div>

        <!-- 信封底部 -->
        <ImageWithFallback class="after-img" :src="friendLetterBottom" />
      </div>
      <!-- 信息展示部分 -->
      <FriendInfoSection :title="info" :items="siteInfo" :is-copy="true" />

      <FriendInfoSection :title="prop" :items="applicationInfo" />

      <!-- 友链分类展示 -->
      <FriendCard :friendList="friendList" :title="t('friendlyLink')" @click-resource-path="openLink" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import FriendCard from './FriendCard.vue'
import FriendInfoSection from './FriendInfoSection.vue'
import proButton from '@/components/Button/proButton.vue'
import CommonLayout from '../Layout/CommonLayout.vue'
import { useResize } from '@/utils/common'
import ImageWithFallback from '@/components/Image/ImageWithFallback.vue'
import friendLetterBiLi from '@/assets/images/friendLetterBiLi.png'
import friendLetterBottom from '@/assets/images/friendLetterBottom.png'
import friendLetterMiddle from '@/assets/images/friendLetterMiddle.jpg'
import friendLetterTop from '@/assets/images/friendLetterTop.png'
import type { Friend } from '@/types/friend'
import { addFriendService, addUserFriendService, getFriendService } from '@/api/friend'
import { useI18nStore, useUserStore } from "@/stores";

const userState = useUserStore()
import { t } from '@/utils/i18n'
const prop = ref("🌸" + t('proposal'))
const info = ref("🌸" + t('webInfo'))
// 响应式数据
const friend = ref({
  title: '',
  introduction: '',
  cover: '',
  url: '',

})
const formRef = ref<FormInstance>()
const friendList = reactive<Friend[]>([])
const showEnvelope = ref(false)
const rules = {
  title: [
    { required: true, message: '名称是什么呢', trigger: 'blur' }
  ],
  introduction: [
    { required: true, message: '介绍一下网站吧', trigger: 'blur' }
  ],
  cover: [
    { required: true, message: '要有个形象才行吖', trigger: 'blur' },
    // 新增 HTTPS 校验
    {
      validator: (rule: any, value: string, callback: any) => {
        if (!/^https:\/\//i.test(value)) { // 忽略大小写校验
          callback(new Error('封面链接必须以 https:// 开头'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  url: [
    { required: true, message: '没有网址可访问不到捏', trigger: 'blur' },
    { type: 'url', message: '请输入合法URL', trigger: 'blur' },
    // 新增 HTTPS 校验
    {
      validator: (rule: any, value: string, callback: any) => {
        if (!/^https:\/\//i.test(value)) { // 忽略大小写校验
          callback(new Error('网址链接必须以 https:// 开头'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};
// 计算属性
const isMobile = useResize()
const formWrapStyle = computed(() => ({
  height: showEnvelope.value ? (isMobile ? '1000px' : '1080px') : '447px',
  top: showEnvelope.value ? '-100px' : '0'
}))

// 常量数据
const siteInfo = ['网站名称:  叶玲子的小栈',
  '网址:  https://www.yeling.top',
  '头像:  https://www.yeling.top/image/logo.jpg',
  '描述:  记录学习、生活、兴趣的次元小栈',
  '网站封面:  https://www.yeling.top/image/cover.jpg'
]

const applicationInfo = [
  '点击上方信封✨✨✨',
  '不会添加带有广告营销和没有实质性内容的友链🚫🚫🚫',
  '申请之前请将本网站添加为您的友链哦🎟️🎟️🎟️'
]

// 方法
const toggleEnvelope = () => {
  if (!showEnvelope.value) {
    showEnvelope.value = true
  }
}

const submitFriend = async () => {
  try {
    // 确保form.value已正确引用表单实例
    await formRef.value?.validate();

    // 验证通过，执行提交逻辑
    if (userState.getIsLogin()) {
      await addUserFriendService({ ...friend.value });
    } else {
      await addFriendService({ ...friend.value });
    }
    ElMessage.success('提交成功，待管理员审核！');
    resetFriend();
    showEnvelope.value = false;

  } catch (error) {
    ElMessage.error('请完善表单内容后再提交~');
  }
};

const resetFriend = () => {
  friend.value.cover = ''
  friend.value.url = ''
  friend.value.title = ''
  friend.value.introduction = ''
}

const openLink = (url: string) => {
  window.open(url, '_blank')
}

const fetchFriends = async () => {

  if (friendList.length) {
    friendList.splice(0, friendList.length)
  }

  const res = await getFriendService()
  for (const friend of res.data.data) {
    friendList.push(friend)
  }


}

const clearSelection = () => {
  // 标准 API
  window.getSelection()?.removeAllRanges()
  // 兼容旧版 IE
  // document.selection?.empty()
}

const onMouseDown = (e: MouseEvent) => {
  const block = document.querySelector('.blockquote')

  if (block && !block.contains(e.target as Node)) {
    clearSelection()
  }
}


// 生命周期
onMounted(() => {
  fetchFriends()
  prop.value = "🌸" + t('proposal')
  info.value = "🌸" + t('webInfo')
  document.addEventListener('mousedown', onMouseDown)
})
onUnmounted(() => document.removeEventListener('mousedown', onMouseDown))
</script>

<style lang="scss" scoped>
.form-wrap {
  margin: 0 auto;
  overflow: hidden;
  width: 530px;
  height: 447px;
  position: relative;
  top: 0;
  transition: all 1s ease-in-out .3s;
  z-index: 0;
}


.before-img {
  position: absolute;
  bottom: 126px;
  left: 0;
  background-repeat: no-repeat;
  width: 530px;
  height: 317px;
  z-index: -100;
}

.after-img {
  position: absolute;
  bottom: -2px;
  left: 0;
  background-repeat: no-repeat;
  width: 530px;
  height: 259px;
  z-index: 100;
}

.friend-wrap {
  color: var(--grey-7);
}

.envelope {
  position: relative;
  margin: 0 auto;
  transition: all 1s ease-in-out .3s;
  padding: 200px 20px 20px;
}

.form-main {
  background: var(--grey-1);
  margin: 0 auto;
  border-radius: 10px;
  overflow: hidden;
}


.user-title {
  text-align: right;
  user-select: none;
}

.user-content {
  text-align: left;
}

.user-title div {
  height: 55px;
  line-height: 55px;
  text-align: center;
}

.user-content>div {
  height: 55px;
  display: flex;
  align-items: center;
}

.user-content :deep(.el-input__inner) {
  border: none;
  height: 35px;
  background: var(--grey-4);
}

.form-friend {
  margin-top: 12px;
  background-color: #eee;
  border: #ddd 1px solid;
  padding: 0;
}



.form-input {
  width: 300px;
  margin: 16px auto;
}

:deep(.el-input__wrapper) {
  cursor: url('/src/assets/cursors/beam.cur'), not-allowed !important;
}

:deep(.el-input__inner) {
  cursor: url('/src/assets/cursors/beam.cur'), not-allowed !important;
}

.myCenter {
  margin: 0px auto;
}

@media screen and (max-width: 700px) {
  .form-wrap {
    width: auto;
  }

  .before-img {
    width: auto;
  }

  .after-img {
    width: auto;
  }
}

@media screen and (max-width: 500px) {
  .friend-main {
    padding: 40px 15px;
  }
}
</style>

<template>
  <CommonLayout :title="i18n.talk" :bgImg="bgImg" />
  <div class="bg">
    <div class="page-container">
      <div class="talk-item">
        <div class="talk-meta">
          <el-avatar class="user-avatar" :size="50" :src="talk.userAvatar" fit="cover" />
        </div>
        <div class="talk-content-wrap">
          <div class="talk-info">
            <span class="talk-user-name">{{ talk.nickname }}
              <component :is="badgeIcon" style="width: 1.2rem; height: 1.2rem; margin-left: 12px;" />
            </span>
            <span class="talk-time">{{ getDateDiff(talk.createTime) }}</span>
          </div>
          <div class="talk-content" v-html="talk.content"></div>
          <div class="talk-image" v-if="talk.imageUrl">
            <ImageList v-if="talk.imageUrl.length > 1" :image-layout="8" :image-list="talk.imageUrl"></ImageList>
            <ImageList v-else :image-layout="14" :image-list="talk.imageUrl"></ImageList>
          </div>
          <div class="info" style="margin-top: 0.5rem;">
            <span class="talk-like info" @click="like">
              <SvgIcon name="icon-dianzan_kuai" class="icon-avtive" :class="{ active: isLike }"
                style="margin-right: 5px" />
              {{ talk.likeCount }}
            </span>
            <span class="talk-comment info">
              <SvgIcon name="icon-xiaoxi" style="margin-right: 5px;" />
              {{ talk.commentCount }}
            </span>
          </div>
        </div>
      </div>
      <Comment v-if="talk.id !== 0" :comment-type="2" :id="talk.id" :author-id="talk.userId"></Comment>
    </div>
  </div>
</template>

<script setup lang="ts">
import bgImg from '@/assets/images/bg-article.jpg'
import type { Talk } from "@/types/talk";
import { getDateDiff, numberToBoolean } from "@/utils/common"
import { computed, onMounted, defineAsyncComponent, ref } from "vue";
import { useUserStore, useI18nStore } from "@/stores";
import Comment from '@/components/Comment/Comment.vue';
import CommonLayout from '../Layout/CommonLayout.vue';
import ImageList from '@/components/Image/ImageList.vue';
import { addTalkLikeService, delTalkLikeService, getTalkByIdService, getTalkLikeService } from '@/api/talk';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter()
const i18n = useI18nStore().currentConfig
const badgeIcon = defineAsyncComponent(() =>
  import('@/assets/icons/badge.svg')
);
const props = defineProps(['id'])
const userState = useUserStore()
const isLike = ref(false)
const talk = ref<Talk>({
  id: 0,
  nickname: '',
  userId: 0,
  userAvatar: '',
  title: '',
  content: '',
  imageUrl: [],
  isTop: 0,
  likeCount: 0,
  commentCount: 0,
  createTime: '',
})

const isRequesting = ref(false)

const like = async () => {
  // 防抖拦截
  if (isRequesting.value) return

  try {
    isRequesting.value = true

    if (!userState.getIsLogin()) {
      ElMessage.warning("请先登录")
      return
    }

    // 执行点赞/取消操作
    if (isLike.value) {
      await delTalkLikeService(talk.value.id)
      talk.value.likeCount -= 1
    } else {
      await addTalkLikeService(talk.value.id)
      talk.value.likeCount += 1
    }

    // 状态切换
    isLike.value = !isLike.value
  } finally {
    setTimeout(() => {
      isRequesting.value = false
    }, 800)
  }
}

const getTalkById = async () => {
  if (props.id === 0) {
    return
  }
  try {
    const res = await getTalkByIdService(props.id)

    const data = res.data.data
    const imageUrls = data.imageUrl ? data.imageUrl.filter((url: string) =>
      url &&
      url.trim() !== '' &&
      url !== 'null' &&
      url !== 'undefined'
    ) : []

    talk.value = {
      ...data,
      imageUrl: imageUrls
    }
  } catch (error) {
    // 如果请求失败，跳转到404页面
    console.error('请求文章失败:', error);
    router.push('/404');
  }


  getIsLike()

}

const getIsLike = async () => {
  if (userState.getIsLogin()) {
    const res = await getTalkLikeService(talk.value.id)
    isLike.value = numberToBoolean(res.data.data)
  }
}

onMounted(() => {
  getTalkById()
})
</script>

<style lang="scss" scoped>
@use "@/assets/styles/mixin.scss" as mi;

.talk-item {
  display: flex;
}

.talk-meta {
  @include mi.flex;
  width: 50px;
  height: 50px;
}

.talk-user-name {
  display: flex;
  align-items: center;
  font-size: 1.25rem;
  font-weight: 600;
  margin-right: 0.3125rem;
  color: #ef89c6;
}

.talk-title {
  margin-top: 8px;
  font-size: 20px;
  font-weight: bold;
}


.talk-content {
  margin-top: 8px;
  font-size: 1rem;
  line-height: 1.5rem;
  word-wrap: break-word;
  word-break: break-all;
  white-space: pre-line;
}

.talk-image {

  margin-top: 0.3125rem;

  .image {
    border-radius: 0.25rem;

  }
}



.talk-time,
.talk-like,
.talk-comment {
  font-size: 1rem;
  margin-right: 1.25rem;
  color: #9499a0;
}


.talk-content-wrap {
  flex: auto;
  margin-left: 0.5rem;
}

.talk-info {
  display: flex;
  flex-direction: column;
}

.info {
  display: flex;
  align-items: center;
}

@keyframes like-animation {
  0% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.4);
    /* 放大到140% */
  }

  100% {
    transform: scale(1.1);
    /* 最终保持110%大小 */
  }
}

.active {
  color: var(--color-red);
  animation: like-animation 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94) both;
  display: inline-block;
}


.icon-avtive {
  transition: transform 0.3s ease-out;
}
</style>

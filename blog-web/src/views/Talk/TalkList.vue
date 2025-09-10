<template>
  <CommonLayout :title="i18n.talk" :bgImg="bgImg" />
  <div class="bg">
    <div class="page-container" v-if="talkList.length > 0">
      <div class="talk-item" v-slide-in v-for="talk in talkList" :key="talk.id">
        <div class="talk-meta">
          <!-- 用户头像 -->
          <el-avatar :src="talk.userAvatar" fit="cover" />
          <div class="talk-info">
            <span class="talk-user-name">{{ talk.nickname }}
              <!-- <svg-icon icon-class="badge"
                style="margin-left: 0.4rem;"></svg-icon> -->
            </span>
            <div class="talk-info-bottom">
              <div v-if="talk.isTop === 1" style="color:#ee6b8b;margin-right: 0.5rem;">
                <SvgIcon name="icon-paihang" size="14" class="talk-top" />置顶
              </div>
              <span class="talk-time">{{ formatDateWithRelativeTime(talk.createTime) }}</span>
            </div>
          </div>
        </div>
        <!-- 说说内容 -->
        <router-link :to="{ name: 'talk', params: { id: talk.id } }" class="talk-title" v-html="talk.title">
        </router-link>
        <router-link v-pio="{ text: `这条说说`, type: 'look' }" :to="{ name: 'talk', params: { id: talk.id } }"
          class="talk-content" v-html="talk.content">
        </router-link>
        <!-- 说说图片 -->
        <div class="talk-image">
          <ImageList v-if="talk.imageUrl.length > 1" :image-layout="8" :image-list="talk.imageUrl"></ImageList>
          <ImageList v-else :image-layout="14" :image-list="talk.imageUrl"></ImageList>
        </div>
        <!-- 说说信息 -->
        <!-- <router-link :to="{ name: 'talk', params: { id: talk.id } }" class="info" style="margin-top: 0.5rem;"> -->
        <!-- 点赞量 -->
        <!-- <span class="talk-like info">
            <font-awesome-icon :icon="['far', 'thumbs-up']" style="margin-right: 5px;" />
            {{ talk.likeCount }}
          </span> -->
        <!-- 评论量 -->
        <!-- <span class="talk-comment info">
            <font-awesome-icon :icon="['fas', 'comment-dots']" style="margin-right: 5px;" />
            {{ talk.commentCount }}
          </span> -->
        <!-- </router-link> -->
      </div>
      <div class="loading-warp">
        <proButton v-if="loadingTalk" v-loading.fullscreen.lock="loading" :info="i18n.loadMore + '...'" width="100px"
          before="#ed6ea0" after="#9cd0ed" @click="nextPage">
        </proButton>
        <el-card v-else style="width: 100%;">
          <div style="text-align: center;">{{ i18n.loadEnd }}</div>
        </el-card>
      </div>
    </div>
    <div v-else>
      <el-empty description="说说列表为空"></el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Talk } from "@/types/talk";
import { formatDateWithRelativeTime } from "@/utils/common";
import proButton from "@/components/Button/proButton.vue";
import { reactive, onMounted, ref } from "vue";
import CommonLayout from "../Layout/CommonLayout.vue";
import ImageList from "@/components/Image/ImageList.vue";
import bgImg from '@/assets/images/talks-bg.jpg'
import { getTalkListByPageService } from "@/api/talk";
import { useI18nStore } from "@/stores";

const i18n = useI18nStore().currentConfig
const loading = ref(false)
const loadingTalk = ref(true)
const data = reactive({
  total: 100,
  page: 1,
  pageSize: 5,
});
const talkList = ref<Talk[]>([])

const getList = async () => {
  loading.value = true;
  try {
    const res = await getTalkListByPageService(data.page, data.pageSize);
    data.total = res.data.data.total;

    // 处理新数据并过滤无效URL
    const newTalks = res.data.data.data.map((talk: { imageUrl: any[] }) => ({
      ...talk,
      imageUrl: talk.imageUrl.filter(url =>
        url &&
        url.trim() !== '' &&
        url !== 'null' &&
        url !== 'undefined'
      )
    }));

    // 追加新数据到现有数组（而不是替换）
    talkList.value = [...talkList.value, ...newTalks];

    // 检查是否已加载全部数据
    if (data.page * data.pageSize >= data.total) {
      loadingTalk.value = false;
    }
  } catch (error) {
    console.error('获取数据失败:', error);
  } finally {
    loading.value = false;
  }
};
const nextPage = () => {
  data.page += 1
  getList()
}

onMounted(() => {
  if (talkList.value.length) {
    talkList.value.splice(0, talkList.value.length)
  }
  getList()
})
</script>

<style lang="scss" scoped>
.talk-item {
  display: flex;
  flex-direction: column;
  padding: 1rem 1.25rem;
  border-radius: 0.5rem;
  box-shadow: 0 0.625rem 1.875rem -0.9375rem var(--box-bg-shadow);
  animation-duration: 0.5s;
  transition: all 0.2s ease-in-out 0s;

  &:hover {
    box-shadow: 0 0 2rem var(--box-bg-shadow);
  }

  &:not(:first-child) {
    margin-top: 1.25rem;
  }
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

.talk-info-bottom {
  display: flex;
  justify-content: center;
}

.talk-time,
.talk-like,
.talk-comment {
  font-size: 1rem;
  margin-right: 1.25rem;
  color: #9499a0;
}

.talk-top {
  transform: translateY(-1px)
}

.talk-meta {
  display: flex;
  align-items: center;
  width: 100%;
}

.talk-info {
  display: flex;
  flex-direction: column;
  margin-left: 0.5rem;
}

.user-avatar {
  width: 2.8rem;
  height: 2.8rem;
  border-radius: 10px;
}

.info {
  display: flex;
  align-items: center;
}
</style>

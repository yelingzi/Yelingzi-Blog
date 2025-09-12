<template>
  <div class="talk-swiper" v-if="talkList.length > 0">
    <SvgIcon name="icon-xiaoxi" size="16" class="talk-icon"></SvgIcon>
    <span class="talk-text">{{ t('talk') }}</span>
    <el-carousel class="swiper-container" arrow="never" direction="vertical">
      <el-carousel-item v-for="(talk, index) in talkList" :key="index" @click="toTalk(talk.id)">
        <div class="slide-content pointer">{{ talk.content }}</div>
      </el-carousel-item>
    </el-carousel>
    <SvgIcon name="icon-arrow-right" class="arrow" />
    <SvgIcon name="icon-arrow-right" class="arrow" />
    <SvgIcon name="icon-arrow-right" class="arrow" />
  </div>
</template>

<script setup lang="ts">
import { getTopTalkListService } from '@/api/talk';
import type { SimpleTalk } from '@/types/talk';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useI18nStore } from '@/stores';
import { t } from '@/utils/i18n'
const router = useRouter()
const talkList = ref<SimpleTalk[]>([]);

const getTopTalkList = async () => {
  const res = await getTopTalkListService()
  talkList.value = res.data.data
}

const toTalk = (id: number) => {
  router.push({ name: 'talk', params: { id: id } })
}

onMounted(() => {

  getTopTalkList()
});
</script>

<style lang="scss" scoped>
@use '/src/assets/styles/mixin.scss' as m;

.talk-swiper {
  @include m.flex;
  margin: 0 0.5rem;
  padding: 0.6rem 1rem;
  font-size: 1rem;
  border-radius: 0.5rem;
  box-shadow: 0 0.625rem 1.875rem -0.9375rem var(--box-bg-shadow);
  transition: all 0.2s ease-in-out 0s;


  &:hover {
    box-shadow: 0 0 2rem var(--box-bg-shadow);
  }
}

.talk-text {
  margin-left: 5px;
  white-space: nowrap;
}

.swiper-container {
  width: 100%;
  height: 1.6rem;
  border-radius: 0.75rem;

  &:hover {
    color: var(--comment-btn);
  }
}

.slide-content {
  width: 100%;
  height: 100%;
  text-align: center;
}

.talk-icon {
  transform: translateY(1px)
}

:deep(.el-carousel__indicator--vertical) {
  visibility: hidden;
}

.arrow {
  animation: 1.5s passing infinite;
}

@keyframes passing {
  0% {
    transform: translateX(-50%);
    opacity: 0;
  }

  50% {
    transform: translateX(0);
    opacity: 1;
  }

  100% {
    transform: translateX(50%);
    opacity: 0;
  }
}
</style>

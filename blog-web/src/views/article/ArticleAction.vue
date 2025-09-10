<template>
  <el-card class="section">
    <div class="floating-action-bar">

      <el-tooltip class="item" effect="dark" content="点赞" placement="top-start">
        <div class="action-item" @click="handleLike">
          <el-badge :value="article.likeCount || 0" class="item">
            <div class="action-button">
              <SvgIcon name="icon-dianzan_kuai" size="18" class="icon-avtive" :class="{ active: article.isLike }" />
            </div>
          </el-badge>
        </div>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="收藏" placement="top-start">
        <div class="action-item" @click="handleFavorite">
          <el-badge :value="article.starCount || 0" class="item">
            <div class="action-button">
              <SvgIcon name="icon-star" size="18" class="icon-avtive" :class="{ active: article.isStar }" />
            </div>
          </el-badge>
        </div>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="沉浸式浏览" placement="top-start">
        <div class="action-item" @click="handleToggleSidebar">
          <div class="action-button">
            <SvgIcon name="icon-kuoda" size="18" />
          </div>
        </div>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="评论" placement="top-start">
        <div class="action-item" @click="handleScrollToComments">
          <el-badge :value="article.commentCount || 0" class="item">
            <div class="action-button">
              <SvgIcon name="icon-xiaoxi" size="18" />
            </div>
          </el-badge>
        </div>
      </el-tooltip>
    </div>

  </el-card>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  position: {
    type: Number,
    default: 0
  },
  article: {
    type: Object,
    required: true
  }
});

const emits = defineEmits(['like', 'favorite', 'toggle-sidebar', 'scroll-comments']);

const handleLike = () => {
  emits('like');
};

const handleFavorite = () => {
  emits('favorite');
};

const handleToggleSidebar = () => {
  emits('toggle-sidebar');
};

const handleScrollToComments = () => {
  emits('scroll-comments');
};
</script>

<style lang="scss" scoped>
.section {
  margin-top: 20px;
  background: var(--grey-1);
  border-radius: 10px;
}

.floating-action-bar {
  display: flex;
  gap: 10px;
}

.action-item {
  margin-right: 15px;
}

.action-button {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;

  &:hover {
    color: var(--color-pink);
  }
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

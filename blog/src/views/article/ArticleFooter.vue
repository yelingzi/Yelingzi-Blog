<template>
  <!-- 版权声明提示 -->
  <div class="copyright-notice">
    <div class="notice-content">
      <SvgIcon name="icon-tishi1" style="color: #74C0FC;" />
      <div class="notice-text">
        <p v-if="article.isOriginal">
          本文为原创文章，遵循
          <a href="https://creativecommons.org/licenses/by-nc-sa/4.0/" target="_blank" rel="noopener noreferrer">
            CC BY-NC-SA 4.0
          </a>
          版权协议，转载请注明出处。
        </p>
        <p v-else>
          本文转载自 {{ article.originalUrl || '未知来源' }}，版权归原作者所有。如有侵权，请联系删除。
        </p>
      </div>
    </div>
  </div>

  <!-- 标签部分 -->
  <!-- <div class="tags-section">
    <router-link v-for="tag in article.tagVOList" :key="tag.id" :to="`/tags?tagId=${tag.id}&tagName=${tag.tagName}`">
      <el-tag size="large" class="tag-item" :type="tagColor.at(tag.id % tagColor.length)">
        <font-awesome-icon icon="fas fa-tags" />
        {{ tag.tagName }}
      </el-tag>
    </router-link>
  </div> -->

  <!-- 操作按钮部分 -->
  <div class="article-actions">
    <button class="action-btn like pointer" :class="{ active: article.isLike }" @click="handleLikeClick">
      <SvgIcon name="icon-shoucang" />
      <span>{{ article.likeNum }}</span>
    </button>
    <div class="share-dropdown" v-click-outside="closeShareMenu">
      <button class="action-btn share pointer" @click="toggleShareMenu">
        <SvgIcon name="icon-fenxiang" />
        分享
      </button>
      <div class="share-menu" v-show="showShare">
        <button class="share-item" @click="copyLink">
          <component :is="Icon" style="width: 16px;height: 16px;" />
          复制链接
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, ref, defineAsyncComponent } from 'vue';

const Icon = defineAsyncComponent(() =>
  import('@/assets/icons/lianjie.svg')
);

const props = defineProps({
  article: {
    type: Object,
    required: true
  },
  showShare: {
    type: Boolean,
    default: false
  }
});
const tagColor = ref(['primary', 'success', 'info', 'warning', 'danger'])
const emits = defineEmits(['share', 'copy', 'like']);

// 控制分享菜单显示状态
const showShareMenu = ref(props.showShare);

// 点赞处理
const handleLikeClick = () => {
  console.log("子组件点赞点击");
  emits('like'); // 明确发出事件
};

// 切换分享菜单显示状态
const toggleShareMenu = () => {
  showShareMenu.value = !showShareMenu.value;
  emits('share', showShareMenu.value);
};

// 复制链接
const copyLink = () => {
  navigator.clipboard.writeText(window.location.href).then(() => {
    emits('copy');
  }).catch((error) => {
    console.error('复制链接失败:', error);
  });
};

// 关闭分享菜单
const closeShareMenu = () => {
  showShareMenu.value = false;
  emits('share', false);
};
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as va;

.copyright-notice {
  border-radius: va.$border-radius-lg;
  background: rgba(va.$primary, 0.05);
  border: 1px solid rgba(va.$primary, 0.1);
  transition: all 0.3s ease;
  margin: 0 30px;

  &:hover {
    background: rgba(va.$primary, 0.08);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(va.$primary, 0.1);
  }

  .notice-content {
    padding: va.$spacing-md;
    display: flex;
    align-items: center;
    gap: va.$spacing-md;


    .notice-text {
      flex: 1;
      color: var(--text-secondary);
      font-size: 0.95em;
      line-height: 1.6;

      p {
        margin: 0;
      }

      a {
        color: va.$primary;
        text-decoration: none;
        border-bottom: 1px dashed va.$primary;
        transition: all 0.2s ease;

        &:hover {
          border-bottom-style: solid;
        }
      }
    }
  }
}

.tags-section {
  display: flex;
  align-items: center;
  gap: va.$spacing-md;
  margin-bottom: va.$spacing-xl;
  padding: 16px 32px;



  .tag-item {
    padding: va.$spacing-xs va.$spacing-md;
    font-size: 0.9em;
    text-decoration: none;
    transition: all 0.3s ease;

    &:hover {
      background: va.$primary;
      color: white;
      transform: translateY(-2px);
    }
  }
}

.article-actions {
  display: flex;
  justify-content: center;
  gap: va.$spacing-lg;
  margin-bottom: va.$spacing-xl;
  margin-top: 20px;

  .action-btn {
    display: inline-flex;
    align-items: center;
    gap: va.$spacing-sm;
    padding: va.$spacing-sm va.$spacing-xl;
    border: none;
    border-radius: 20px;
    font-size: 1em;
    transition: all 0.3s ease;

    &.like {
      background: var(--hover-bg);
      color: var(--text-secondary);

      &.active {
        background: va.$primary;
        color: white;
      }

      &:hover {
        transform: scale(1.05);
      }
    }

    &.share {
      background: va.$primary;
      color: white;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(va.$primary, 0.2);
      }
    }
  }

  :deep(.share-dropdown) {
    position: relative;

    .share-menu {
      position: absolute;
      bottom: calc(100% + 8px);
      right: 0;
      background: var(--card-bg);
      border-radius: va.$border-radius-lg;
      box-shadow: va.$shadow-lg;
      padding: va.$spacing-xs;
      min-width: 180px;
      z-index: 100;
      border: 1px solid var(--border-color);
      transform-origin: bottom right;
      animation: shareMenuIn 0.2s ease;

      &::before {
        content: '';
        position: absolute;
        bottom: -5px;
        right: 20px;
        width: 10px;
        height: 10px;
        background: var(--card-bg);
        border-left: 1px solid var(--border-color);
        border-top: 1px solid var(--border-color);
        transform: rotate(225deg);
      }

      .share-item {
        width: 100%;
        padding: va.$spacing-sm va.$spacing-lg;
        border: none;
        background: none;
        color: var(--text-secondary);
        font-size: 0.95em;
        display: flex;
        align-items: center;
        gap: va.$spacing-md;
        cursor: pointer;
        transition: all 0.3s ease;
        border-radius: va.$border-radius-sm;

        &:hover {
          background: var(--hover-bg);
          padding-left: va.$spacing-xl;
        }

        &:active {
          transform: scale(0.95);
        }
      }
    }
  }
}
</style>

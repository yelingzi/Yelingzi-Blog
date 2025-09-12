<template>

  <el-card class="author-card">
    <div class="floating-cards">
      <span v-for="n in 12" :key="n" class="card-particle"></span>
    </div>
    <div class="card-header">
      <div class="author-avatar">
        <el-avatar class="avatar" :src="blogInfo.avatar" alt="作者头像" />
      </div>
      <div class="status-badge">
        <span>{{ t('online') }}</span>
      </div>
    </div>
    <div class="author-info">
      <h3>{{ t('nickname') }}</h3>
      <p class="bio">{{ t('signature') }}</p>
    </div>
    <!-- <div class="social-links">
      <div v-for="item in socialLinks" :key="item.type">
        <el-tooltip placement="top" :content="item.content">
          <a v-if="$store.state.webSiteInfo.showList.indexOf(item.type) !== -1" href="javascript:void(0)" title="GitHub"
            :class="`social-btn ${item.type}`" @click="copyToClipboard(item)">
            <i :class="item.icon"></i>
          </a>
        </el-tooltip>
      </div>
    </div> -->
  </el-card>

</template>


<script lang="ts" setup>
import { useBlogStore } from '@/stores';
import { t } from '@/utils/i18n'
const blogData = useBlogStore();
const blogInfo = blogData.blogInfo

</script>

<style lang="scss" scoped>
@use '/src/assets/styles/variables.scss' as va;
@use "sass:math";
@use "sass:list";

.author-card {

  padding: va.$spacing-md;
  margin-bottom: va.$spacing-lg;
  box-shadow: 0 4px 20px rgb(44, 182, 182);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 120px;
    background-image: url('@/assets/images/author-bg.jpg');
    background-size: cover;
    background-position: center;
    transition: opacity 0.3s ease;
    border-top-left-radius: va.$border-radius-md;
    border-top-right-radius: va.$border-radius-md;
  }

  &:hover::before {
    opacity: 0.2;
  }


  &:hover .floating-cards {
    opacity: 1;
  }

  .card-header {
    position: relative;
    margin-bottom: 20px;
    display: flex;
    justify-content: center;

    .author-avatar {
      width: 88px;
      height: 88px;
      position: relative;

      .avatar {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover;
        border: 3px solid #fff;
        box-shadow: 0 4px 24px rgb(50, 163, 255);
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.05);
        }
      }
    }

    .status-badge {
      position: absolute;
      bottom: 0;
      right: 50%;
      transform: translateX(32px);
      background: linear-gradient(135deg, #10b981, #059669);
      border-radius: 12px;
      padding: 4px 12px;
      font-size: 0.75rem;
      color: var(--grey-1);
      box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);

      span {
        display: flex;
        align-items: center;
        gap: 4px;

        &::before {
          content: '';
          display: inline-block;
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: #fff;
        }
      }
    }
  }

  .author-info {
    text-align: center;
    margin-bottom: 20px;

    h3 {
      font-size: 1.25rem;
      font-weight: 700;
      color: va.$primary;
      -webkit-background-clip: text;
      margin-bottom: 8px;
    }

    .bio {
      font-size: 0.9rem;
      color: var(--text-secondary);
      line-height: 1.6;
      margin-bottom: 16px;
    }
  }

  .social-links {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 20px;

    .social-btn {
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 12px;
      background: rgba(99, 102, 241, 0.05);
      font-size: 1.1rem;
      transition: all 0.3s ease;
      position: relative;
      text-decoration: none;
    }

    .qq {
      color: #60a5fa;

      &:hover {
        background: #60a5fa;
        color: white;
      }
    }

    .qqGroup {
      color: #e1c235;

      &:hover {
        background: #e1c235;
        color: white;
      }
    }

    .github {
      color: #000;

      &:hover {
        background: #000;
        color: white;
      }
    }

    .gitee {
      color: #ee3434;

      &:hover {
        background: #da3535;
        color: white;
      }
    }

    .email {
      color: #d872a7;

      &:hover {
        background: #d872a7;
        color: white;
      }
    }

    .wechat {
      color: #10b981;

      &:hover {
        background: #10b981;
        color: white;
      }
    }
  }

  .floating-cards {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    overflow: hidden;
    opacity: 0;
    transition: opacity 0.3s ease;


  }


  .card-particle {
    position: absolute;
    width: 8px;
    height: 8px;
    background: white;
    border-radius: 2px;
    opacity: 0;
    animation: fall 3s ease-in-out infinite;

    @for $i from 1 through 12 {
      &:nth-child(#{$i}) {
        left: #{math.random(100)}#{"%"}; // 使用模块化 random 函数
        animation-delay: #{math.random(3000)}ms;
        // 修正颜色列表和 nth 用法
        background: list.nth((#6366f1, #8b5cf6, #ec4899, #10b981, #f59e0b, #ef4444),
            math.random(6));
        transform: rotate(#{$i * 30}deg);
      }
    }
  }
}

@keyframes fall {
  0% {
    transform: translateY(-20px) rotate(0deg);
    opacity: 0;
  }

  20% {
    opacity: 0.8;
  }

  80% {
    opacity: 0.8;
  }

  100% {
    transform: translateY(300px) rotate(360deg);
    opacity: 0;
  }
}
</style>

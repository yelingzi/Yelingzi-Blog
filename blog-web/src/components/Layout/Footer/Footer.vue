<!--
 * @Description: ------------ fileDescription -----------
 * @Author: snows_l snows_l@163.com
 * @Date: 2024-08-16 16:36:50
 * @LastEditors: snows_l snows_l@163.com
 * @LastEditTime: 2025-01-20 12:46:39
 * @FilePath: \BLOG\src\components\Footer\index.vue
-->
<template>
  <div class="footer-container-warp">
    <div class="content-warp zoomIn" :class="{ 'm-content-warp': isMobi }">
      <!-- <div class="display-warp saying-warp pointer kbn-custom" :data-tip="'点击即可刷新😊'" @click="handleRefreshSaying">「 {{
        state.saying }} 」</div> -->
      <div class="sheming display-warp" style="text-wrap: nowrap">本站内容仅用于学习交流，如有侵权！请联系站长删除。敬请谅解！</div>
      <div class="copy-right display-warp" style="margin-right: 14px">Copyright ©2024 - {{ new Date().getFullYear() }}
        By {{ t('nickname') }} 版权所有</div>
      <div style="margin: 5px 0; color: #fff; display: flex; justify-content: center; align-items: center"
        v-if="!isMobi">
        <span class="pointer beianhao-value hover-shadow" @click="handleICPBeian">
          <!-- <span class="pre">{{ state.domainBeianPre }}</span> -->
          <span class="num" :style="{ 'background-color': state.domainBeianBg }">{{ t('icpBeian') }}</span>
        </span>
        <span class="web-beian-warp pointer" style="margin-left: 20px" @click="handleWebBeian">
          <span class="beianhao-value hover-shadow">
            <span class="pre">
              <img style="margin: -5px 5px 0 0px; width: 14px; height: 14px; vertical-align: middle"
                src="@/assets/images/gongan.png" alt="" />
              <!-- {{ state.beian }} -->{{ t('beian') }}
            </span>
            <!-- <span class="num" :style="{ 'background-color': state.webBeianBg }">{{ state.beian }}</span> -->
          </span>
        </span>

      </div>
      <div v-else class="m-baian">
        <div class="display-warp" style="margin-top: 10px" @click="handleICPBeian">
          <span class="pointer beianhao-value hover-shadow">
            <!-- <span class="pre">{{ state.domainBeianPre }}</span> -->
            <span class="num" :style="{ 'background-color': state.domainBeianBg }">{{ t('icpBeian') }}</span>
          </span>
        </div>
        <div class="display-warp" style="margin-top: 10px" @click="handleWebBeian">
          <span class="pointer beianhao-value hover-shadow">
            <span class="pre">
              <img style="margin: -5px 0px 0px 0; width: 14px; height: 14px; vertical-align: middle"
                src="@/assets/images/gongan.png" alt="" />
              <!-- {{ state.webBeianPre }} -->{{ t('beian') }}
            </span>
            <!-- <span class="num" :style="{ 'background-color': state.webBeianBg }">{{ state.beian }}</span> -->
          </span>
        </div>
      </div>
      <div v-if="ver != ''" class="app-version">版本号：{{ ver }}</div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useResize } from '@/utils/common';
import { t } from '@/utils/i18n'
import { getRandomColor } from '@/utils/theme.ts';
import { reactive } from 'vue';
const isMobi = useResize();

const state = reactive({
  saying: isMobi ? '渔得鱼心满意足，樵得樵眼笑眉舒！' : '渔得鱼心满意足，樵得樵眼笑眉舒！',
  webBeianBg: getRandomColor(),
  domainBeianBg: getRandomColor()
});

const ver = import.meta.env.VITE_APP_FULL_VERSION

// ICP备案号
const handleICPBeian = () => {
  window.open('https://beian.miit.gov.cn/', '_blank');
};

// 网站备案号
const handleWebBeian = () => {
  window.open('https://beian.mps.gov.cn/#/query/webSearch?code=13010802002515', '_blank');
};
</script>

<style lang="scss" scoped>
@use '/src/assets/styles/mixin.scss' as m;

.footer-container-warp {
  width: 100vw;
  padding: 15px;
  box-shadow: 0 1px 20px 10px rgba(255, 255, 255, 0.394);
  background: linear-gradient(-45deg, #ee7752, #ce3e75, #23a6d5, #23d5ab);
  background-size: 400% 400%;
  animation: animation 10s ease infinite;

  .content-warp {
    height: 100%;
    margin: 0 auto;
    width: var(--content-max-width);
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
    animation-duration: 1s;
    animation-fill-mode: both;

    .display-warp {
      display: flex;
      justify-content: center;
      font-size: 14px;
      color: #fff;
      margin: 5px 0;
      text-align: center;
    }

    .beianhao-value {
      display: flex;
      align-items: center;
      border-radius: var(--border-radius-1);
      overflow: hidden;
      height: 22px;

      .pre {
        height: 100%;
        display: flex;
        align-items: center;
        padding: 4px 4px 4px 10px;
        display: inline-block;
        background-color: #5d5d5d;
        border-right: 1px solid var(--border-color);
      }

      .num {
        height: 100%;
        padding: 4px 10px 4px 4px;
        display: inline-block;
      }
    }

    .m-baian {
      .pre {
        line-height: 15px;
      }

      .num {
        line-height: 16px;
      }
    }

    .saying-warp {
      padding: 0 10px;
      border-radius: 5px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  .saying-warp:hover {
    text-shadow: 1px 1px 1px #000;
  }

  .m-content-warp {
    width: var(--content-max-width-m);

    .saying-warp {
      border-radius: 5px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

.app-version {
  color: var(--color-white);
}

@keyframes animation {
  0% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0% 50%;
  }
}

@keyframes zoomIn {
  from {
    opacity: 0;
    transform: scale3d(0.3, 0.3, 0.3);
  }

  50% {
    opacity: 1;
  }
}

.zoomIn {
  animation-name: zoomIn;
}
</style>

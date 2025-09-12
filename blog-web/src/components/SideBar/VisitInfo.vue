<template>
  <div class="visit-info slider-item-warp" :class="{ 'intersection-observer': isMobi }" v-if="visit.ip">
    <div class="slider-title">
      <SvgIcon name="icon-diqiu" color="#222" />{{ t('accessInfo') }}
    </div>
    <el-divider style="margin: 12px 0 0 0;" />
    <div class="visit-content-warp">
      <component :is="weatherIcon" class="tianqi-icon" />
      <div class="visit-item-warp">
        <div class="visit-item">
          <div class="visit-label">
            <SvgIcon name="icon-rili1" />{{ t('date') }}：
          </div>
          <div class="visit-value text">{{ visit.week }}</div>
        </div>
        <div class="visit-item">
          <div class="visit-label">
            <SvgIcon name="icon-dingwei" />IP:
          </div>
          <div class="visit-value text" v-if="visit.ip.length < 15">{{ visit.ip }}</div>

          <div class="visit-value text" v-else>
            <el-tooltip class="box-item" :content="visit.ip">
              <div class="text"
                style="text-wrap: nowrap; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%">
                {{ visit.ip }}
              </div>
            </el-tooltip>
          </div>
        </div>
        <div class="visit-item">
          <div class="visit-label">
            <SvgIcon name="icon-chengshi" color="#222" />{{ t('city') }}：
          </div>
          <div class="visit-value text">{{ visit.location }}</div>
        </div>
        <div class="visit-item">
          <div class="visit-label">
            <SvgIcon name="icon-gugeliulanqi" color="#222" />{{ t('browser') }}：
          </div>
          <div class="visit-value text">{{ visit.browser }}</div>
        </div>
        <div class="visit-item">
          <div class="visit-label">
            <SvgIcon name="icon-diannao" color="#222" />{{ t('platform') }}：
          </div>
          <div class="visit-value text">{{ visit.system }}</div>
        </div>
        <div class="visit-item">
          <div class="visit-label">
            <SvgIcon name="icon-kongqiwendu" color="#222" />{{ t('temperature') }}：
          </div>
          <div class="visit-value text">{{ visit.low }} ~ {{ visit.high }}</div>
        </div>
        <div class="visit-item">
          <div class="visit-label">
            <SvgIcon name="icon-tianqi" color="#222" />{{ t('weather') }}：
          </div>
          <div class="visit-value text">{{ visit.tq }}</div>
        </div>
        <div class="visit-item">
          <div class="visit-label">
            <SvgIcon name="icon-fengli" color="#222" />{{ t('wind') }}：
          </div>
          <div class="visit-value text">{{ visit.fl }} {{ visit.fengxiang }}</div>
        </div>

      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useResize } from '@/utils/common';
import { useTianqiStore } from '@/stores';
import { computed, defineAsyncComponent, h, onMounted, reactive, ref } from 'vue';
import { t } from '@/utils/i18n'

const tianqi = useTianqiStore()
const visit = tianqi.visit

const isMobi = useResize();

// 动态图标计算属性（自动响应currentWeather变化）
const weatherIcon = computed(() =>
  defineAsyncComponent({
    loader: () => {
      // 匹配逻辑（提取第一个匹配的天气关键词）
      const matched = ['晴', '多云', '阴', '小雨', '中雨', '大雨', '阵雨', '暴雨', '小雪', '中雪', '大雪', '雨夹雪', '雾']
        .find(item => visit.tq.includes(item)) || '阴'
      return import(`@/assets/icons/${matched}.svg`)
    },
    loadingComponent: () => h('div', '加载天气图标...'),
    errorComponent: () => h('div', '图标加载失败'),
    delay: 100,
    timeout: 2000
  })
)

onMounted(() => {
  tianqi.checkAndUpdateWeather()
})
</script>


<style lang="scss" scoped>
.visit-info {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  background-image: linear-gradient(#ffffff00, #caf3f4 50%, #b4f6e2 100%);
  margin-bottom: 20px;
  border-radius: 10px;

  .visit-content-warp {
    padding: 20px 0 0 0;
    position: relative;
    background-size: 30% 30%;
    background-repeat: no-repeat;
    background-position: right;
    min-height: 255px;

    .tianqi-icon {
      width: 28%;
      height: 28%;
      position: absolute;
      top: 10px;
      right: 10px;
      animation: cloudAnimation 2s linear infinite;
    }

    .visit-item-warp {
      position: absolute;
      top: 20px;
      left: 0px;
      width: 100%;
      padding: 0 20px 20px 10px;
      z-index: 99;
    }

    .visit-item {
      color: #222;
      display: flex;
      justify-content: flex-start;
      align-items: center;
      margin: 10px 0;

      .visit-label {
        display: flex;
        align-items: center;
        text-align: left;
        width: 105px;
      }

      .visit-label svg {
        margin-right: 5px;
      }

      .visit-value {
        width: calc(100% - 105px);
      }
    }

  }
}

.slider-title {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-left: 10px;
}

.slider-title svg {
  vertical-align: middle;
  line-height: 1;
}
</style>

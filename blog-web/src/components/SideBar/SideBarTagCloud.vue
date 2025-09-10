<template>
  <div class="tag-wall">
    <div class="tag-text">
      <SvgIcon name="icon-youhuiquan" style="margin-right: 0.15rem" />
      {{ i18n.tagCloud }}
    </div>
    <div class="tag-cloud" ref="wrapper" @mouseenter="stopRotate" @mouseleave="startRotate">
      <p v-for="(item, index) in data" :key="index" ref="tag" @click="clickTag(item)"
        @mouseenter="handleTagHover(index)" @mouseleave="handleTagLeave"
        :class="{ 'tag-dimmed': hoveredIndex !== null && hoveredIndex !== index }">

        {{ item.tagName }}
      </p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue';
import { getTagListService } from '@/api/article';
import type { Tags } from '@/types/tag';
import { useRouter } from 'vue-router';
import { useI18nStore, useBlogStore } from '@/stores';
type TimeoutHandle = ReturnType<typeof setTimeout>

interface TagItem {
  x: number;
  y: number;
  z: number;
  ele: HTMLParagraphElement;
}
const i18n = useI18nStore().currentConfig
const router = useRouter();

const data = ref<Tags[]>([]);
const wrapper = ref<HTMLElement | null>(null);
const tag = ref<HTMLParagraphElement[]>([]);
const tagList = ref<TagItem[]>([]);
const hoveredIndex = ref<number | null>(null);
const isRotating = ref(true);
const timer = ref<TimeoutHandle | undefined>(undefined)

const option = reactive({
  radius: 100, // 滚动半径，单位px
  maxFont: 24, // 最大字体大小
  color: null, // 字体颜色。为空时随机
  rotateAngleXbase: 600, // 默认旋转速度基数，数越小速度越快
  rotateAngleYbase: 600,
});

const rotateAngleX = ref(Math.PI / option.rotateAngleXbase);
const rotateAngleY = ref(Math.PI / option.rotateAngleYbase);

// 初始化标签
const _initTags = () => {
  tagList.value = [];
  rotateAngleX.value = Math.PI / option.rotateAngleXbase;
  rotateAngleY.value = Math.PI / option.rotateAngleYbase;

  if (!tag.value || tag.value.length === 0) {
    console.warn('Tags DOM elements are not ready yet.');
    return;
  }

  for (let i = 0; i < data.value.length; i++) {
    // 获取球面上均匀的点的经纬度 θ = arccos( ((2*num)-1)/all - 1); Φ = θ*sqrt(all * π);
    const angleX = Math.acos((2 * (i + 1) - 1) / data.value.length - 1);
    const angleY = angleX * Math.sqrt(data.value.length * Math.PI);
    // 根据经纬度获取点的坐标，球中心的点坐标是 (0,0,0) x=r*sinθ*cosΦ   y=r*sinθ*sinΦ   z=r*cosθ;
    const x = option.radius * Math.sin(angleX) * Math.cos(angleY);
    const y = option.radius * Math.sin(angleX) * Math.sin(angleY);
    const z = option.radius * Math.cos(angleX);

    if (option.color) {
      tag.value[i].style.color = option.color;
    } else {
      // 随机颜色
      tag.value[i].style.color = `rgb(
        ${Math.round(255 * Math.random())},
        ${Math.round(255 * Math.random())},
        ${Math.round(255 * Math.random())}
      )`;
    }

    // 每个标签对象都有四对值
    const tagItem: TagItem = {
      x,
      y,
      z,
      ele: tag.value[i],
    };
    tagList.value.push(tagItem);
  }
  startRotate();
};

// 设置每个标签的坐标位置和字体大小以及透明度
const setPosition = (tagItem: TagItem) => {
  if (!wrapper.value) return;

  tagItem.ele.style.transform = `translate(
    ${tagItem.x + wrapper.value.offsetWidth / 2 - tagItem.ele.offsetWidth / 2}px,
    ${tagItem.y + wrapper.value.offsetHeight / 2 - tagItem.ele.offsetHeight / 2}px
  )`;
  tagItem.ele.style.opacity = `${tagItem.z / option.radius / 2 + 0.7}`;
  tagItem.ele.style.fontSize = `${(tagItem.z / option.radius / 2 + 0.5) * option.maxFont}px`;
};

// 旋转X轴
const rotateX = (tagItem: TagItem) => {
  const cos = Math.cos(rotateAngleX.value);
  const sin = Math.sin(rotateAngleX.value);
  const y1 = tagItem.y * cos - tagItem.z * sin;
  const z1 = tagItem.y * sin + tagItem.z * cos;
  tagItem.y = y1;
  tagItem.z = z1;
};

// 旋转Y轴
const rotateY = (tagItem: TagItem) => {
  const cos = Math.cos(rotateAngleY.value);
  const sin = Math.sin(rotateAngleY.value);
  const x1 = tagItem.z * sin + tagItem.x * cos;
  const z1 = tagItem.z * cos - tagItem.x * sin;
  tagItem.x = x1;
  tagItem.z = z1;
};

// 点击标签
const clickTag = (item: Tags) => {
  useBlogStore().tagId = item.id
  router.push('tag')
};

// 停止旋转
const stopRotate = () => {
  isRotating.value = false;
  if (timer.value) {
    clearInterval(timer.value);
    timer.value = undefined;
  }
};

// 开始旋转
const startRotate = () => {
  if (timer.value) {
    return; // 如果定时器已存在，直接返回
  }

  isRotating.value = true;
  timer.value = setInterval(() => {
    if (!isRotating.value) {
      clearInterval(timer.value);
      timer.value = undefined;
      return;
    }
    for (let i = 0; i < tagList.value.length; i++) {
      rotateX(tagList.value[i]);
      rotateY(tagList.value[i]);
      setPosition(tagList.value[i]);
    }
  }, 20);
};

// 鼠标悬停标签
const handleTagHover = (index: number) => {
  hoveredIndex.value = index;
};

// 鼠标离开标签
const handleTagLeave = () => {
  hoveredIndex.value = null;
};

// 获取标签列表
const getTagList = async () => {
  const res = await getTagListService();
  data.value = res.data.data;
  console.log(data.value);
};

// 生命周期钩子
onMounted(async () => {
  await getTagList();
  _initTags();
});

watch(data, () => {
  setTimeout(() => {
    _initTags();
  }, 0);
});

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value);
    timer.value = undefined;
  }
});
</script>

<style lang="scss" scoped>
.tag-wall {
  padding: 16px;
  background-image: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%);
  margin-top: 20px;
  border-radius: 10px;

  .tag-text {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--color-pink);
    margin: 0px 0px 12px 0px;
    padding-bottom: 8px;
    border-bottom: 2px solid rgba(99, 102, 241, 0.1);
  }
}

.tag-cloud {
  width: 260px;
  height: 260px;
  position: relative;
  color: #333;
  margin: 0 auto;
  text-align: center;

}

.tag-cloud p {
  position: absolute;
  top: 0px;
  left: 0px;
  color: #333;
  font-family: Arial;
  text-decoration: none;
  margin: 0 10px 15px 0;
  line-height: 18px;
  text-align: center;
  font-size: 16px;
  padding: 4px 9px;
  display: inline-block;
  border-radius: 3px;
  transition: opacity 0.3s ease;
}

.tag-cloud p:hover {
  cursor: url('/src/assets/cursors/up.cur'), not-allowed !important;
}

.tag-dimmed {
  opacity: 0.05 !important;
}
</style>

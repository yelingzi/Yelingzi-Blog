<template>
  <CommonLayout :title="photoInfo.albumName" :bg-img="photoInfo.albumCover" />
  <div class="bg">
    <div class="page-container">
      <div class="image-layout-tool" v-if="!isMobi" style="margin-right: 10px">
        <div class="img-icon-warp pointer" style="padding: 5px 5px 5px 7px"
          :class="{ 'icon-active': imageLayout === 24 }" @click="handleChangeLayout(24)">
          <img src="@/assets/icons/col1.png" fit="cover" />
        </div>
        <div class="img-icon-warp pointer" style="padding: 5px 5px 5px 7px"
          :class="{ 'icon-active': imageLayout === 12 }" @click="handleChangeLayout(12)">
          <img src="@/assets/icons/col2.png" />
        </div>
        <div class="img-icon-warp pointer" style="" :class="{ 'icon-active': imageLayout === 8 }"
          @click="handleChangeLayout(8)">
          <img src="@/assets/icons/col3.png" />
        </div>
      </div>
      <ImageList v-if="imageList.length > 0" :key="imageLayout" :image-layout="imageLayout" :image-list="imageList">
      </ImageList>
      <div v-else>
        <el-empty description="当前相册为空"></el-empty>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import CommonLayout from '../Layout/CommonLayout.vue';
import ImageList from '@/components/Image/ImageList.vue';
import type { Photo, PhotoInfo } from '@/types/album';
import { ref, onMounted, reactive } from 'vue';
import { useResize } from '@/utils/common';
import { getAlbumDataService } from '@/api/album'
  ;
import { ElMessage } from 'element-plus';
interface Props {
  id: number
}

const props = withDefaults(defineProps<Props>(), {
  id: 0
})
const isMobi = useResize()
const imageLayout = ref(24)
const photoInfo = ref<PhotoInfo>({
  id: 0,
  albumCover: "",
  albumDesc: "",
  createTime: "",
  albumName: "",
  photoList: [] as Photo[],
});

const imageList = ref<string[]>([])
const handleChangeLayout = (num: number) => {
  imageLayout.value = num
}
const getImageList = async () => {
  console.log(props.id)
  const res = await getAlbumDataService(props.id)
  photoInfo.value = res.data.data
  imageList.value = photoInfo.value.photoList.map(item => item.photoUrl);
}
onMounted(() => {
  getImageList()
})

</script>

<style lang="scss" scoped>
.image-layout-tool {
  margin-bottom: 1.2rem;
  display: flex;
  justify-content: flex-end;

  .img-icon-warp {
    border-radius: var(--border-radius-1);
    border: 1px solid var(--border-color);
    width: 30px;
    height: 30px;
    margin-left: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 5px 5px 5px 7px;

    img {
      height: 100%;
    }
  }

  .icon-active {

    border-color: var(--theme-color);
  }
}
</style>

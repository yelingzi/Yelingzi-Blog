<template>
  <CommonLayout :title="t('tag')" :bgImg="bgImg" />
  <div class="bg">
    <div class="page-container">
      <div class="tag-cloud">
        <div class="tag-item" v-for="tag in tagList" :key="tag.id" @click="changeTags(tag)">
          <el-badge :value="tag.articleCount" :type="typeList.at(tag.id % typeList.length)">
            <el-tag :type="typeList.at(tag.id % typeList.length)" :effect="tag.id === tagData.id ? 'dark' : 'light'">
              <SvgIcon name="icon-youhuiquan" class="tag-icon" />
              {{ tag.tagName }}
            </el-tag>
          </el-badge>
        </div>
      </div>
      <DividerLine symbol="2" />
      <Tag :id="tagData.id" :tag-name="tagData.tagName" :key="tagData.id" @update:tag="changeTags"></Tag>
    </div>
  </div>
</template>

<script setup lang="ts">
import bgImg from '@/assets/images/bg-article.jpg'
import CommonLayout from '../Layout/CommonLayout.vue';
import Tag from './Tag.vue';
import DividerLine from '@/components/Hr/DividerLine.vue';
import { onMounted, ref } from "vue";
import { getTagListService } from '@/api/article';
import { useBlogStore } from "@/stores";

import { t } from '@/utils/i18n'
const blog = useBlogStore()
const tagId = blog.tagId
interface Tag {
  id: number
  tagName: string
  articleCount: number
}

const tagData = ref({
  id: 0,
  tagName: ''
})
const tagList = ref<Tag[]>([])
const typeList = ref(['primary', 'success', 'info', 'warning', 'danger'])

const changeTags = (tag: Tag) => {
  tagData.value.id = tag.id
  tagData.value.tagName = tag.tagName
}

const getTagList = async () => {
  const res = await getTagListService()
  tagList.value = res.data.data
  if (tagId === 0 && tagList.value.length > 0) {
    tagData.value.id = tagList.value.at(0)!.id
    tagData.value.tagName = tagList.value.at(0)!.tagName
  } else {
    tagData.value.id = tagId
  }
  blog.tagId = 0
}


onMounted(() => {
  getTagList()

});
</script>

<style lang="scss" scoped>
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
  padding: 20px;
}

.tag-item {
  transition: all 0.3s;

  &:hover {
    transform: scale(1.1);
  }
}
</style>

<template>
  <div class="container">
    <div class="header">
      <div class="title">{{ t('search') }}</div>
      <component class="icon pointer" :is="closeIcon" @click.stop="onClose()" />
    </div>
    <div class="search-wrapper">
      <div class="search-container">
        <div class="search-input">
          <input class="input text" v-pio="{ text: '输入想要搜索的内容吧' }" v-model="content" @keyup.enter="search"
            :placeholder="t('seachInput')" />
          <button class="send pointer" @click="search">
            <SvgIcon name="icon-Search" class="serach-icon" size="32" />
          </button>
        </div>
      </div>
      <div class="search-results">
        <SearchArticle v-if="searchContent" :search="searchContent" @navigate="handleArticleNavigate" />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { defineAsyncComponent, onMounted, ref } from 'vue';
import { t } from '@/utils/i18n'
import SearchArticle from './SearchArticle.vue';
import { useRouter } from 'vue-router';

const router = useRouter()

const closeIcon = defineAsyncComponent(() =>
  import('@/assets/icons/close.svg')
);

const emit = defineEmits(['close']);
const content = ref('')
const searchContent = ref('')

const onClose = () => {
  emit('close')
}
const handleKeyClose = (e: KeyboardEvent) => {
  if (e.key === 'Escape') {
    emit('close')
  }
}
const search = () => {
  searchContent.value = content.value
}
const handleArticleNavigate = (id: number) => {
  router.push({ name: 'article', params: { id } })
  setTimeout(() => {
    onClose()
  }, 300)
}
onMounted(() => {
  window.addEventListener('keydown', handleKeyClose)
})

</script>

<style lang="scss" scoped>
.container {
  padding: 20px;
  width: 100%;
  background-color: var(--article-bg);
  min-height: 100vh;
  /* 改为min-height确保内容不足时也能撑开 */
}

.search-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 0;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 60px;
  position: relative;
}

.title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 32px;
  font-weight: 500;
  margin: 0 auto;
}

.icon {
  width: 24px;
  height: 24px;
  margin-left: auto;
}

.search-container {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 36px;
}

.search-input {
  display: flex;
  align-items: center;
  background-color: #ff7575;
  border-radius: 10px;
  box-shadow: 0 6px 15px #ff7575, 0 0 0 10px #fff;
  width: 600px;
  padding: 7px 20px;
}

.search-input .input {
  width: 80%;
  flex: 1;
  height: 38px;
  font: 200 22px;
  outline: none;
  color: #fff;
  background-color: transparent;
}

.search-input .input::placeholder {
  color: #8f3636;
}

.search-input .send {
  height: 40px;
  background-color: transparent;
  border: none;
}

.search-input .send .serach-icon {
  color: #F78EE4;
}

.search-results {
  width: 100%;
  max-width: 640px;
  margin: 0 auto;
  padding: 0;
}
</style>

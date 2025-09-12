<!-- src/components/LanguageSwitcher.vue -->
<template>
  <div class="language-switcher">
    <el-dropdown trigger="click" @command="handleSwitch">
      <span class="el-dropdown-link">
        <SvgIcon name="icon-global" size="20" />
        {{ currentLangName }}
      </span>

      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="lang in availableLangs" :key="lang.value" :command="lang.value"
            :class="{ active: lang.value === currentLang }">
            {{ lang.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useI18nStore } from '@/stores'
import { handleI18n } from '@/utils/i18n'

const i18nStore = useI18nStore()
const currentLang = computed(() => i18nStore.currentLang)

const availableLangs = [
  { value: 'zh-CN', label: '简体中文' },
  { value: 'en-US', label: 'English' },
]

const currentLangName = computed(() => {
  const lang = availableLangs.find(l => l.value === currentLang.value)
  return lang ? lang.label : '中文'
})

const handleSwitch = async (lang: string) => {
  await handleI18n(lang)
}
</script>

<style scoped>
.language-switcher {
  cursor: pointer;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
}

.active {
  color: var(--el-color-primary);
  font-weight: bold;
}
</style>

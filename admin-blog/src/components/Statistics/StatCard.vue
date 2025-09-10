<!-- StatCard.vue -->
<template>
    <div class="stat-item">
        <component :is="iconComponent" class="stat-icon" :style="{ backgroundColor: color }" />
        <div class="stat-content">
            <div class="stat-label">{{ label }}</div>
            <div class="stat-value">{{ value }}</div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { defineProps, computed, defineAsyncComponent, type PropType } from 'vue'
import Icons, { type IconName } from '@/utils/icon'

const props = defineProps({
    icon: {
        type: String as PropType<IconName>,
        required: true
    },
    color: {
        type: String,
        default: '#40c9c6'
    },
    label: {
        type: String,
        required: true
    },
    value: {
        type: [Number, String],
        required: true
    }
})

const DefaultIcon = defineAsyncComponent(() => import('@/assets/icons/default.svg'))

const iconComponent = computed(() => {
  try {
    return Icons[props.icon as IconName]
  } catch {
    return DefaultIcon
  }
})
</script>

<style lang="scss" scoped>
.stat-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    background-color: var(--grey-1);
    border: 1px solid var(--grey-9-a1);
    border-radius: 6px;
    margin-right: 12px;
}

.stat-icon {
    width: 48px;
    height: 48px;
    fill: currentColor;
    margin-right: 12px;
    border-radius: 6px;

}

.stat-content {
    display: flex;
    flex-direction: column;
}

.stat-label {
    font-size: 14px;
    color: #888;
}

.stat-value {
    font-size: 18px;
    font-weight: bold;
    color: #333;
}
</style>
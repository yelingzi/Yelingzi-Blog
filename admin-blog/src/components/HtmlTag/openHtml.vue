<template>
    <div class="container">
        <div class="item" v-for="item in data" :key="item.label">
            <span class="unselectable">{{ item.label }}</span>
            <span>{{ item.text }}</span>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';

const data = ref([
    { label: '字体颜色：', text: '<p style="color: red;"></p>' },
    { label: '字体大小：', text: '<p style="font-size:16px;"></p>' },
    { label: '文字居中：', text: '<div style="text-align: center;"></p>' },
    { label: '图片大小：', text: '<img src="" alt="" style="width:100%;height:300px;"></img>' },
    { label: '常用居中：', text: 'style="display: flex;align-items: center;justify-content: center;"' },
]);
</script>

<style lang="scss" scoped>
.container {
    width: 100%;
}

.item{
    font-size: 16px;
    padding: 5px;
}

.unselectable {
    -webkit-user-select: none; /* Safari */
    -moz-user-select: none; /* Firefox */
    -ms-user-select: none; /* Internet Explorer 和 Edge */
    user-select: none; /* Standard syntax */
}
</style>
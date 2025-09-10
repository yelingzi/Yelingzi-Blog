<template>
    <div class="common-layout">
        <el-container>
            <el-aside  width="200px">
                <el-affix class="menu" :offset="1">
                    <Menu :menu-data="menuList" :logo="logo"></Menu>
                </el-affix>
            </el-aside>
            <el-container>
                <el-header style="padding: 0;">
                    <el-affix :offset="1">
                        <Header></Header>
                    </el-affix>
                </el-header>
                <el-main style="padding-bottom: 16px;">
                    <RouterView />
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script lang="ts" setup>
import { RouterView, useRouter } from 'vue-router'
import Menu from "@/components/menu/menu.vue"
import Header from "@/components/header/header.vue";
import logo from '@/assets/images/logo.jpg'
import { onMounted, ref } from "vue";
import { useUserStore } from "@/stores";
import { userInfoService } from "@/api/login";
const userState = useUserStore()
const menuList = ref()

onMounted(() => {
    menuList.value = userState.menuList
})

</script>

<style lang="scss" scoped>
.menu{
    height: 100vh;
    background-color: #545c64;
}

</style>
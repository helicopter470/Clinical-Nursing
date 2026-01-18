<script setup>
import CommonHeader from '@/components/CommonHeader.vue';
import CommonAside from '@/components/CommonAside.vue';
</script>

<template>
    <div class="common-layout">
        <el-container class="lay-container">
            <el-header class="layout-header">
                <common-header />
            </el-header>
            <el-container class="layout-content">
                <el-aside width="200px" class="layout-aside">
                    <common-aside />
                </el-aside>
                <el-main class="layout-main">
                    <router-view />
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<style>
.lay-container {
    height: 100vh;
    padding-top: 70px;
}

.layout-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    height: 70px;
    background-color: #51c0d9;
    padding: 0 20px;
    z-index: 100; 
}

.layout-aside {
    background-color: #f5f5f5;
    border-right: 1px solid #eee;
}

.layout-main {
    padding: 20px;
    background-color: #f9f9f9;
    overflow: auto;
}
</style>
<template>
    <el-aside>
        <el-menu class="menu" router :default-active="router.currentRoute.value.path">
            <el-menu-item index="/home">
                <el-icon>
                    <HomeFilled />
                </el-icon>
                <span>系统首页</span>
            </el-menu-item>
            <el-menu-item index="/nurse" v-if="data.user.role==='PATIENT'">
                <el-icon><Message /></el-icon>
                <span>护工信息</span>
            </el-menu-item>
            <el-menu-item index="/service" v-if="data.user.role==='PATIENT'">
                <el-icon><OfficeBuilding /></el-icon>
                <span>护理服务</span>
            </el-menu-item>
            <el-menu-item index="/knowledge">
                <el-icon><Reading /></el-icon>
                <span>护理知识</span>
            </el-menu-item>
            <el-sub-menu index="1">
                <template #title>
                    <el-icon><Menu /></el-icon>
                    <span>信息管理</span>
                </template>
                <el-menu-item index="/hp">
                    <span>住院信息</span>
                </el-menu-item>
                <el-menu-item index="/dailyMonitor">
                    <span>每日监测</span>
                </el-menu-item>
                <el-menu-item index="/nurseService" v-if="data.user.role==='ADMIN'">
                    <span>护理服务</span>
                </el-menu-item>
                <el-menu-item index="/nurseReserve">
                    <span>护工预约</span>
                </el-menu-item>
                <el-menu-item index="/serviceReserve">
                    <span>服务预约</span>
                </el-menu-item>
                <el-menu-item index="/evaluation">
                    <span>护工评价</span>
                </el-menu-item>
                <el-menu-item index="/careKnowledge" v-if="data.user.role==='ADMIN'">
                    <span>护理知识</span>
                </el-menu-item>
                <el-menu-item index="/wardInfo" v-if="data.user.role==='ADMIN'">
                    <span>病房信息</span>
                </el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="2" v-if="data.user.role==='ADMIN'">
                <template #title>
                    <el-icon><Menu /></el-icon>
                    <span>用户管理</span>
                </template>
                <el-menu-item index="/adminInfo">
                    <span>管理员信息</span>
                </el-menu-item>
                <el-menu-item index="/nurseInfo">
                    <span>护工信息</span>
                </el-menu-item>
                <el-menu-item index="/patientInfo">
                    <span>患者信息</span>
                </el-menu-item>
            </el-sub-menu>
        </el-menu>
    </el-aside>
</template>


<script setup>
import router from "@/router";
import { useUserStore } from '@/stores/useUserStore'
import { reactive } from "vue";

const userStore = useUserStore();
const data = reactive({
    user: { ...userStore.userInfo }
})
</script>

<style scoped lang="less">
.menu {
    flex-direction: column; //垂直排列
    justify-content: flex-start; //顶部开始
    align-items: center;
    width: 100%;
}


.el-menu-item.is-active {
    background-color: #e0edfd !important;
}

.el-menu-item:hover {
    color: #1967e3;
}

.el-aside {
    width: 100%;
    height: 100%;
    background-color: #fff;
}
</style>
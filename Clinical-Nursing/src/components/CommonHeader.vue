<template>
    <div class="header">
        <div class="l-content">
            <img src="../assets/images/logo.png" style="width: 30px;">
            <div class="title">
                临床护理系统
            </div>
        </div>
        <div class="r-content">
            <el-dropdown>
                <span class="el-dropdown-link">
                    <img :src="userStore.getAvatar" alt="用户头像">
                    <span>{{ userStore.getUserName }}</span>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="toPerson">个人中心</el-dropdown-item>
                        <router-link to="/password" style="text-decoration: none; color: inherit;">
                            <el-dropdown-item index="/password">修改密码</el-dropdown-item>
                        </router-link>
                        <el-dropdown-item @click="handleLoginout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script setup>

import { reactive } from 'vue';
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/useUserStore';

const router = useRouter();
const userStore = useUserStore();

const toPerson = () => {
    router.push('/person');
};

const data = reactive({
    user: JSON.parse(localStorage.getItem('system-user') || '{}')
})

if (!data.user?.id) {
    ElMessage.error('请登录！')
    router.push('/login')
}

const handleLoginout = () => {
    userStore.logout();
    ElMessage.success('退出成功')
    router.push('/login')
}

</script>

<style scoped lang="less">
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: #51c0d9;
}


.title {
    font-weight: 700;
    font-size: 20px;
    padding: 0 15px;
    color: #fff;
}

.l-content {
    display: flex;
    align-items: center;
}

.r-content {
    padding-right: 30px;

    .el-button {
        width: 40px;
        height: 40px;
        border-radius: 50%;
    }
}

.el-dropdown-link {
    display: flex;
    justify-content: space-between;
    align-items: center;

    img {
        width: 50px;
        height: 50px;
        border-radius: 50%;
    }

    span {
        display: flex;
        align-items: center;
        margin-left: 8px;
        font-size: 14px;
        color: white;
    }
}
</style>
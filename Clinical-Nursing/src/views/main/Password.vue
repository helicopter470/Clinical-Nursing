<template>
    <div style="width: 50%;">
        <div class="card" style="padding: 20px;">
            <el-form :model="data.form" label-width="100px" style="padding-right: 50px" autocomplete="off">
                <el-form-item label="账号">
                    <el-input disabled v-model="data.user.username" autocomplete="off" />
                </el-form-item>
                <el-form-item label="旧密码" prop="oldPassword">
                    <el-input type="password" name="old-password-visible" v-model="data.form.oldPassword" show-password
                        autocomplete="off" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                    <el-input type="password" name="new-password" v-model="data.form.newPassword" show-password
                        autocomplete="off" />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input type="password" name="confirm-password" v-model="data.form.confirmPassword" show-password
                        autocomplete="off" />
                </el-form-item>
                <div style="text-align: center">
                    <el-button type="primary" @click="save">修改</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { useUserStore } from "@/stores/useUserStore";
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import request from '@/utils/request';

const userStore = useUserStore();
const data = reactive({
    user: { ...userStore.userInfo },
    form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    }
});


const save = async () => {
    if (!data.form.newPassword || !data.form.confirmPassword) {
        ElMessage.warning('请填写新密码并确认');
        return;
    }
    if (data.form.newPassword !== data.form.confirmPassword) {
        ElMessage.warning('两次输入的密码不一致');
        return;
    }
    if (!data.form.oldPassword) {
        ElMessage.warning('请输入旧密码以验证身份');
        return;
    }

    try {
        // 验证旧密码（使用新增的 /verifyPassword）
        const verifyRes = await request.post('/verifyPassword', {
            username: data.user.username,
            password: data.form.oldPassword,
            role: data.user.role
        });
        if (!verifyRes || !(verifyRes.code === '200' || verifyRes.code === 200)) {
            ElMessage.error(verifyRes?.msg || '旧密码错误，验证失败');
            return;
        }

        // 调用后端已有的修改接口（PUT /updatePassword），传 username + new password + role
        const updateRes = await request.put('/updatePassword', {
            username: data.user.username,
            password: data.form.newPassword,
            role: data.user.role
        });
        if (updateRes && (updateRes.code === '200' || updateRes.code === 200)) {
            ElMessage.success('密码修改成功');
            data.form.oldPassword = '';
            data.form.newPassword = '';
            data.form.confirmPassword = '';
        } else {
            ElMessage.error(updateRes?.msg || '修改密码失败');
        }
    } catch (err) {
        ElMessage.error('修改密码失败，详情见控制台');
    }
}
</script>

<style>
.card {
    margin-bottom: 5px;
}
</style>
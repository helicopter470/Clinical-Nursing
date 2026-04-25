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
import { reactive, ref, computed } from "vue";
import { ElMessage } from "element-plus";
import request from '@/utils/request';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const data = reactive({
    user: { ...userStore.userInfo },
    form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    }
});

const passwordStrength = computed(() => {
    const p = data.form.newPassword || '';
    let score = 0;
    if (p.length >= 8) score++;
    if (/[A-Za-z]/.test(p)) score++;
    if (/\d/.test(p)) score++;
    if (/[^A-Za-z\d]/.test(p)) score++;
    if (p.length >= 12) score++;
    if (score <= 1) return { label: '弱', type: 'danger' };
    if (score <= 3) return { label: '中', type: 'warning' };
    return { label: '强', type: 'success' };
});

const validateNewPassword = () => {
    const p = data.form.newPassword || '';
    if (p.length < 8) {
        return '新密码长度至少8位';
    }
    if (!(/[A-Za-z]/.test(p) && /\d/.test(p))) {
        return '新密码需同时包含字母和数字';
    }
    if (data.form.oldPassword && p === data.form.oldPassword) {
        return '新密码不能与旧密码相同';
    }
    return '';
}

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

    const pwdErr = validateNewPassword();
    if (pwdErr) {
        ElMessage.warning(pwdErr);
        return;
    }

    try {
        const res = await request.put('/changePassword', {
            userId: Number(data.user.id),
            role: data.user.role,
            oldPassword: data.form.oldPassword,
            newPassword: data.form.newPassword
        });

        if (res && (res.code === '200' || res.code === 200)) {
            ElMessage.success(res?.msg || '密码修改成功，请重新登录');
            data.form.oldPassword = '';
            data.form.newPassword = '';
            data.form.confirmPassword = '';

            // 清理登录态：token 已不再可信，强制重新登录
            try {
                localStorage.removeItem('system-user');
            } catch (_) {}
            userStore.logout && userStore.logout();
            // 兼容没有 logout 的情况
            if (!userStore.logout) {
                userStore.userInfo = null;
            }
            router.replace('/login');
        } else {
            ElMessage.error(res?.msg || '修改密码失败');
        }
    } catch (err) {
        ElMessage.error(err?.response?.data?.msg || err?.response?.data?.message || '修改密码失败');
    }
}
</script>

<style>
.card {
    margin-bottom: 5px;
}
</style>
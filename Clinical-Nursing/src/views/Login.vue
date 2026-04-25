<template>
    <div class="login-container">
        <div class="login-box">
            <div class="title">临床护理系统
            </div>
            <el-form :model="data.form" ref="formRef" :rules="data.rules">
                <el-form-item prop="username">
                    <el-input :prefix-icon="User" size="large" v-model="data.form.username" placeholder="请输入账号">
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input :prefix-icon="Lock" size="large" v-model="data.form.password" placeholder="请输入密码"
                        show-password>
                    </el-input>
                </el-form-item>
                <el-form-item prop="role">
                    <el-select size="large" style="width: 100%;" v-model="data.form.role">
                        <el-option value="ADMIN" label="管理员"></el-option>
                        <el-option value="NURSE" label="护工"></el-option>
                        <el-option value="PATIENT" label="患者"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button size="large" type="primary" 
                    style="width: 100%;" @click="login">登 录</el-button>
                </el-form-item>
            </el-form>
            <div style="text-align:right">
                还没有账号？请<a href="/register">注册</a>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { User, Lock } from "@element-plus/icons-vue"
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";
import {useUserStore} from "@/stores/useUserStore"

const formRef = ref()
const userStore=useUserStore();
const data = reactive({
    form: { role: 'ADMIN' },
    rules: {
        username: [
            { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
        ],
    }
})

//登陆方法
const login = () => {
    formRef.value.validate((valid => {
        if (valid) {
            //调用后端接口
            request.post('/login', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success('登陆成功')
                    // 后端现在返回：{ user, accessToken, tokenType }
                    const payload = res.data || {}
                    const user = payload.user || {}
                    const accessToken = payload.accessToken

                    // 兼容旧逻辑：把 user 字段铺平，并保留 token 字段给拦截器读取
                    userStore.updateUserInfo({
                        ...user,
                        accessToken,
                        tokenType: payload.tokenType || 'Bearer'
                    });

                    router.push('/home')
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })).catch(error => {
        console.log(error)
    })
}
</script>

<style scoped>
.login-container {
    height: 100vh;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    background-image: url("@/assets/images/background.png");
    background-size: cover;
}

.login-box {
    width: 350px;
    padding: 50px 30px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, .1);
    background-color: rgba(255, 255, 255, .5);
}

.title {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 30px;
    margin-bottom: 20px;
}
</style>
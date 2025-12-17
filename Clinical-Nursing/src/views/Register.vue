<template>
    <div class="register-container">
        <div class="register-box">
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
                <el-form-item prop="confirmPassword">
                    <el-input :prefix-icon="Lock" size="large" v-model="data.form.confirmPassword" placeholder="请确认密码"
                        show-password>
                    </el-input>
                </el-form-item>
                <el-form-item>
                    <el-button size="large" type="primary" style="width: 100%;" @click="register">注 册</el-button>
                </el-form-item>
            </el-form>
            <div style="text-align: right;">
                已有帐号？请<a href="/login">登录</a>
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

const formRef = ref()
const validatePass = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请确认密码'))
    } else if (value !== data.form.password) {
        callback(new Error('两次输入密码不一致'))
    } else {
        callback()
    }
}
const data = reactive({
    form: { role: 'ADMIN' },
    rules: {
        username: [
            { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        confirmPassword: [
            { validator: validatePass, trigger: 'blur' }
        ]
    }
})

//注册方法
const register = () => {
    formRef.value.validate((valid => {
        if (valid) {
            //调用后端接口
            request.post('/register', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success('注册成功')
                    router.push('/login')
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
.register-container {
    height: 100vh;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    background-image: url("@/assets/images/background.png");
    background-size: cover;
}

.register-box {
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
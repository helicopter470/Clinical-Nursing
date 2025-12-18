<template>
    <div style="width: 50%;">
        <div class="card" style="padding: 20px;">
            <el-form :model="data.user" label-width="100px" style="padding-right: 50px">
                <el-form-item label="头像" style="margin: 20px 0; text-align: center;">
                    <div>
                        <el-upload :show-file-list="false" class="avatar-uploader" :action="uploadUrl"
                            :on-success="handleFileUpload">
                            <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
                            <el-icon v-else class="avatar-uploader-icon">
                                <Plus />
                            </el-icon>
                        </el-upload>
                    </div>
                </el-form-item>
                <el-form-item label="账号">
                    <el-input disabled v-model="data.user.username" autocomplete="off" />
                </el-form-item>
                <el-form-item label="姓名">
                    <el-input v-model="data.user.name" autocomplete="off" />
                </el-form-item>
                <el-form-item label="电话">
                    <el-input v-model="data.user.phone" autocomplete="off" />
                </el-form-item>
                <el-form-item label="擅长" v-if="data.user.role === 'NURSE'">
                    <el-input v-model="data.user.speciality" autocomplete="off" />
                </el-form-item>
                <el-form-item label="从业年限" v-if="data.user.role === 'NURSE'">
                    <el-input-number v-model="data.user.experience" :min="1" :max="100" />
                </el-form-item>
                <el-form-item label="性别" v-if="data.user.role !== 'ADMIN'">
                    <el-radio-group v-model="data.user.sex">
                        <el-radio value="男">男</el-radio>
                        <el-radio value="女">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="年龄" v-if="data.user.role !== 'ADMIN'">
                    <el-input-number v-model="data.user.age" :min="1" :max="100" />
                </el-form-item>
                <el-form-item label="身份证号" v-if="data.user.role !== 'ADMIN'">
                    <el-input v-model="data.user.idcard" autocomplete="off" />
                </el-form-item>
                <el-form-item label="家庭住址" v-if="data.user.role !== 'ADMIN'">
                    <el-input v-model="data.user.address" :rows="2" type="textarea" placeholder="请输入" />
                </el-form-item>
                <!-- <el-form-item label="账户余额"></el-form-item> -->
                <div style="text-align: center">
                    <el-button type="primary" @click="save">保存</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script setup>

import { reactive } from 'vue';
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { useUserStore } from '@/stores/useUserStore'

//文件上传的接口地址
const uploadUrl = import.meta.env.VITE_APP_BASE_URL + '/files/upload'
const userStore = useUserStore();
const data = reactive({
    user: { ...userStore.userInfo }
})


//更新头像
const handleFileUpload = (file) => {
    data.user.avatar = file.data;
    //调用store方法更新头像
    userStore.updateAvatar(data.user.avatar);
    ElMessage.success('头像上传成功');
}

const emit = defineEmits(["updateUser"])
//把当前修改的用户信息存储到后台数据库
const save = () => {
    if (data.user.role === 'ADMIN') {
        request.put('/admin/update', data.user).then(res => {
            if (res.code === '200') {
                ElMessage.success('更新成功')
                //把更新后的用户信息存储到缓存
                // localStorage.setItem('system-user', JSON.stringify(data.user))
                userStore.updateUserInfo(data.user);
                emit('updateUser')
            } else {
                ElMessage.error(res.msg)
            }
        })
    } else if (data.user.role === 'PATIENT') {
        request.put('/patient/update', data.user).then(res => {
            if (res.code === '200') {
                ElMessage.success('更新成功')
                //把更新后的用户信息存储到缓存
                //localStorage.setItem('system-user', JSON.stringify(data.user))
                userStore.updateUserInfo(data.user);
                emit('updateUser')
            } else {
                ElMessage.error(res.msg)
            }
        })
    } else if (data.user.role === 'NURSE') {
        request.put('/nurse/update', data.user).then(res => {
            if (res.code === '200') {
                ElMessage.success('更新成功')
                //把更新后的用户信息存储到缓存
                //localStorage.setItem('system-user', JSON.stringify(data.user))
                userStore.updateUserInfo(data.user);
                emit('updateUser')
            } else {
                ElMessage.error(res.msg)
            }
        })
    }

}

</script>

<style scoped>
.avatar-uploader .avatar {
    width: 120px;
    height: 120px;
    display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    text-align: center;
}
</style>
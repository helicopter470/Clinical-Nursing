<template>
    <div>
        <div class="card">
            <!-- 回车搜索 -->
            <el-input v-model="pageStore.name" style="width: 250px;margin-right: 10px;" placeholder="请输入姓名查询"
                @keyup.enter="pageStore.setName(pageStore.name)"></el-input>
            <el-button type="primary" @click="pageStore.setName(pageStore.name)">查询</el-button>
            <el-button type="info" style="margin-left: 10px;" @click="reset">重置</el-button>
        </div>
        <div class="card">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="info" style="margin-left: 10px;" @click="delBatch">批量删除</el-button>
        </div>
        <div class="card">
            <el-table :data="pageStore.tableData" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column label="账号" prop="username" />
                <el-table-column label="头像">
                    <template #default="scope">
                        <!-- 预览和防止被遮挡 -->
                        <el-image v-if="scope.row.avatar" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"
                            preview-teleported />
                    </template>
                </el-table-column>
                <el-table-column label="姓名" prop="name" />
                <el-table-column label="角色" prop="role" />
                <el-table-column label="电话" prop="phone" />
                <el-table-column label="操作" min-width="120" fixed="right">
                    <template #default="scope">
                        <el-button type="primary" @click="handleUpdate(scope.row)" size="small"
                            plain>编辑</el-button>
                        <el-button type="danger" @click="handleDelete(scope.row.id)" size="small"
                            plain>删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="card">
            <el-pagination @current-change="pageStore.setPageNum" @size-change="pageStore.setPageSize"
                v-model:current-page="pageStore.pageNum" v-model:page-size="pageStore.pageSize"
                layout="total, prev, pager, next" :total="pageStore.total"></el-pagination>
        </div>
        <!-- 关闭时销毁弹窗 -->
        <el-dialog title="管理员信息" v-model="data.formVisible" width="750" destroy-on-close>
            <el-form ref="formRef" :rules="data.rules" :model="data.form" label-width="80px"
                style="margin-right: 40px;padding-top: 20px;">
                <el-form-item label="账号" prop="username">
                    <!-- 禁用自动完成 -->
                    <el-input :disabled="data.form.id" v-model="data.form.username" autocomplete="off"
                        placeholder="请输入账号" />
                </el-form-item>
                <el-form-item label="头像">
                    <el-upload :action="uploadUrl" list-type="picture" :on-success="handleAvatarSuccess">
                        <el-button type="primary">上传头像</el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="电话">
                    <el-input v-model="data.form.phone" autocomplete="off" placeholder="请输入电话" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="data.formVisible = false">取消</el-button>
                    <el-button type="primary" @click="save">
                        保存
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>

import { usePageStore } from '@/stores/usePageStore'
import { useUserStore } from '@/stores/useUserStore'
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, reactive, ref } from 'vue';
import request from '@/utils/request'

//文件上传的接口地址
const uploadUrl = import.meta.env.VITE_APP_BASE_URL + '/files/upload'

const data = reactive({
    formVisible: false,
    form: '',
    ids: [],
    rules: {
        username: [
            { required: true, message: "请输入账号", trigger: 'blur' }
        ],
        name: [
            { required: true, message: "请输入名称", trigger: 'blur' }
        ]
    }
})

const pageStore = usePageStore();
const userStore = useUserStore();

const reset = () => {
    pageStore.name = '',
        pageStore.loadData();
}

const handleAdd = () => {
    data.formVisible = true
    data.form = {}
}

const formRef = ref()
const save = () => {
    //判断是新增还是编辑
    formRef.value.validate((vaild) => {
        if (vaild) {
            data.form.id ? update() : add()
        }
    })
}

const add = () => {
    request.post('/admin/add', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success("操作成功")
            pageStore.loadData()//新增后重新加载数据
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const update = () => {
    request.put('/admin/update', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            // 新增：判断是否是当前登录用户，是则更新store
            if (data.form.id === userStore.userInfo.id) {
                userStore.updateUserInfo(data.form);
            }
            ElMessage.success("操作成功")
            pageStore.loadData()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const handleUpdate = (row) => {
    data.form = { ...row }
    // 深拷贝当前行数据（单层对象适用）
    data.formVisible = true
}

const handleDelete = (id) => {
    ElMessageBox.confirm('删除后的数据无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/admin/deleteById/' + id).then(res => {
            if (res.code === '200') {
                ElMessage.success("操作成功")
                pageStore.loadData()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

const handleSelectionChange = (rows) => {//f返回所有选中的行对象数组
    //从选中的行数组里面提取出所有的id组成一个新数组
    data.ids = rows.map(row => row.id)
}

const delBatch = () => {
    if (data.ids.length === 0) {
        ElMessage.warning('请选择数据')
        return
    }
    ElMessageBox.confirm('删除数据后无法恢复，您确认删除吗？', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/admin/deleteBatch', { data: data.ids }).then(res => {
            if (res.code === '200') {
                ElMessage.success('操作成功')
                pageStore.loadData() // 删除后一定要重新加载最新的数据
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

const handleAvatarSuccess = (res) => {
    data.form.avatar = res.data
}

// 组件挂载时加载初始数据
onMounted(() => {
    pageStore.setType('admin');
    pageStore.loadData();
});

</script>

<style>
.card {
    margin-bottom: 5px;
}

.el-image {
    display: block;
    width: 40px;
    height: 40px;
    border-radius: 50%;
}
</style>
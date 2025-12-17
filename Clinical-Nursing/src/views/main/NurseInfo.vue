<template>
    <div>
        <div class="card">
            <el-input v-model="pageStore.name" style="width: 250px; margin-right: 10px;"
                placeholder="请输入姓名查询"></el-input>
            <el-button type="primary" @click="pageStore.setName(pageStore.name)">查询</el-button>
            <el-button type="info" @click="reset" style="margin-left: 10px;">重置</el-button>
        </div>
        <div class="card">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="info" @click="delBatch" style="margin-left: 10px;">批量删除</el-button>
        </div>
        <div class="card">
            <el-table script :data="pageStore.tableData" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column label="账号" prop="username" />
                <el-table-column label="密码" prop="password" />
                <el-table-column label="姓名" prop="name" />
                <el-table-column label="头像">
                    <template #default="scope">
                        <el-image v-if="scope.row.avatar" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"
                            preview-teleported></el-image>
                    </template>
                </el-table-column>
                <el-table-column label="角色" prop="role" />
                <el-table-column label="电话" prop="phone" show-overflow-tooltip/>
                <el-table-column label="擅长" prop="speciality" show-overflow-tooltip/>
                <el-table-column label="从业年限" prop="experience" />
                <el-table-column label="性别" prop="sex" />
                <el-table-column label="年龄" prop="age" />
                <el-table-column label="身份证号" prop="idcard" show-overflow-tooltip/>
                <el-table-column label="家庭住址" prop="address" show-overflow-tooltip/>
                <el-table-column label="操作" min-width="120" fixed="right">
                    <template #default="scope">
                        <el-button type="primary" :icon="Edit" @click="handleUpdate(scope.row)" circle
                            plain></el-button>
                        <el-button type="danger" :icon="Delete" @click="handleDelete(scope.row.id)" circle
                            plain></el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="card">
            <el-pagination @current-change="pageStore.setPageNum" @size-change="pageStore.setPageSize"
                v-model:current-page="pageStore.pageNum" v-model:page-size="pageStore.pageSize"
                layout="total, prev, pager, next" :total="pageStore.total">
            </el-pagination>
        </div>
        <el-dialog title="护工信息" v-model="data.formVisible" destory-on-close width="750">
            <el-form ref="formRef" :rules="data.rules" :model="data.form" label-width="80px"
                style="margin-left: 40px;padding-top: 20px;">
                <el-form-item label="账号" prop="username">
                    <el-input :disabled="!!data.form.id" v-model="data.form.username" autocomplete="off"
                        placeholder="请输入账号" />
                </el-form-item>
                <el-form-item label="头像">
                    <el-upload :action="uploadUrl" list-type="picture" :on-success="handleAvatarSuccess">
                        <el-button type="primary">上传头像</el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入姓名"></el-input>
                </el-form-item>
                <el-form-item label="电话">
                    <el-input v-model="data.form.phone" autocomplete="off" placeholder="请输入电话"></el-input>
                </el-form-item>
                <el-form-item label="擅长">
                    <el-input v-model="data.form.specialty" autocomplete="off" placeholder="请输入擅长"></el-input>
                </el-form-item>
                <el-form-item label="从业年限">
                    <el-input-number v-model="data.form.experience" autocomplete="off" :min="1"
                        :max="100"></el-input-number>
                </el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="data.form.sex">
                        <el-radio value="男">男</el-radio>
                        <el-radio value="女">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="年龄">
                    <el-input-number v-model="data.form.age" :min="1" :max="100" />
                </el-form-item>
                <el-form-item label="身份证号">
                    <el-input v-model="data.form.idcard" autocomplete="off" placeholder="请输入身份证号"></el-input>
                </el-form-item>
                <el-form-item label="家庭住址">
                    <el-input v-model="data.form.address" autocomplete="off" placeholder="请输入" :rows="2"
                        type="textarea"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="data.formVisible = false">取消</el-button>
                    <el-button type="primary" @click="save">保存</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>

import { usePageStore } from '@/stores/usePageStore'
import { onMounted, reactive, ref } from 'vue';
import { Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request';

const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const pageStore = usePageStore()

const data = reactive({
    formVisible: false,
    form: '',
    ids: [],
    rules: {
        username: [
            { required: true, message: '请输入账号', trigger: 'blur' }
        ],
        avatar: [
            { required: true, message: '请上传头像', trigger: 'blur' }
        ],
        name: [
            { required: true, message: '请输入姓名', trigger: 'blur' }
        ]
    }
})

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
    request.post('/nurse/add', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('操作成功')
            pageStore.loadData()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const update = () => {
    request.put('/nurse/update', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('操作成功')
            pageStore.loadData()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const handleUpdate = (row) => {
    data.form = {
        ...row,
        age: row.age != null ? Number(row.age) : row.age,
        experience: row.experience != null ? Number(row.experience) : row.experience
    }
    data.formVisible = true
}

const handleDelete = (id) => {
    ElMessageBox.confirm('删除后的数据无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/nurse/deleteById/' + id).then(res => {
            if (res.code === '200') {
                ElMessage.success('操作成功')
                pageStore.loadData()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

const handleSelectionChange = (rows) => {
    data.ids = rows.map(row => row.id)
}

const delBatch = () => {
    if (data.ids.length === 0) {
        ElMessage.error('请选择数据')
        return
    }
    ElMessageBox.confirm('删除后的数据无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/nurse/delBatch', { data: data.ids }).then(res=>{
            if(res.code==='200'){
                ElMessage.success('操作成功')
                pageStore.loadData()
            }else{
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

const handleAvatarSuccess = (res) => {
    data.form.avatar = res.data
}

onMounted(() => {
    pageStore.setType('nurse');
    pageStore.loadData();
})

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
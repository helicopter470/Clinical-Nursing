<template>
    <div>
        <div class="card">
            <!-- 回车搜索 -->
            <el-input v-model="pageStore.name" style="width: 250px;margin-right: 10px;" placeholder="请输入名称查询"
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
                <el-table-column label="名称" prop="name" />
                <el-table-column label="图片">
                    <template #default="scope">
                        <!-- 预览和防止被遮挡 -->
                        <el-image v-if="scope.row.picture" :src="scope.row.picture"
                            :preview-src-list="[scope.row.picture]" preview-teleported />
                    </template>
                </el-table-column>
                <el-table-column label="介绍" prop="introduction" show-overflow-tooltip />
                <el-table-column label="价格">
                    <template #default="scope">
                        <span style="color: red;font-weight: 700;">¥{{ scope.row.price }}/天</span>
                    </template>
                </el-table-column>
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
        <el-dialog title="护理服务信息" v-model="data.formVisible" width="750" destroy-on-close>
            <el-form ref="formRef" :rules="data.rules" :model="data.form" label-width="80px"
                style="margin-right: 40px;padding-top: 20px;">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="data.form.name" autocomplete="off" placeholder="请输入名称" />
                </el-form-item>
                <el-form-item label="图片">
                    <el-upload :action="uploadUrl" list-type="picture" :on-success="handlePictureSuccess">
                        <el-button type="primary">上传图片</el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item label="介绍" prop="introduction">
                    <el-input v-model="data.form.introduction" autocomplete="off" placeholder="请输入介绍" :row="2"
                        type="textarea" />
                </el-form-item>
                <el-form-item label="价格" prop="price">
                    <el-input v-model="data.form.price" autocomplete="off" placeholder="请输入价格" />
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
        name: [
            { required: true, message: "请输入名称", trigger: 'blur' }
        ],
        introduction: [
            { required: true, message: "请输入介绍", trigger: 'blur' }
        ],
        price: [
            { required: true, message: "请输入价格", trigger: 'blur' }
        ],
    }
})

const pageStore = usePageStore();

const reset = () => {
    pageStore.name = '';
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
    request.post('/nurseService/add', data.form).then(res => {
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
    request.put('/nurseService/update', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
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
        request.delete('/nurseService/deleteById/' + id).then(res => {
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
        request.delete('/nurseService/deleteBatch', { data: data.ids }).then(res => {
            if (res.code === '200') {
                ElMessage.success('操作成功')
                pageStore.loadData() // 删除后一定要重新加载最新的数据
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

const handlePictureSuccess = (res) => {
    data.form.picture = res.data
}

// 组件挂载时加载初始数据
onMounted(() => {
    pageStore.setType('nurseService');
    pageStore.loadData();
});

</script>

<style>
.card {
    margin-bottom: 5px;
}

.el-image {
    display: block;
    width: 60px;
    height: 60px;
    border-radius: 10%;
}
</style>
<template>
    <div>
        <div class="card">
            <el-input v-model="pageStore.nurseName" style="width: 250px; margin-right: 10px;" placeholder="请输入护工名称查询"
                v-if="data.user.role === 'ADMIN'"></el-input>
            <el-input v-model="pageStore.name" style="width: 250px; margin-right: 10px;"
                placeholder="请输入患者名称查询"></el-input>
            <el-button type="primary" @click="handleSelect">查询</el-button>
            <el-button type="info" @click="reset" style="margin-left: 10px;">重置</el-button>
        </div>
        <div class="card" v-if="data.user.role === 'ADMIN'">
            <el-button type="info" @click="delBatch" style="margin-left: 10px;">批量删除</el-button>
        </div>
        <div class="card">
            <el-table script :data="tableDataFiltered" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" v-if="data.user.role === 'ADMIN'"></el-table-column>
                <el-table-column label="护工" prop="nurseName" />
                <el-table-column label="患者" prop="patientName" />
                <el-table-column label="开始时间" prop="startDate" />
                <el-table-column label="结束时间" prop="endDate" />
                <el-table-column label="状态" prop="status">
                    <template #default="scope">
                        <el-tag type="danger" v-if="scope.row.status === '已取消'">已取消</el-tag>
                        <el-tag type="warning" v-if="scope.row.status === '待审核'">待审核</el-tag>
                        <el-tag type="success" v-if="scope.row.status === '服务中'">服务中</el-tag>
                        <el-tag type="danger" v-if="scope.row.status === '拒绝'">拒绝</el-tag>
                        <el-tag type="primary" v-if="scope.row.status === '已结束'">已结束</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="申请时间" prop="applicationTime" />
                <el-table-column label="审核意见" prop="reserveOption" />
                <el-table-column label="审核" prop="review" min-width="120" v-if="data.user.role === 'NURSE'">
                    <template #default="scope">
                        <el-button type="primary" size="small" plain v-if="scope.row.status === '待审核'"
                            @click="handlePass(scope.row)">通过</el-button>
                        <el-button type="danger" size="small" plain v-if="scope.row.status === '待审核'"
                            @click="handleReject(scope.row)">拒绝</el-button>
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="120" fixed="right">
                    <template #default="scope">
                        <el-button type="primary" size="small" plain
                            v-if="data.user.role === 'PATIENT' && scope.row.status === '已结束'"
                            @click="evaluation(scope.row)">评价</el-button>
                        <el-button type="danger" :icon="Delete" @click="handleDelete(scope.row.id)" circle plain
                            v-if="data.user.role === 'ADMIN'"></el-button>
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
        <el-dialog v-model="data.reviewVisible" width="500">
            <el-form ref="formRef" :model="data.form" label-width="80px" style="margin-right: 40px;padding-top: 20px;">
                <el-form-item label="审核意见">
                    <el-input v-model="data.form.reserveOption" autocomplete="off" placeholder="请输入内容" :row="4"
                        type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="data.reviewVisible = false">取消</el-button>
                    <el-button type="primary" @click="reviewSave">保存</el-button>
                </div>
            </template>
        </el-dialog>
        <el-dialog title="护工评价" v-model="data.formVisible" width="500">
            <el-form ref="formRef" :model="data.form" label-width="80px" style="margin-right: 40px;padding-top: 20px;">
                <el-form-item label="评分">
                    <el-rate v-model="data.form.rating" :max="5" allow-half />
                </el-form-item>
                <el-form-item label="评价内容">
                    <el-input v-model="data.form.comment" autocomplete="off" placeholder="请输入内容" :row="4"
                        type="textarea" />
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
import { onMounted, reactive, ref, computed } from 'vue';
import { Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/stores/useUserStore';
import request from '@/utils/request';


const pageStore = usePageStore()
const userStore = useUserStore()

const data = reactive({
    formVisible: false,
    reviewVisible: false,
    form: '',
    ids: [],
    user: { ...userStore.userInfo },
})

// 表格按角色过滤：ADMIN 全看，NURSE 只看自己负责的预约，PATIENT 只看自己的预约
const tableDataFiltered = computed(() => pageStore.tableData || [])

const handleSelect = () => {
    pageStore.pageNum = 1;
    pageStore.loadData();
}

const reset = () => {
    pageStore.name = '';
    pageStore.nurseName = '';
    pageStore.pageNum = 1;
    pageStore.loadData();
}

const formRef = ref()

const handleDelete = (id) => {
    ElMessageBox.confirm('删除后的数据无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/nurseReserve/deleteById/' + id).then(res => {
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
        request.delete('/nurseReserve/delBatch', { data: data.ids }).then(res => {
            if (res.code === '200') {
                ElMessage.success('操作成功')
                pageStore.loadData()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

const handlePass = (row) => {
    ElMessageBox.confirm('确认通过该申请吗？', '操作确认', { type: 'info' }).then(() => {
        const updateData = {
            ...row,
            id: row.id,
            status: '服务中'
        };
        request.put('/nurseReserve/update', updateData).then(res => {
            if (res.code === '200') {
                ElMessage.success('审核通过成功');
                pageStore.loadData();
            } else {
                ElMessage.error(res.msg);
            }
        })
    }).catch();
}

const handleReject = (row) => {
    data.form = { ...row }
    data.reviewVisible = true
}

const reviewSave = () => {
    if (!data.form.reserveOption) {
        ElMessage.error('请填写审核意见');
        return;
    }
    ElMessageBox.confirm('确认拒绝该申请吗？', '操作确认', { type: 'warning' }).then(() => {
        data.form.status = '拒绝';
        request.put('/nurseReserve/update', data.form).then(res => {
            if (res.code === '200') {
                data.reviewVisible = false;
                ElMessage.success('操作成功');
                pageStore.loadData();
            } else {
                ElMessage.error(res.msg);
            }
        })
    }).catch()
}

const evaluation = (row) => {
    data.form = {
        nurseId: row.nurseId,
        patientId: row.patientId,
        rating: 0,
        comment: ''
    },
    data.formVisible = true

}
const save = () => {
    request.post('/evaluation/add', data.form).then(res => {
        if (res.code === '200') {
            data.formVisible = false
            ElMessage.success('操作成功')
            pageStore.loadData()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

onMounted(() => {
    pageStore.setType('nurseReserve');
    pageStore.loadData();
})

</script>

<style>
.card {
    margin-bottom: 5px;
}
</style>
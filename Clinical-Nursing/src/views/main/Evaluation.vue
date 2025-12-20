<template>
    <div>
        <div class="card">
            <el-input v-model="pageStore.nurseName" style="width: 250px; margin-right: 10px;" placeholder="请输入护工名称查询"
                v-if="data.user.role !== 'NURSE'"></el-input>
            <el-input v-model="pageStore.name" style="width: 250px; margin-right: 10px;" placeholder="请输入患者名称查询"
                v-if="data.user.role !== 'PATIENT'"></el-input>
            <el-button type="primary" @click="handleSelect">查询</el-button>
            <el-button type="info" @click="reset" style="margin-left: 10px;">重置</el-button>
        </div>
        <div class="card" v-if="data.user.role !== 'NURSE'">
            <el-button type="info" @click="delBatch" style="margin-left: 10px;">批量删除</el-button>
        </div>
        <div class="card">
            <el-table script :data="tableDataFiltered" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" v-if="data.user.role !== 'NURSE'"></el-table-column>
                <el-table-column label="护工" prop="nurseName" />
                <el-table-column label="患者" prop="patientName" />
                <el-table-column label="评分" prop="rating">
                    <template #default="scope">
                        <el-rate :model-value="Number(scope.row.rating)" disabled allow-half />
                    </template>
                </el-table-column>
                <el-table-column label="评论" prop="comment" />
                <el-table-column label="评价时间" prop="evaluationTime" />
                <el-table-column label="操作" min-width="120" fixed="right" v-if="data.user.role !== 'NURSE'">
                    <template #default="scope">
                        <el-button type="danger" @click="handleDelete(scope.row.id)" size="small"
                            plain>删除</el-button>
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
    </div>
</template>

<script setup>

import { usePageStore } from '@/stores/usePageStore'
import { onMounted, reactive, ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/stores/useUserStore';
import request from '@/utils/request';


const pageStore = usePageStore()
const userStore = useUserStore()

// 表格按角色过滤：ADMIN 全看，NURSE 只看自己负责的预约，PATIENT 只看自己的预约
const tableDataFiltered = computed(() => pageStore.tableData || [])

const data = reactive({
    formVisible: false,
    form: '',
    ids: [],
    user: { ...userStore.userInfo },
})

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


const handleDelete = (id) => {
    ElMessageBox.confirm('删除后的数据无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/evaluation/deleteById/' + id).then(res => {
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
        request.delete('/evaluation/delBatch', { data: data.ids }).then(res => {
            if (res.code === '200') {
                ElMessage.success('操作成功')
                pageStore.loadData()
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

//获取患者列表
const patientList = ref([]);
const getPatientList = () => {
    request.get('/patient/selectAll').then(res => {
        if (res.code === '200') {
            patientList.value = res.data;
        }
    });
}


onMounted(() => {
    pageStore.setType('evaluation');
    pageStore.loadData();
    getPatientList();
})

</script>

<style>
.card {
    margin-bottom: 5px;
}
</style>
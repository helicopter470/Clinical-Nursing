<template>
    <div>
        <div class="card">
            <el-input v-model="pageStore.nurseName" style="width: 250px; margin-right: 10px;" placeholder="请输入护工名称查询"
                v-if="data.user.role === 'ADMIN'"></el-input>
            <el-input v-model="pageStore.name" style="width: 250px; margin-right: 10px;" placeholder="请输入患者名称查询"
                v-if="data.user.role !== 'PATIENT'"></el-input>
            <el-date-picker v-model="pageStore.record" style="width: 250px; margin-right: 10px;" type="date"
                placeholder="记录日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
            <el-button type="primary" @click="handleSelect">查询</el-button>
            <el-button type="info" @click="reset" style="margin-left: 10px;">重置</el-button>
        </div>
        <div class="card" v-if="data.user.role !== 'PATIENT'">
            <el-button type="primary" @click="handleAdd" v-if="data.user.role === 'NURSE'">新增</el-button>
            <el-button type="info" style="margin-left: 10px;" @click="delBatch">批量删除</el-button>
        </div>
        <div class="card">
            <el-table :data="tableDataFiltered" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" v-if="data.user.role !== 'PATIENT'"></el-table-column>
                <el-table-column label="患者" prop="patientName" />
                <el-table-column label="护工" prop="nurseName" />
                <el-table-column label="血压(mmHg)" prop="blood" />
                <el-table-column label="体温(℃)" prop="temp" />
                <el-table-column label="脉搏(次/分钟)" prop="pulse" />
                <el-table-column label="呼吸(次/分钟)" prop="breathe" />
                <el-table-column label="精神状态" prop="mental">
                    <template #default="scope">
                        <el-tag type="primary" v-if="scope.row.mental === '正常'">正常</el-tag>
                        <el-tag type="success" v-if="scope.row.mental === '良好'">良好</el-tag>
                        <el-tag type="warning" v-if="scope.row.mental === '较差'">较差</el-tag>
                        <el-tag type="danger" v-if="scope.row.mental === '很差'">很差</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="饮食状态" prop="dietary">
                    <template #default="scope">
                        <el-tag type="primary" v-if="scope.row.dietary === '正常'">正常</el-tag>
                        <el-tag type="success" v-if="scope.row.dietary === '良好'">良好</el-tag>
                        <el-tag type="warning" v-if="scope.row.dietary === '较差'">较差</el-tag>
                        <el-tag type="danger" v-if="scope.row.dietary === '很差'">很差</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="房间" prop="wardName" />
                <el-table-column label="床号" prop="bedName" />
                <el-table-column label="记录日期" prop="record" />
                <el-table-column label="备注" prop="remark" />
                <el-table-column label="操作" min-width="120" fixed="right" v-if="data.user.role !== 'PATIENT'">
                    <template #default="scope">
                        <el-button type="primary" :icon="Edit" circle @click="handleUpdate(scope.row)" plain
                            v-if="data.user.role === 'NURSE'"></el-button>
                        <el-button type="danger" :icon="Delete" circle @click="handleDelete(scope.row.id)"
                            plain></el-button>
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
            <el-form ref="formRef" :rules="data.rules" :model="data.form" label-width="120px"
                style="margin-right: 40px;padding-top: 20px;">
                <el-form-item label="患者" prop="patientId">
                    <el-select v-model="data.form.patientId" placeholder="请选择患者" filterable clearable
                        @change="onPatientChange">
                        <el-option v-for="item in patientList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="血压(mmHg)" prop="blood">
                    <el-input v-model="data.form.blood" autocomplete="off" placeholder="血压(mmHg)" />
                </el-form-item>
                <el-form-item label="体温(℃)" prop="temp">
                    <el-input-number v-model="data.form.temp" :min="1" :max="100" placeholder="体温" />
                </el-form-item>
                <el-form-item label="脉搏(次/分钟)" prop="pulse">
                    <el-input-number v-model="data.form.pulse" :min="1" :max="100" placeholder="脉搏" />
                </el-form-item>
                <el-form-item label="呼吸(次/分钟)" prop="breathe">
                    <el-input-number v-model="data.form.breathe" :min="1" :max="100" placeholder="呼吸" />
                </el-form-item>
                <el-form-item label="精神状态" prop="mental">
                    <el-select v-model="data.form.mental" placeholder="请选择精神状态">
                        <el-option value="正常">正常</el-option>
                        <el-option value="良好">良好</el-option>
                        <el-option value="较差">较差</el-option>
                        <el-option value="很差">很差</el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="饮食状态" prop="dietary">
                    <el-select v-model="data.form.dietary" placeholder="请选择饮食状态">
                        <el-option value="正常">正常</el-option>
                        <el-option value="良好">良好</el-option>
                        <el-option value="较差">较差</el-option>
                        <el-option value="很差">很差</el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="记录日期" prop="record">
                    <el-date-picker v-model="data.form.record" type="date" placeholder="请选择记录日期"
                        value-format="YYYY-MM-DD" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="data.form.remark" autocomplete="off" placeholder="请输入介绍" :row="2"
                        type="textarea" />
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
import { Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { onMounted, reactive, ref, computed } from 'vue';
import { useUserStore } from '@/stores/useUserStore';
import request from '@/utils/request'

const userStore = useUserStore()

// 表格按角色过滤：ADMIN 全看，NURSE 只看自己负责的预约，PATIENT 只看自己的预约
const tableDataFiltered = computed(() => pageStore.tableData || [])

const data = reactive({
    formVisible: false,
    form: '',
    ids: [],
    user: { ...userStore.userInfo },
    rules: {
        patientId: [
            { required: true, message: "请选择患者", trigger: 'change' }
        ],
        blood: [
            { required: true, message: "请输入血压（例如 80-120）", trigger: 'blur' },
        ],
        temp: [
            { required: true, type: 'number', message: "请输入体温", trigger: 'change' },
        ],
        pulse: [
            { required: true, type: 'number', message: "请输入脉搏", trigger: 'change' },
        ],
        breathe: [
            { required: true, type: 'number', message: "请输入呼吸", trigger: 'change' },
        ],
        mental: [
            { required: true, message: "请选择精神状态", trigger: 'change' }
        ],
        dietary: [
            { required: true, message: "请选择饮食状态", trigger: 'change' }
        ],
        record: [
            { required: true, message: "请选择记录日期", trigger: 'change' }
        ],
        remark: [
            { required: false },
            { min: 0, max: 200, message: '备注最多 200 字', trigger: 'blur' }
        ]
    }
})

const pageStore = usePageStore();

const handleSelect = () => {
    pageStore.pageNum = 1;
    pageStore.loadData();
}

const reset = () => {
    pageStore.name = '';
    pageStore.nurseName = '';
    pageStore.record = '';
    pageStore.pageNum = 1;
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
    data.form.nurseId = data.form.nurseId || userStore.userInfo?.id
    request.post('/dailyMonitor/add', data.form).then(res => {
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
    request.put('/dailyMonitor/update', data.form).then(res => {
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
        request.delete('/dailyMonitor/deleteById/' + id).then(res => {
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
        request.delete('/dailyMonitor/deleteBatch', { data: data.ids }).then(res => {
            if (res.code === '200') {
                ElMessage.success('操作成功')
                pageStore.loadData() // 删除后一定要重新加载最新的数据
            } else {
                ElMessage.error(res.msg)
            }
        })
    }).catch()
}

const patientList = ref([])

//某护工正在服务中的患者（接口：/nurseReserve/patientsInService/{nurseId}）
const getPatientsInService = (nurseId) => {
    request.get('/nurseReserve/patientsInService/' + nurseId).then(res => {
        if (res.code === '200') {
            // 兼容不同命名（id/name 或 patientId/patientName）
            patientList.value = (res.data || []).map(p => ({
                ...p,
                id: p.id ?? p.patientId,
                name: p.name ?? p.patientName
            }))
        } else {
            patientList.value = []
        }
    }).catch(() => { patientList.value = [] })
}

const onPatientChange = (patientId) => {
    if (!patientId) {
        data.form.wardId = null
        data.form.bedId = null
        return
    }
    // 调整为你后端实际接口
    request.get('/hp/selectByPatientId/' + patientId).then(res => {
        const hp = Array.isArray(res.data) ? res.data[0] : res.data
        if (res && res.code === '200' && hp) {
            data.form.bedId = hp.id ?? hp.bedId
            data.form.wardId = hp.ward_id ?? hp.wardId
        }
    }).catch(() => {
        data.form.bedId = null
        data.form.wardId = null
    })
}

// 组件挂载时加载初始数据
onMounted(() => {
    pageStore.setType('dailyMonitor');
    pageStore.loadData();
    const nurseId = userStore.userInfo?.id
    if (nurseId) getPatientsInService(nurseId)
});

</script>

<style>
.card {
    margin-bottom: 5px;
}
</style>
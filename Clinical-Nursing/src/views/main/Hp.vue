<template>
    <div>
        <div class="card" v-if="data.user.role !== 'PATIENT'">
            <el-input v-model="pageStore.name" style="width: 250px; margin-right: 10px;"
                placeholder="请输入患者名称查询"></el-input>
            <el-button type="primary" @click="pageStore.setName(pageStore.name)">查询</el-button>
            <el-button type="info" @click="reset" style="margin-left: 10px;">重置</el-button>
        </div>
        <div class="card" v-if="data.user.role === 'ADMIN'">
            <el-button type="primary" @click="handleAdd">新增</el-button>
            <el-button type="info" @click="delBatch" style="margin-left: 10px;">批量删除</el-button>
        </div>
        <div class="card">
            <el-table script :data="tableDataFiltered" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" v-if="data.user.role === 'ADMIN'"></el-table-column>
                <el-table-column label="患者名称" prop="patientName" />
                <el-table-column label="房间名称" prop="wardName" />
                <el-table-column label="床号" prop="bedNum" />
                <el-table-column label="病因" prop="illness" />
                <el-table-column label="病情概述" prop="illnessDesc" show-overflow-tooltip />
                <el-table-column label="治疗方案" prop="treatment" show-overflow-tooltip />
                <el-table-column label="住院状态" prop="status">
                    <template #default="scope">
                        <el-tag type="danger" v-if="scope.row.status === '住院中'">住院中</el-tag>
                        <el-tag type="success" v-if="scope.row.status === '已出院'">已出院</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="入院日期" prop="admitDate" />
                <el-table-column label="出院日期" prop="dischargeDate" />
                <el-table-column label="联系电话" prop="patientPhone" show-overflow-tooltip />
                <el-table-column label="紧急联系人" prop="emergencyContact" show-overflow-tooltip />
                <el-table-column label="紧急联系电话" prop="emergencyPhone" show-overflow-tooltip />
                <el-table-column label="操作" min-width="120" fixed="right" v-if="data.user.role !== 'NURSE'">
                    <template #default="scope">
                        <el-button type="primary" @click="handleUpdate(scope.row)" size="small" plain
                            v-if="data.user.role === 'ADMIN'">编辑</el-button>
                        <el-button type="danger" @click="handleDelete(scope.row.id)" size="small" plain
                            v-if="data.user.role === 'ADMIN'">删除</el-button>
                        <el-button type="primary" @click="handleUpdate(scope.row)" plain size="small"
                            v-if="data.user.role === 'PATIENT'">补充信息</el-button>
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
                <el-form-item label="患者名称" prop="patientId">
                    <el-select v-model="data.form.patientId" placeholder="请选择患者" @change="getPatientPhone">
                        <el-option v-for="item in patientList" :key="item.id" :label="item.name"
                            :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="房间名称" prop="wardId">
                    <el-select v-model="data.form.wardId" placeholder="请选择房间">
                        <el-option v-for="item in wardList" :key="item.id" :label="item.name"
                            :value="item.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="床号" prop="bedNum">
                    <el-input v-model="data.form.bedNum" autocomplete="off" placeholder="请输入床号"></el-input>
                </el-form-item>
                <el-form-item label="病因" prop="illness">
                    <el-input v-model="data.form.illness" autocomplete="off" placeholder="请输入病因"></el-input>
                </el-form-item>
                <el-form-item label="病情概述" prop="illnessDesc">
                    <el-input v-model="data.form.illnessDesc" autocomplete="off" placeholder="请输入" :rows="2"
                        type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="治疗方案" prop="treatment">
                    <el-input v-model="data.form.treatment" autocomplete="off" placeholder="请输入" :rows="2"
                        type="textarea"></el-input>
                </el-form-item>
                <el-form-item label="住院状态" prop="status">
                    <el-select v-model="data.form.status" placeholder="请选择病因">
                        <el-option value="住院中">住院中</el-option>
                        <el-option value="已出院">已出院</el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="入院日期" prop="admitDate">
                    <el-date-picker v-model="data.form.admitDate" type="date" placeholder="请选择入院日期"
                        value-format="YYYY-MM-DD" style="width: 100%;"></el-date-picker>
                </el-form-item>
                <el-form-item label="出院日期" prop="dischargeDate">
                    <el-date-picker v-model="data.form.dischargeDate" type="date" placeholder="请选择出院日期"
                        value-format="YYYY-MM-DD" style="width: 100%;"></el-date-picker>
                </el-form-item>
                <el-form-item label="联系电话" v-if="data.user.role === 'PATIENT'">
                    <el-input v-model="data.form.patientPhone" disabled placeholder="自动获取患者手机号"></el-input>
                </el-form-item>
                <el-form-item label="紧急联系人" v-if="data.user.role === 'PATIENT'">
                    <el-input v-model="data.form.emergencyContact" autocomplete="off" placeholder="请输入病因"></el-input>
                </el-form-item>
                <el-form-item label="紧急联系电话" v-if="data.user.role === 'PATIENT'">
                    <el-input v-model="data.form.emergencyPhone" autocomplete="off" placeholder="请输入病因"></el-input>
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
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/stores/useUserStore';
import request from '@/utils/request';


const pageStore = usePageStore()
const userStore = useUserStore()

// 表格按角色过滤
const tableDataFiltered = computed(() => pageStore.tableData || [])

const data = reactive({
    formVisible: false,
    form: '',
    ids: [],
    user: { ...userStore.userInfo },
    rules: {
        // 表单校验规则示例（在data.rules中添加）
        patientId: [
            { required: true, message: "请选择患者名称", trigger: 'change' }
        ],
        wardId: [
            { required: true, message: "请选择房间名称", trigger: 'change' }
        ],
        bedNum: [
            { required: true, message: "请选择床号", trigger: 'change' }
        ],
        illness: [
            { required: true, message: "请输入病因", trigger: 'blur' }
        ],
        illnessDesc: [
            { required: true, message: "请输入病情概述", trigger: 'blur' }
        ],
        treatment: [
            { required: true, message: "请输入治疗方案", trigger: 'blur' }
        ],
        status: [
            { required: true, message: "请选择住院状态", trigger: 'change' }
        ],
        admitDate: [{ required: true, message: '请选择入院日期', trigger: 'change' }],
        dischargeDate: [
            { required: false, message: '请选择出院日期', trigger: 'change' },
            {
                validator: (rule, value, callback) => {
                    if (value && data.form.admitDate) {
                        if (new Date(value) < new Date(data.form.admitDate)) {
                            callback(new Error('出院日期不能早于入院日期'));
                        } else {
                            callback();
                        }
                    } else {
                        callback();
                    }
                },
                trigger: 'change'
            }
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
    request.post('/hp/add', data.form).then(res => {
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
    request.put('/hp/update', data.form).then(res => {
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
    data.form = { ...row }
    data.formVisible = true
}

const handleDelete = (id) => {
    ElMessageBox.confirm('删除后的数据无法恢复，您确认删除吗', '删除确认', { type: 'warning' }).then(() => {
        request.delete('/hp/deleteById/' + id).then(res => {
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
        request.delete('/hp/delBatch', { data: data.ids }).then(res => {
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

//选择患者后，自动回填手机号
const getPatientPhone = () => {
    const selectedPatient = patientList.value.find(item => item.id === data.form.patientId);
    if (selectedPatient) {
        data.form.patientPhone = selectedPatient.phone; // 从患者列表中取手机号
    }
}

//获取病房列表
const wardList = ref([]);
const getWardList = () => {
    request.get('/ward/selectAll').then(res => {
        if (res.code === '200') {
            wardList.value = res.data;
        }
    });
}

onMounted(() => {
    pageStore.setType('hp');
    pageStore.loadData();
    getPatientList();
    getWardList();
})
</script>

<style>
.card {
    margin-bottom: 5px;
}
</style>
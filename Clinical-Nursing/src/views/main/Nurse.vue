<template>
    <div class="card">
        <el-input v-model="pageStore.name" style="width: 250px;margin-right: 10px;" placeholder="请输入护工名称查询"
            @keyup.enter="pageStore.setName(pageStore.name)"></el-input>
        <el-button type="primary" @click="pageStore.setName(pageStore.name)">查询</el-button>
        <el-button type="info" style="margin-left: 10px;" @click="reset">重置</el-button>
    </div>
    <div>
        <el-row :gutter="10" :data="pageStore.tableData">
            <el-col :span="6" v-for="nurse in pageStore.tableData" :key="nurse.id">
                <el-card shadow="hover" style="margin-bottom: 5px;">
                    <div style="text-align:center;">
                        <img :src=nurse.avatar style="width:100%;height:350px;object-fit:cover;border-radius:6px;" />
                    </div>
                    <div style="padding: 10px 0;">
                        <div style="display:flex;justify-content:space-between;align-items:center;">
                            <div style="font-weight:500;font-size: 20px;">{{ nurse.name }}</div>
                            <div style="font-size:15px;">从业{{ nurse.experience }}年 · {{ nurse.sex }} · {{
                                nurse.age
                            }}岁</div>
                        </div>
                        <div style="color:#666;margin:8px 0;">{{ nurse.speciality }}</div>
                        <div>
                            <el-button type="success" @click="viewEval(nurse)">查看评价</el-button>
                            <el-button type="primary" @click="reserve(nurse)">预约</el-button>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
    <el-dialog :title="currentNurse ? `评价 - ${currentNurse.name}` : '评价'" v-model="data.evalDialogVisible" width="600">
        <div v-if="!evals.length" style="text-align:center;padding:20px;color:#999;">暂无评价</div>
        <div v-else class="eval-list">
            <div class="eval-item" v-for="(evaluation, idx) in evals" :key="idx">
                <img class="eval-avatar" :src="evaluation.avatar" />
                <div class="eval-body">
                    <div class="eval-head">
                        <div class="eval-name">{{ evaluation.name || '匿名' }}</div>
                        <el-rate :model-value="Number(evaluation.rating)" disabled allow-half />
                        <div class="eval-time">{{ evaluation.evaluationTime }}</div>
                    </div>
                    <div class="eval-content">{{ evaluation.comment }}</div>
                </div>
            </div>
        </div>
        <template #footer>
            <el-button @click="data.evalDialogVisible = false">关闭</el-button>
        </template>
    </el-dialog>
    <el-dialog title="预约时间" v-model="data.reserveDialogVisible" destory-on-close width="750">
        <el-form ref="reserveFormRef" :rules="data.rules" :model="data.form" label-width="80px"
            style="margin-left: 40px;padding-top: 20px;">
            <el-form-item label="开始日期" prop="startDate">
                <el-date-picker v-model="data.form.startDate" type="date" placeholder="开始日期" value-format="YYYY-MM-DD"
                    style="width: 100%;"></el-date-picker>
            </el-form-item>
            <el-form-item label="结束日期" prop="endDate">
                <el-date-picker v-model="data.form.endDate" type="date" placeholder="结束日期" value-format="YYYY-MM-DD"
                    style="width: 100%;"></el-date-picker>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="data.reserveDialogVisible = false">关闭</el-button>
            <el-button type="primary" @click="saveReserve">确定预约</el-button>
        </template>
    </el-dialog>
    <div class="card">
        <el-pagination @current-change="pageStore.setPageNum" @size-change="pageStore.setPageSize"
            v-model:current-page="pageStore.pageNum" v-model:page-size="pageStore.pageSize"
            layout="total, prev, pager, next" :total="pageStore.total"></el-pagination>
    </div>
</template>

<script setup>

import { usePageStore } from '@/stores/usePageStore';
import { onMounted, reactive, ref } from 'vue';
import request from '@/utils/request';
import { useUserStore } from '@/stores/useUserStore';

const data = reactive({
    evalDialogVisible: false,
    reserveDialogVisible: false,
    form: {},
    rules: {
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [
            { required: false, message: '请选择结束日期', trigger: 'change' },
            {
                validator: (rule, value, callback) => {
                    if (value && data.form.startDate) {
                        if (new Date(value) < new Date(data.form.startDate)) {
                            callback(new Error('结束日期不能早于开始日期'));
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

const userStore = useUserStore();
const pageStore = usePageStore();

const reset = () => {
    pageStore.name = '';
    pageStore.loadData();
}

// 新增：评价弹窗状态与数据
const evals = ref([])
const currentNurse = ref(null)

const viewEval = async (nurse) => {
    currentNurse.value = nurse
    data.evalDialogVisible = true
    try {
        const res = await request.get('/evaluation/selectAll', {
            params: { nurseName: nurse.name }
        });
        const list = Array.isArray(res.data) ? res.data : [];
        // 初步映射，保留 patient_id 以便后续补充头像
        let mapped = list.map(item => ({
            patient_id: item.patientId,
            avatar: item.avatar,
            name: item.patientName,
            rating: item.rating,
            comment: item.comment,
            evaluationTime: item.evaluationTime
        }));
        // 筛选需要补充头像的患者ID（去重处理，避免重复请求）
        const idsToFetch = [...new Set(mapped.filter(m => !m.avatar && m.patient_id).map(m => m.patient_id))];
        if (idsToFetch.length) {
            await Promise.all(idsToFetch.map(async id => {
                try {
                    // 使用 path 参数形式，Controller 是 /selectById/{id}
                    const pr = await request.get(`/patient/selectById/${id}`);
                    const pd = pr.data
                    const avatarFromPatient = pd.avatar
                    // 7. 若获取到头像，更新到对应的评价数据中
                    if (avatarFromPatient) {
                        mapped.forEach(m => {
                            if (String(m.patient_id) === String(id) && !m.avatar) {
                                // 若返回相对路径，可加 baseUrl；这里优先保留后端给的完整 URL
                                m.avatar = avatarFromPatient
                            }
                        });
                    }
                } catch (e) {
                    console.warn('获取患者头像失败', id, e);
                }
            }));
        }
        // 最终赋值到 evals，使用你的模板字段名并兜底默认头像
        evals.value = mapped.map(m => ({
            avatar: m.avatar,
            name: m.name,
            rating: m.rating,
            comment: m.comment,
            evaluationTime: m.evaluationTime
        }));
    } catch (e) {
        console.error('获取评价失败', e)
        evals.value = []
    }
}

const reserve = async (nurse) => {
    // 检查登录用户
    const patientId = userStore.userInfo?.id;
    if (!patientId) {
        ElMessage.warning('请先登录再预约');
        return;
    }
    try {
        const res = await request.get(`/hp/selectByPatientId/${patientId}`);
        const raw = res?.data;
        // 兼容后端返回数组或单个对象
        let hpRecord = null;
        if (Array.isArray(raw)) {
            hpRecord = raw.find(item => {
                const idCandidates = [item.patientId, item.patient_id, item.patientid, item.id];
                return idCandidates.some(v => String(v ?? '') === String(patientId));
            }) || null;
        } else {
            hpRecord = raw || null;
        }

        // 兼容不同状态字段名，并做 trim 比较
        const statusVal = (hpRecord?.status ?? hpRecord?.state ?? '').toString().trim();
        if (!hpRecord || statusVal !== '住院中') {
            ElMessage.warning('当前患者非住院状态，无法预约护工');
            return;
        }

        // 保存入院日期到表单，供后续校验使用（兼容 admitDate / admit_date）
        data.form = {
            nurse_id: nurse?.id,
            patient_id: Number(patientId),
            status: '待审核',
            admitDate: hpRecord?.admitDate || hpRecord?.admit_date || hpRecord?.admit_date_str || null
        };
    } catch (e) {
        ElMessage.error('获取住院信息失败，无法预约护工');
        return;
    }
    try {
        const rr = await request.get(`/nurseReserve/selectByPatientId/${patientId}`);
        // console.log('/nurseReserve response', rr);
        const rawList = rr?.data;
        // 兼容后端返回数组或单个对象
        const reserves = Array.isArray(rawList) ? rawList : (rawList ? [rawList] : []);
        const blocked = reserves.some(item => {
            const statusVal = String((item?.status ?? item?.state ?? '')).trim();
            return statusVal === '服务中' || statusVal === '待审核';
        });
        if (blocked) {
            ElMessage.warning('当前患者已有进行中或待审核的护工服务，无法再次预约');
            return;
        }
    } catch (e) {
        ElMessage.error('无法验证历史预约状态，无法预约');
        return;
    }
    data.reserveDialogVisible = true;
}

const reserveFormRef = ref(null);

const saveReserve = () => {
    // 先触发表单校验，校验失败则阻止提交
    reserveFormRef.value?.validate((valid) => {
        if (!valid) {
            ElMessage.error('表单校验未通过，请检查输入项');
            return;
        }
        // 确保必须字段存在
        if (!data.form.nurse_id) {
            ElMessage.error('未选择护工，无法预约');
            return;
        }
        // 验证：预约开始日期不能早于入院日期
        if (data.form.admitDate && data.form.startDate) {
            const admit = new Date(data.form.admitDate);
            const start = new Date(data.form.startDate);
            // 将时间归零再比较，保证只按日期比较
            admit.setHours(0,0,0,0);
            start.setHours(0,0,0,0);
            if (start < admit) {
                ElMessage.error('开始日期必须在入院日期之后或当天');
                return;
            }
        }

        // 转换数值字段并补充 status（以防用户被篡改）
        data.form.patient_id = Number(data.form.patient_id);
        data.form.nurse_id = Number(data.form.nurse_id);
        data.form.status = data.form.status || '待审核';
        const payload = {
            ...data.form,
            patientId: data.form.patient_id,
            nurseId: data.form.nurse_id
        };
        request.post('/nurseReserve/add', payload).then(res => {
            if (res.code === '200') {
                data.reserveDialogVisible = false;
                ElMessage.success('预约已提交，待审核');
                pageStore.loadData();
            } else {
                ElMessage.error(res.msg || '预约提交失败');
            }
        }).catch(e => {
            console.error('预约接口错误', e);
            ElMessage.error('预约提交失败');
        });
    });
}

onMounted(() => {
    pageStore.setType('nurse');
    pageStore.setPageSize(8)
    pageStore.loadData();
});

</script>
<style scoped>
.card {
    margin-bottom: 5px;
}

.nurse-card {
    border-radius: 8px;
    overflow: hidden;
}

.eval-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    max-height: 60vh;
    overflow: auto;
    padding: 6px 4px;
}

.eval-item {
    display: flex;
    gap: 12px;
    align-items: flex-start;
    padding-bottom: 8px;
    border-bottom: 1px solid #f0f0f0;
}

.eval-avatar {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    object-fit: cover;
    margin-right: 10px;
}

.eval-body {
    flex: 1;
}

.eval-head {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 6px;
}

.eval-name {
    font-weight: 600;
    color: #333;
}

.eval-time {
    margin-left: auto;
    color: #999;
    font-size: 12px;
}

.eval-content {
    color: #444;
    line-height: 1.6;
    white-space: pre-wrap;
}
</style>
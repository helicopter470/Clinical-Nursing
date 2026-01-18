<template>
    <div class="card">
        <el-input v-model="pageStore.name" style="width: 250px;margin-right: 10px;" placeholder="请输入护理服务名称查询"
            @keyup.enter="pageStore.setName(pageStore.name)"></el-input>
        <el-button type="primary" @click="pageStore.setName(pageStore.name)">查询</el-button>
        <el-button type="info" style="margin-left: 10px;" @click="reset">重置</el-button>
    </div>
    <div>
        <el-row :gutter="10" :data="pageStore.tableData">
            <el-col :span="8" v-for="service in pageStore.tableData" :key="service.id">
                <el-card class="service-card" shadow="hover">
                    <div class="img-wrap">
                        <img :src="service.picture" alt="" />
                    </div>
                    <div class="card-body">
                        <div class="title">{{ service.name }}</div>
                        <el-tooltip :content="service.introduction" placement="top" effect="dark"
                            :disabled="!(service.introduction && service.introduction.length > 40)">
                            <div class="intro">{{ service.introduction }}</div>
                        </el-tooltip>
                        <div class="foot">
                            <div class="price">￥ {{ service.price }}<span class="unit">/天</span></div>
                            <el-button type="primary" @click="reserve(service)">预约</el-button>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
    <div class="card">
        <el-pagination @current-change="pageStore.setPageNum" @size-change="pageStore.setPageSize"
            v-model:current-page="pageStore.pageNum" v-model:page-size="pageStore.pageSize"
            layout="total, prev, pager, next" :total="pageStore.total"></el-pagination>
    </div>
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

    <!-- 支付确认弹窗 -->
    <el-dialog title="请您支付" v-model="data.payDialogVisible" width="420px" :close-on-click-modal="false">
        <div style="padding: 12px 20px;">
            <p>请支付总金额： <strong style="font-size:18px;color:#e74c3c">￥ {{ data.payTotal }}</strong></p>
            <el-input v-model="data.payConfirm" placeholder="请输入总金额以确认支付" clearable style="margin-top:12px;" />
        </div>
        <template #footer>
            <el-button @click="data.payDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmPay">确认支付</el-button>
        </template>
    </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { usePageStore } from '@/stores/usePageStore';
import { useUserStore } from '@/stores/useUserStore';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';

const data = reactive({
    evalDialogVisible: false,
    reserveDialogVisible: false,
    // 支付相关
    payDialogVisible: false,
    payTotal: 0,
    payConfirm: '',
    pendingReservePayload: null,
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

const pageStore = usePageStore();
const userStore = useUserStore();
const reserveFormRef = ref(null);

const reset = () => {
    pageStore.name = '';
    pageStore.loadData();
}

// 点击预约：先校验当前患者在 nursereserve 表中是否有 status 为 "服务中" 的记录
const reserve = async (service) => {
    const patientId = userStore.userInfo?.id;
    if (!patientId) {
        ElMessage.warning('请先登录再预约');
        return;
    }
    try {
        // 护工预约查询，兼容后端返回数组或单个对象
        const resN = await request.get(`/nurseReserve/selectByPatientId/${patientId}`);
        const rawN = resN?.data;
        const nurseList = Array.isArray(rawN) ? rawN : (rawN ? [rawN] : []);
        // 判断是否存在待审核
        const hasPendingNurse = nurseList.some(i => {
            const statusVal = String((i?.status ?? i?.state ?? '')).trim();
            return statusVal === '待审核';
        });
        if (hasPendingNurse) {
            ElMessage.warning('已有护工预约处于待审核，无法预约护理服务');
            return;
        }
        // 找到服务中的护工记录
        const active = nurseList.find(i => {
            const statusVal = String((i?.status ?? i?.state ?? '')).trim();
            return statusVal === '服务中';
        });
        if (!active) {
            ElMessage.warning('请先预约护工并处于 "服务中" 后才能预约护理服务');
            return;
        }

        // 额外检查：患者是否已有正在进行的护理服务（避免重复预约）
        const resS = await request.get(`/serviceReserve/selectByPatientId/${patientId}`);
        const rawS = resS?.data;
        const svcList = Array.isArray(rawS) ? rawS : (rawS ? [rawS] : []);
        const hasActiveService = svcList.some(i => String((i?.status ?? '')).trim() === '服务中');
        if (hasActiveService) {
            ElMessage.warning('您已有正在服务中的护理服务，无法重复预约');
            return;
        }

        // 尽量兼容不同命名的护工 id 字段
        const activeNurseId = active?.nurseId ?? active?.nurse_id ?? active?.id;
        if (!activeNurseId) {
            ElMessage.warning('未找到正在服务的护工信息，无法预约');
            return;
        }

        // 记录护工预约开始/结束日期供后续校验（兼容命名 startDate/start_date, endDate/end_date）
        const nurseStart = active?.startDate ?? active?.start_date ?? null;
        const nurseEnd = active?.endDate ?? active?.end_date ?? null;

        // 预填表单（保留 service.price 作为单价）
        const svcId = service?.id;
        if (!svcId) {
            ElMessage.error('未获取到护理服务ID，无法预约');
            return;
        }

        data.form = {
            service_id: Number(svcId),
            nurse_id: Number(activeNurseId),
            patient_id: Number(patientId),
            service_price: Number(service.price ?? 0),
            status: '待审核',
            nurseReserveStartDate: nurseStart,
            nurseReserveEndDate: nurseEnd
        };
        data.reserveDialogVisible = true;
    } catch (e) {
        console.error('检查护工/服务预约记录失败', e);
        ElMessage.error('无法验证预约状态，无法预约');
        return;
    }
}

const saveReserve = () => {
    reserveFormRef.value?.validate(async (valid) => {
        if (!valid) {
            ElMessage.error('表单校验未通过，请检查输入项');
            return;
        }
        if (!data.form.nurse_id) {
            ElMessage.error('未找到护工信息，无法预约');
            return;
        }
        if (!data.form.service_id) {
            ElMessage.error('未找到护理服务信息，无法预约');
            return;
        }

        // ---- 新增校验：服务开始/结束需在护工预约区间内（按日期比较，忽略时分） ----
        const nurseStartRaw = data.form.nurseReserveStartDate ?? data.form.nurse_reserve_start_date ?? null;
        const nurseEndRaw = data.form.nurseReserveEndDate ?? data.form.nurse_reserve_end_date ?? null;

        const svcStart = new Date(data.form.startDate); 
        svcStart.setHours(0,0,0,0);

        // 校验开始不能早于护工开始
        if (nurseStartRaw) {
            const nurseStart = new Date(nurseStartRaw); 
            nurseStart.setHours(0,0,0,0);
            if (svcStart < nurseStart) {
                ElMessage.error('服务开始日期不能早于护工预约开始日期');
                return;
            }
        }
        // 校验结束不能晚于护工结束（仅当护工有结束日期且用户填写了结束日期时）
        if (nurseEndRaw && data.form.endDate) {
            const nurseEnd = new Date(nurseEndRaw); 
            nurseEnd.setHours(0,0,0,0);
            const svcEnd = new Date(data.form.endDate); 
            svcEnd.setHours(0,0,0,0);
            if (svcEnd > nurseEnd) {
                ElMessage.error('服务结束日期不能晚于护工预约结束日期');
                return;
            }
        }
        // -----------------------------------------------------------------------

        // 计算天数（包含开始与结束），若未填写结束日期则视为 1 天
        let days = 1;
        try {
            if (data.form.startDate && data.form.endDate) {
                const s = new Date(data.form.startDate);
                const e = new Date(data.form.endDate);
                const diff = Math.floor((e - s) / (1000 * 60 * 60 * 24));
                days = diff >= 0 ? (diff + 1) : 1;
            }
        } catch (e) {
            days = 1;
        }

        // 获取服务单价（优先使用预存的 service_price，否则从 pageStore.tableData 中查）
        let unitPrice = Number(data.form.service_price ?? 0);
        if (!unitPrice) {
            const svc = pageStore.tableData?.find(it => Number(it.id) === Number(data.form.service_id));
            unitPrice = svc ? Number(svc.price || 0) : 0;
        }

        const total = Number(unitPrice) * Number(days);
        data.payTotal = total;
        data.payConfirm = '';
        // 构造待提交 payload（等待用户在支付弹窗确认）
        data.pendingReservePayload = {
            patientId: Number(data.form.patient_id),
            nurseId: Number(data.form.nurse_id),
            serviceId: Number(data.form.service_id),
            status: data.form.status || '待审核',
            startDate: data.form.startDate,
            endDate: data.form.endDate,
            price: total
        };
        // 显示支付弹窗
        data.payDialogVisible = true;
    });
}

const confirmPay = async () => {
    if (String(data.payConfirm).trim() === '') {
        ElMessage.warning('请输入确认金额');
        return;
    }
    const entered = Number(data.payConfirm);
    if (Number.isNaN(entered) || entered !== Number(data.payTotal)) {
        ElMessage.error('输入金额与总金额不一致');
        return;
    }
    if (!data.pendingReservePayload) {
        ElMessage.error('无待提交预约数据');
        return;
    }
    try {
        const res = await request.post('/serviceReserve/add', data.pendingReservePayload);
        if (res && (res.code === '200' || res.code === 200)) {
            data.payDialogVisible = false;
            data.reserveDialogVisible = false;
            ElMessage.success('预约并支付成功');
            pageStore.loadData();
            // 清理
            data.pendingReservePayload = null;
            data.payTotal = 0;
            data.payConfirm = '';
        } else {
            ElMessage.error(res?.msg || '预约提交失败');
        }
    } catch (e) {
        console.error('confirmPay error', e);
        ElMessage.error('提交预约失败');
    }
};

onMounted(() => {
    pageStore.setType('nurseService')
    pageStore.setPageSize(6);
})

</script>

<style>
.card {
    margin-bottom: 5px;
}

.service-card {
    border-radius: 8px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    height: 520px;
    /* 根据需要调整卡片高度 */
    padding: 0;
    margin-bottom: 5px;
}


.img-wrap {
    width: 100%;
    height: 320px;
    /* 图片显示高度 */
    overflow: hidden;
    background: #f5f5f5;
}

.img-wrap img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

/* 内容区 */
.card-body {
    padding: 14px;
    display: flex;
    flex-direction: column;
    flex: 1;
}

.title {
    font-weight: 600;
    font-size: 20px;
    margin-bottom: 8px;
}

.intro {
    color: #666;
    font-size: 14px;
    line-height: 1.6;
    flex: 1;
    margin-bottom: 12px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    /* 限制两行 */
    max-height: 3.2em;
    text-overflow: ellipsis;
    overflow: hidden;
}

/* 底部：价格 + 按钮 */
.foot {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.price {
    color: #e74c3c;
    font-size: 18px;
    font-weight: 700;
}

.price .unit {
    color: #e74c3c;
    font-size: 12px;
    margin-left: 4px;
}
</style>
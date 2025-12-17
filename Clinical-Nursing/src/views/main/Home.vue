<template>
    <div class="dashboard">
        <el-row :gutter="20" class="stats-row">
            <el-col :span="6" v-for="(s, idx) in stats" :key="idx">
                <el-card class="stat-card" shadow="hover">
                    <div class="stat-title">{{ s.title }}</div>
                    <div class="stat-value">{{ s.value }}</div>
                </el-card>
            </el-col>
        </el-row>
        <el-row :gutter="20" class="charts-row">
            <el-col :span="12">
                <el-card class="chart-card" shadow="hover">
                    <div class="chart-header"></div>
                    <div ref="barRef" class="chart" />
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="chart-card" shadow="hover">
                    <div class="chart-header"></div>
                    <div ref="pieRef" class="chart" />
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';
import request from '@/utils/request';

const stats = ref([
    { title: '患者数量', value: 0 },
    { title: '护工数量', value: 0 },
    { title: '护工预约次数', value: 0 },
    { title: '服务预约次数', value: 0 }
]);

const barRef = ref(null);
const pieRef = ref(null);
let barChart = null;
let pieChart = null;

const initBar = (names, values) => {
    if (!barRef.value) return;
    // 初始化ECharts实例：将ECharts绑定到指定的DOM容器上
    barChart = echarts.init(barRef.value);
    const colors = ['#2ecc71', '#3498db', '#f39c12', '#e74c3c', '#9b59b6', '#1abc9c', '#34495e'];
    const option = {
        title: { text: '护工预约量统计', left: 'center', top: 8 },
        tooltip: { trigger: 'item' },
        xAxis: { type: 'category', data: names, axisLabel: { rotate: 30 } },
        yAxis: { type: 'value' },
        series: [
            {
                type: 'bar',
                data: (values || []).map((v, idx) => ({
                    value: v,
                    itemStyle: { color: colors[idx % colors.length] }
                }))
            }
        ]
    };
    barChart.setOption(option);
};

const initPie = (data) => {
    if (!pieRef.value) return;
    // 初始化ECharts实例：将ECharts绑定到指定的DOM容器上
    pieChart = echarts.init(pieRef.value);
    const option = {
        title: { text: '服务预约量统计', left: 'center', top: 8 },
        tooltip: { trigger: 'item' },
        // - orient: 'vertical'：图例垂直排列
        // - left: 'left'：图例显示在饼图左侧，便于查看分类
        legend: { orient: 'vertical', left: 'left' },
        series: [
            {
                name: '预约',
                type: 'pie',
                radius: '50%',
                data,
                // emphasis：高亮配置（鼠标悬停到扇区时的样式）
                emphasis: {
                    itemStyle: {
                        shadowBlur: 10, // 阴影模糊度，增强立体感
                        shadowOffsetX: 0, // 阴影水平偏移量
                        shadowColor: 'rgba(0,0,0,0.3)' // 阴影颜色（半透明黑色）
                    }
                }
            }
        ]
    };
    pieChart.setOption(option);
};

const resize = () => {
    //浏览器窗口尺寸变化时，重新调整柱状图/饼图的大小，避免图表变形
    try { barChart && barChart.resize(); } catch (e) { }
    try { pieChart && pieChart.resize(); } catch (e) { }
};

onMounted(async () => {
    //绑定窗口resize事件：窗口大小变化时触发图表自适应
    window.addEventListener('resize', resize);

    try {
        // 后端实际路径
        const [patientsRes, nursesRes, nurseReserveRes, serviceReserveRes, nurseServiceRes] = await Promise.all([
            request.get('/patient/selectAll'),
            request.get('/nurse/selectAll'),
            request.get('/nurseReserve/selectAll'),
            request.get('/serviceReserve/selectAll'),
            request.get('/nurseService/selectAll'),
        ]);

        // 解析后端返回
        const patients = patientsRes.data;
        const nurses = nursesRes.data;
        const nurseReserves = nurseReserveRes.data;
        const serviceReserves = serviceReserveRes.data;
        const nurseServices = nurseServiceRes.data;

        stats.value[0].value = Array.isArray(patients) ? patients.length : 0;
        stats.value[1].value = Array.isArray(nurses) ? nurses.length : 0;
        stats.value[2].value = Array.isArray(nurseReserves) ? nurseReserves.length : 0;
        stats.value[3].value = Array.isArray(serviceReserves) ? serviceReserves.length : 0;

        // 构造护工预约柱状图：按 nurse_id 统计并取姓名（若 nurse 表有 name）
        // 初始化Map：用于存储「护工ID → 预约次数」的映射
        const nurseCountMap = new Map();
        //遍历护工预约记录（兼容空值，避免循环报错）
        for (const r of nurseReserves || []) {
            const nid = r.nurseId;
            //累计预约次数：存在则+1，不存在则初始化为1
            nurseCountMap.set(nid, (nurseCountMap.get(nid) || 0) + 1);
        }
        // 尝试用 nurses 列表里的 name 映射 id->name
        const idName = {};
        for (const n of nurses || []) idName[n.id] = n.name;

        //提取柱状图X轴名称（护工名）和Y轴数值（预约次数）
        const barNames = Array.from(nurseCountMap.keys()).map(id => idName[id]);
        const barValues = Array.from(nurseCountMap.values());

        initBar(barNames, barValues);

        // 饼图：按 service_id 统计，并用 nurseserver 表的 name 显示图例
        const serviceCountMap = new Map();
        for (const r of serviceReserves || []) {
            const sid = r.serviceId;
            serviceCountMap.set(sid, (serviceCountMap.get(sid) || 0) + 1);
        }
        // 构造 id->name 映射（nurseserver 表）
        const srvName = {};
        for (const s of nurseServices || []) srvName[s.id] = s.name;

        let pieData = Array.from(serviceCountMap.entries()).map(([id, value]) => ({
            name: srvName[id],
            value
        }));
        initPie(pieData);
    } catch (e) {
    }
});

onBeforeUnmount(() => {
    // 移除窗口resize事件监听：避免组件卸载后仍触发resize函数
    window.removeEventListener('resize', resize);
    // 销毁柱状图实例：释放ECharts占用的DOM/内存，容错处理
    try { barChart && barChart.dispose(); } catch (e) { }
    // 销毁饼图实例：释放资源
    try { pieChart && pieChart.dispose(); } catch (e) { }
});
</script>

<style>
.dashboard {
    padding: 16px;
}

.stats-row {
    margin-bottom: 16px;
}

.stat-card {
    text-align: left;
    min-height: 88px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.stat-title {
    color: #666;
    font-size: 14px;
    margin-bottom: 6px;
}

.stat-value {
    font-size: 24px;
    font-weight: 700;
    color: #333;
}

.charts-row .chart-card {
    min-height: 420px;
}

.chart-header {
    text-align: center;
    font-weight: 700;
    margin-bottom: 8px;
    color: #333;
}

.chart {
    width: 100%;
    height: 360px;
}
</style>
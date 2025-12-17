<template>
    <div class="card">
        <el-input v-model="pageStore.title" style="width: 250px;margin-right: 10px;" placeholder="请输入标题查询"
            @key-enter="pageStore.setTitle(pageStore.title)"></el-input>
        <el-button type="primary" @click="pageStore.setTitle(pageStore.title)">查询</el-button>
        <el-button type="info" @click="reset">重置</el-button>
    </div>
    <div>
        <el-row :gutter="10" :data="pageStore.tableData">
            <el-col :span="12" v-for="know in pageStore.tableData" :key="know.id">
                <el-card shadow="hover" class="know-card" @click="openDetail(know)">
                    <div class="know-inner">
                        <div class="know-thumb">
                            <img :src="know.picture" alt="封面" />
                        </div>
                        <div class="know-info">
                            <div class="know-title">{{ know.title }}</div>
                            <div class="know-introduction">{{ know.introduction }}</div>
                            <div class="know-meta">
                                <span class="meta-item">👁 {{ know.clickNum ?? 0 }}</span>
                                <span class="meta-item">📅 {{ know.time }}</span>
                            </div>
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
</template>

<script setup>

import { usePageStore } from '@/stores/usePageStore';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router'
import request from '@/utils/request';

const router = useRouter()
const pageStore = usePageStore();

const reset = () => {
    pageStore.title = '';
    pageStore.loadData();
}

const openDetail = async (know) => {
    if (!know || !know.id) return;
    // 生成会话存储的唯一标识key（用于标记该知识是否已被查看过），若本会话未标记过，则调用后端自增并乐观更新界面
    const flagKey = `knowledge_viewed_${know.id}`;
    if (!sessionStorage.getItem(flagKey)) {
        try {
            // 乐观更新显示
            if (typeof know.clickNum !== 'number') know.clickNum = Number(know.clickNum) || 0;
            know.clickNum++;
            // 异步通知后端自增
            await request.post(`/careKnowledge/incrementClick/${know.id}`);
            // 标记本会话已计数，避免重复自增
            sessionStorage.setItem(flagKey, '1');
        } catch (e) {
            console.warn('阅读量自增失败', e);
        }
    }
    // 跳转到详情
    router.push({ path: `/knowledge/${know.id}` });
}

onMounted(() => {
    pageStore.setType('careKnowledge');
    pageStore.setPageSize(6)
    pageStore.loadData();
})

</script>

<style>
.card {
    margin-bottom: 5px;
}

.know-card {
    padding: 12px;
    margin-bottom: 8px;
}

.know-inner {
    display: flex;
    gap: 16px;
    /* 垂直方向上靠顶部对齐（默认居中会导致图片/文字顶部不齐） */
    align-items: flex-start;
}

.know-thumb {
    flex: 0 0 160px;
}

.know-thumb img {
    width: 140px;
    height: 140px;
    /* 保持图片宽高比，裁剪超出部分以填满容器（避免图片拉伸变形） */
    object-fit: cover;
    border-radius: 6px;
}

.know-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
}

.know-title {
    font-size: 18px;
    font-weight: 600;
    color: #222;
    margin-bottom: 6px;
    /* 不换行 */
    white-space: nowrap;
    /* text-overflow: ellipsis + overflow: hidden：超出部分显示省略号（需配合单行/固定宽使用） */
    text-overflow: ellipsis;
    overflow: hidden;
}

.know-introduction {
    color: #666;
    line-height: 1.6;
    margin-bottom: 65px;
    max-height: 3.2em;
    /* 1.6 * 2 行，精准控制显示行数 */
    overflow: hidden;
    text-overflow: ellipsis;
}

.know-meta {
    color: #999;
    font-size: 13px;
    display: flex;
    gap: 12px;
    align-items: center;
}
</style>
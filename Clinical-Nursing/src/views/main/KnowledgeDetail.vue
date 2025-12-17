<template>
    <div class="detail-wrap" v-if="item">
        <el-card shadow="hover">
            <h2 class="title">{{ item.title }}</h2>
            <div class="meta">阅读量：{{ item.clickNum ?? 0 }} 发布时间：{{ item.time }}</div>
            <div class="content" v-html="item.content"></div>
        </el-card>
    </div>
    <div v-else style="text-align: center;padding: 40px;color: #999;">加载中......</div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue';
import request from '@/utils/request';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const item = ref(null);
const id = route.params.id;

// 离开详情页时移除本会话已查看标记，保证返回列表再次点击会重新计数
onBeforeUnmount(() => {
    try {
        sessionStorage.removeItem(`knowledge_viewed_${id}`);
        //console.log('[KnowledgeDetail] 清除会话标记', `knowledge_viewed_${id}`);
    } catch (e) {
        //console.warn('[KnowledgeDetail] 清除标记失败', e);
    }
});

onMounted(async () => {
    if (!id) return router.back()
    try {
        const res = await request.get(`/careKnowledge/selectById/${id}`) 
        item.value =  res.data || null
    } catch (e) {
        console.error('获取详情失败', e)
    }
})


</script>

<style scoped>
.detail-wrap {
    display: flex;
    justify-content: center;
}

.el-card {
    padding: 20px;
    max-width: 800px;
    margin: 20px;
}


.title {
    font-size: 22px;
    font-weight: 700;
    text-align: center;
    margin-bottom: 12px;
}

.meta {
    color: #999;
    text-align: center;
    margin-bottom: 18px;
}

.content {
    line-height: 1.8;
    color: #333;
    white-space: pre-line;
    /* 关键：识别\n换行符，自动换行 */
    line-height: 1.8;
    /* 可选：增加行间距，提升可读性 */
    margin: 0 30px;
}
</style>
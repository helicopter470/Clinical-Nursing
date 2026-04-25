<template>
  <div>
    <!-- 悬浮按钮：仅在登录后的主布局里挂载本组件，因此这里不再做登录判断 -->
    <div
      class="ai-fab"
      :style="fabStyle"
      @pointerdown="onFabPointerDown"
      @click="onFabClick"
      title="AI 医疗知识助手"
    >
      <el-icon size="22"><ChatDotRound /></el-icon>
    </div>

    <el-drawer v-model="open" size="420px" :with-header="false">
      <div class="ai-wrap">
        <div class="ai-header">
          <div class="ai-title">
            <div class="t1">AI 医疗知识助手</div>
          </div>
          <div class="ai-actions">
            <el-button size="small" @click="clearChat" plain>清空</el-button>
          </div>
        </div>

        <el-alert
          class="ai-alert"
          type="warning"
          show-icon
          :closable="false"
          title="免责声明"
          description="本功能仅用于健康科普与护理知识学习，不替代医生诊断与处方。出现紧急症状请立即就医。"
        />

        <div class="ai-quick">
          <el-button size="small" @click="sendQuick('发热怎么办？')">发热怎么办</el-button>
          <el-button size="small" @click="sendQuick('血压160/100严重吗？')">血压偏高</el-button>
          <el-button size="small" @click="sendQuick('糖尿病饮食注意什么？')">糖尿病饮食</el-button>
          <el-button size="small" @click="sendQuick('咳嗽气短需要注意什么？')">咳嗽气短</el-button>
        </div>

        <div ref="listRef" class="ai-list">
          <div v-if="messages.length === 0" class="ai-empty">
            你可以从上面的快捷问题开始，或直接输入你的问题。
          </div>

          <div v-for="m in messages" :key="m.id" class="msg" :class="m.role">
            <div class="bubble">
              <pre class="content">{{ m.content }}</pre>
              <div class="meta">{{ m.time }}</div>
            </div>
          </div>
        </div>

        <div class="ai-input">
          <el-input
            v-model="input"
            type="textarea"
            :rows="3"
            resize="none"
            placeholder="请输入你的问题（Enter 发送，Shift+Enter 换行）"
            @keydown="onKeydown"
          />
          <div class="ai-send">
            <el-button type="primary" :loading="sending" @click="send">发送</el-button>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, ref } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';
import { mockChat } from '@/utils/mockAi';

const open = ref(false);
const input = ref('');
const sending = ref(false);
const listRef = ref(null);

// 是否优先使用真实 AI（后端接口）
const USE_REAL_AI = true;
const aiModeText = computed(() => (USE_REAL_AI ? '在线（真实模型）' : '模拟回答'));

// 悬浮按钮位置（可拖拽）
// 说明：按需求“每次刷新页面回到原位”，因此不再使用 localStorage 持久化位置。
const defaultPos = { right: 22, bottom: 22 };
const fabPos = ref({ ...defaultPos });

function loadPos() {
  return { ...defaultPos };
}

function savePos() {
  // 不持久化
}

const fabStyle = computed(() => ({
  right: `${fabPos.value.right}px`,
  bottom: `${fabPos.value.bottom}px`
}));

// 拖拽状态
const dragging = ref(false);
let startX = 0;
let startY = 0;
let startRight = 0;
let startBottom = 0;
let pointerId = null;

const clamp = (n, min, max) => Math.max(min, Math.min(max, n));

const onFabPointerDown = (e) => {
  // 仅主/左键开始拖拽
  if (e.button !== undefined && e.button !== 0) return;

  dragging.value = false;
  startX = e.clientX;
  startY = e.clientY;
  startRight = fabPos.value.right;
  startBottom = fabPos.value.bottom;
  pointerId = e.pointerId;

  // 捕获指针，保证移出按钮也能继续拖拽
  try {
    e.currentTarget.setPointerCapture(pointerId);
  } catch {
    // ignore
  }

  window.addEventListener('pointermove', onPointerMove);
  window.addEventListener('pointerup', onPointerUp, { once: true });
};

const onPointerMove = (e) => {
  if (pointerId !== null && e.pointerId !== pointerId) return;

  const dx = e.clientX - startX;
  const dy = e.clientY - startY;

  // 超过阈值认为是拖拽（避免轻微抖动导致点击失效）
  if (!dragging.value && (Math.abs(dx) > 4 || Math.abs(dy) > 4)) {
    dragging.value = true;
  }

  if (!dragging.value) return;

  // right/bottom 随鼠标反向变化
  const newRight = startRight - dx;
  const newBottom = startBottom - dy;

  // 简单边界：不让按钮拖出屏幕（预留按钮直径 54px 和一些边距）
  const maxRight = window.innerWidth - 54 - 8;
  const maxBottom = window.innerHeight - 54 - 8;

  fabPos.value = {
    right: clamp(newRight, 8, maxRight),
    bottom: clamp(newBottom, 8, maxBottom)
  };
};

const onPointerUp = () => {
  window.removeEventListener('pointermove', onPointerMove);
  // 拖拽结束后持久化
  if (dragging.value) savePos();
  pointerId = null;
  // 下一帧再复位，避免 click 事件读取到旧状态
  setTimeout(() => {
    dragging.value = false;
  }, 0);
};

const onFabClick = () => {
  // 拖拽时不触发打开
  if (dragging.value) return;
  // 点击悬浮按钮：打开/关闭切换
  open.value = !open.value;
};

const messages = ref([]); // {id, role:'user'|'assistant', content, time}

// 限制 AI 每次回答字符数
const MAX_AI_CHARS = 100;
const clampAiAnswer = (s) => {
  const text = String(s ?? '');
  if (text.length <= MAX_AI_CHARS) return text;
  return text.slice(0, MAX_AI_CHARS) + '…';
};

const nowText = () => {
  const d = new Date();
  const h = String(d.getHours()).padStart(2, '0');
  const m = String(d.getMinutes()).padStart(2, '0');
  return `${h}:${m}`;
};

const scrollToBottom = async () => {
  await nextTick();
  const el = listRef.value;
  if (el) el.scrollTop = el.scrollHeight;
};

const pushMsg = async (role, content) => {
  messages.value.push({
    id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
    role,
    content,
    time: nowText()
  });
  await scrollToBottom();
};

const clearChat = () => {
  messages.value = [];
  input.value = '';
};

const sendQuick = async (text) => {
  open.value = true;
  input.value = text;
  await nextTick();
  send();
};

const send = async () => {
  const text = String(input.value || '').trim();
  if (!text) {
    ElMessage.warning('请输入问题');
    return;
  }
  if (sending.value) return;

  sending.value = true;
  input.value = '';

  await pushMsg('user', text);

  // 先放一个“思考中”占位
  const thinkingId = `${Date.now()}-thinking`;
  messages.value.push({ id: thinkingId, role: 'assistant', content: '正在思考中…', time: nowText() });
  await scrollToBottom();

  try {
    let answer = '';

    if (USE_REAL_AI) {
      // 发送给后端：/ai/chat
      const history = messages.value
        .filter((m) => m.role === 'user' || m.role === 'assistant')
        .slice(-12)
        .map((m) => ({ role: m.role, content: m.content }));

      const res = await request.post('/ai/chat', {
        message: text,
        history
      });

      if (res?.code === '200' && res?.data?.answer) {
        answer = res.data.answer;
      } else {
        // 失败则回退到 mock
        answer = await mockChat({ message: text, history });
      }
    } else {
      answer = await mockChat({ message: text, history: messages.value });
    }

    // 截断：每次回答最多 100 字符
    answer = clampAiAnswer(answer);

    const idx = messages.value.findIndex((m) => m.id === thinkingId);
    if (idx !== -1) {
      messages.value[idx] = { ...messages.value[idx], content: answer };
    } else {
      await pushMsg('assistant', answer);
    }
  } catch (e) {
    // 网络/后端异常时：也回退到 mock，保证演示可用
    try {
      const history = messages.value
        .filter((m) => m.role === 'user' || m.role === 'assistant')
        .slice(-12)
        .map((m) => ({ role: m.role, content: m.content }));

      let fallback = await mockChat({ message: text, history });
      fallback = clampAiAnswer(fallback);

      const idx = messages.value.findIndex((m) => m.id === thinkingId);
      if (idx !== -1) messages.value[idx] = { ...messages.value[idx], content: fallback };
      else await pushMsg('assistant', fallback);

      ElMessage.warning('后端AI不可用，已切换为模拟回答');
    } catch {
      const idx = messages.value.findIndex((m) => m.id === thinkingId);
      const errText = '对话失败，请稍后重试。';
      if (idx !== -1) messages.value[idx] = { ...messages.value[idx], content: errText };
      else await pushMsg('assistant', errText);
    }
  } finally {
    sending.value = false;
    await scrollToBottom();
  }
};

const onKeydown = (e) => {
  // Enter 发送，Shift+Enter 换行
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault();
    send();
  }
};

onBeforeUnmount(() => {
  window.removeEventListener('pointermove', onPointerMove);
});
</script>

<style scoped>
.ai-fab {
  position: fixed;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 22px rgba(64, 158, 255, 0.35);
  cursor: pointer;
  z-index: 9999;
  user-select: none;
  touch-action: none;
}
.ai-fab:hover {
  filter: brightness(0.98);
}

.ai-wrap {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.ai-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 2px 8px 2px;
}

.ai-title .t1 {
  font-size: 16px;
  font-weight: 800;
}
.ai-title .t2 {
  font-size: 12px;
  color: #888;
  margin-top: 2px;
}

.ai-alert {
  margin: 6px 0 10px 0;
}

.ai-quick {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.ai-list {
  flex: 1;
  overflow: auto;
  padding: 6px 2px;
  background: #fafafa;
  border: 1px solid #eee;
  border-radius: 8px;
}

.ai-empty {
  color: #888;
  font-size: 13px;
  padding: 14px;
}

.msg {
  display: flex;
  padding: 10px;
}

.msg.user {
  justify-content: flex-end;
}
.msg.assistant {
  justify-content: flex-start;
}

.bubble {
  max-width: 86%;
  border-radius: 10px;
  padding: 10px 10px 8px 10px;
  position: relative;
}

.msg.user .bubble {
  background: #e8f4ff;
  border: 1px solid #cfe6ff;
}

.msg.assistant .bubble {
  background: #fff;
  border: 1px solid #eee;
}

.content {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
  font-size: 13px;
  line-height: 1.6;
  color: #222;
}

.meta {
  margin-top: 6px;
  font-size: 12px;
  color: #999;
  text-align: right;
}

.ai-input {
  margin-top: 10px;
}

.ai-send {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>

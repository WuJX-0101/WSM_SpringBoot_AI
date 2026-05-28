<template>
  <div class="ai-chat">
    <PageHeader title="AI助手" />

    <el-card class="chat-card">
      <!-- 消息列表 -->
      <div class="message-list" ref="messageListRef">
        <div class="welcome-message" v-if="messages.length === 0">
          <el-icon><ChatDotRound /></el-icon>
          <h3>你好！我是WMS智能助手</h3>
          <p>我可以帮你查询库存、分析数据、回答仓储管理相关问题</p>
          <div class="quick-questions">
            <el-button v-for="q in quickQuestions" :key="q" @click="sendMessage(q)" text>
              {{ q }}
            </el-button>
          </div>
        </div>

        <div v-for="(msg, index) in messages" :key="index" :class="['message-item', msg.role]">
          <div class="avatar-wrapper">
            <el-avatar v-if="msg.role === 'user'" :size="40" icon="User" />
            <el-avatar v-else :size="40" class="ai-avatar">
              <el-icon><MagicStick /></el-icon>
            </el-avatar>
            <span v-if="msg.role === 'ai'" class="ai-name">WMS助手</span>
          </div>
          <div class="message-content">
            <div :class="['message-text', msg.role]" v-html="formatMessage(msg.content)"></div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>

        <div v-if="loading" class="message-item ai">
          <el-avatar :size="36" icon="MagicStick" class="ai-avatar" />
          <div class="message-content">
            <div class="message-text typing">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="inputText"
          placeholder="输入你的问题..."
          :disabled="loading"
          @keyup.enter="sendMessage()"
          size="large"
        >
          <template #append>
            <el-button @click="sendMessage()" :loading="loading" type="primary">
              发送
            </el-button>
          </template>
        </el-input>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { chat } from '@/api/ai'

interface Message {
  role: 'user' | 'ai'
  content: string
  time: string
}

const messages = ref<Message[]>([])
const inputText = ref('')
const loading = ref(false)
const messageListRef = ref<HTMLElement>()

const quickQuestions = [
  '当前库存情况如何？',
  '哪些商品需要补货？',
  '最近的入库情况？',
  '如何优化库存管理？'
]

const formatTime = () => {
  const now = new Date()
  return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
}

// 格式化消息内容（支持换行、加粗）
const formatMessage = (content: string) => {
  if (!content) return ''
  return content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

const sendMessage = async (text?: string) => {
  const question = text || inputText.value.trim()
  if (!question) return

  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: question,
    time: formatTime()
  })
  inputText.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    const res: any = await chat(question)
    // 添加AI回复
    messages.value.push({
      role: 'ai',
      content: res.data || '抱歉，我无法回答这个问题',
      time: formatTime()
    })
  } catch {
    messages.value.push({
      role: 'ai',
      content: '抱歉，服务暂时不可用，请稍后再试',
      time: formatTime()
    })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}
</script>

<style scoped>
.chat-card {
  height: calc(100vh - 180px);
  display: flex;
  flex-direction: column;
}

:deep(.el-card__body) {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 0;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.welcome-message {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.welcome-message .el-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 16px;
}

.welcome-message h3 {
  margin: 0 0 8px;
  color: #303133;
}

.quick-questions {
  margin-top: 24px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  align-items: flex-start;
}

.message-item.user {
  flex-direction: row-reverse;
}

.avatar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.ai-name {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.message-content {
  max-width: 70%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.8;
  word-break: break-word;
}

.message-text.user {
  background: #409eff;
  color: #ffffff;
  border-top-right-radius: 4px;
}

.message-text.ai {
  background: #f4f4f5;
  color: #303133;
  border-top-left-radius: 4px;
}

.message-text.ai :deep(strong) {
  color: #409eff;
  font-weight: 600;
}

.message-time {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 6px;
}

.message-item.user .message-time {
  text-align: right;
}

/* 打字动画 */
.typing {
  display: flex;
  gap: 6px;
  padding: 16px 20px;
}

.typing span {
  width: 8px;
  height: 8px;
  background: #909399;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-8px);
  }
}

.input-area {
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
}
</style>

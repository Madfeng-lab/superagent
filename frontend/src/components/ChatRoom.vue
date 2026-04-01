<template>
  <div class="relative h-[100dvh] overflow-hidden bg-slate-950 text-slate-100">
    <div
      aria-hidden="true"
      class="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_12%_14%,rgba(56,189,248,0.20),transparent_28%),radial-gradient(circle_at_88%_10%,rgba(167,139,250,0.22),transparent_30%)]"
    />
    <div class="relative mx-auto flex h-full w-full max-w-5xl flex-col px-3 py-3 sm:px-4 sm:py-4">
      <div class="flex h-full min-h-0 flex-col rounded-2xl border border-white/10 bg-white/10 backdrop-blur-xl">
        <header class="grid gap-1 border-b border-white/10 bg-white/5 px-4 py-3">
          <RouterLink to="/" class="w-fit text-sm text-cyan-300 hover:underline">← 返回</RouterLink>
          <h1 class="text-lg font-semibold tracking-tight sm:text-xl">{{ title }}</h1>
          <span v-if="sessionHint" class="text-xs text-slate-300">{{ sessionHint }}</span>
        </header>

        <div ref="scrollRef" class="flex-1 space-y-3 overflow-y-auto px-3 py-4 sm:px-4">
          <div
            v-for="(m, i) in messages"
            :key="i"
            class="flex w-full"
            :class="m.role === 'user' ? 'justify-end' : 'justify-start'"
          >
            <div
              class="relative max-w-[92%] rounded-2xl px-3 py-2 sm:max-w-[85%] sm:px-4 sm:py-3"
              :class="
                m.role === 'user'
                  ? 'rounded-br-md bg-cyan-500 text-white'
                  : 'rounded-bl-md border border-white/10 bg-slate-900/70 text-slate-100'
              "
            >
              <div
                v-if="m.stepTitle"
                class="mb-1 inline-flex rounded-full border border-indigo-300/30 bg-indigo-400/20 px-2 py-0.5 text-[11px] font-medium text-indigo-200"
              >
                {{ m.stepTitle }}
              </div>
              <pre class="m-0 whitespace-pre-wrap break-words font-sans text-sm leading-6">{{ getVisibleContent(m) }}</pre>
              <button
                v-if="m.stepTitle"
                type="button"
                class="mt-2 inline-flex text-xs text-cyan-300 hover:text-cyan-200"
                @click="toggleStepExpand(i)"
              >
                {{ m.collapsed ? '展开全部' : '收起' }}
              </button>
              <span v-if="m.streaming" class="ml-1 animate-pulse">▍</span>
            </div>
          </div>
        </div>

        <footer class="flex items-end gap-2 border-t border-white/10 bg-white/5 p-3 max-[520px]:flex-col max-[520px]:items-stretch">
          <textarea
            v-model="input"
            class="min-h-[46px] flex-1 resize-none rounded-xl border border-white/15 bg-slate-900/70 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-400 focus:border-cyan-400 focus:outline-none"
            rows="2"
            placeholder="输入消息…（Enter 发送，Shift+Enter 换行）"
            :disabled="sending"
            @keydown="onKeydown"
          />
          <button
            type="button"
            class="rounded-xl bg-cyan-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:opacity-50 max-[520px]:w-full"
            :disabled="sending || !input.trim()"
            @click="send"
          >
            {{ sending ? '生成中…' : '发送' }}
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { RouterLink } from 'vue-router'
import { apiUrl } from '../api/http'
import { readSseStream } from '../utils/sseStream'

const props = defineProps({
  title: { type: String, required: true },
  mode: { type: String, required: true },
  chatId: { type: String, default: '' }
})

const messages = ref([])
const input = ref('')
const sending = ref(false)
const scrollRef = ref(null)
let abort = null

const sessionHint = ref('')
const COLLAPSED_MAX_LINES = 6
const COLLAPSED_MAX_CHARS = 320

function scrollToBottom() {
  nextTick(() => {
    const el = scrollRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

watch(messages, () => scrollToBottom(), { deep: true })

onMounted(() => {
  if (props.mode === 'gym' && props.chatId) {
    sessionHint.value = `会话 ID：${props.chatId}`
  } else if (props.mode === 'manus') {
    sessionHint.value = '单次对话流式输出'
  }
})

onUnmounted(() => {
  abort?.abort()
})

function onKeydown(e) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    send()
  }
}

function buildStreamUrl(message) {
  const q = new URLSearchParams({ message })
  if (props.mode === 'gym') {
    q.set('chatId', props.chatId)
    return `${apiUrl('/ai/gym_app/chat/sse')}?${q.toString()}`
  }
  return `${apiUrl('/ai/manus/chat')}?${q.toString()}`
}

function toggleStepExpand(index) {
  const row = messages.value[index]
  if (!row || !row.stepTitle) return
  row.collapsed = !row.collapsed
}

function getVisibleContent(message) {
  if (!message.stepTitle || !message.collapsed) {
    return message.content
  }
  const lines = message.content.split('\n')
  if (lines.length <= COLLAPSED_MAX_LINES && message.content.length <= COLLAPSED_MAX_CHARS) {
    return message.content
  }
  const byLines = lines.slice(0, COLLAPSED_MAX_LINES).join('\n')
  const preview = byLines.length > COLLAPSED_MAX_CHARS ? byLines.slice(0, COLLAPSED_MAX_CHARS) : byLines
  return `${preview}\n...`
}

async function send() {
  const text = input.value.trim()
  if (!text || sending.value) return
  if (props.mode === 'gym' && !props.chatId) return

  abort?.abort()
  abort = new AbortController()

  const responseId = Date.now() + Math.random()
  let aiIndex = -1
  let lastManusIndex = -1

  messages.value.push({ role: 'user', content: text })
  if (props.mode === 'gym') {
    aiIndex = messages.value.length
    messages.value.push({ role: 'assistant', content: '', streaming: true, responseId })
  }
  input.value = ''
  sending.value = true

  const url = buildStreamUrl(text)

  try {
    await readSseStream(url, abort.signal, (chunk) => {
      if (props.mode === 'gym') {
        const row = messages.value[aiIndex]
        if (row) row.content += chunk
        return
      }
      lastManusIndex = appendManusStepChunk(chunk, responseId, lastManusIndex)
    })
  } catch (e) {
    const errorText = e.name === 'AbortError' ? '（已取消）' : `[错误] ${e.message || e}`
    if (props.mode === 'gym') {
      const row = messages.value[aiIndex]
      if (row) row.content += (row.content ? '\n' : '') + errorText
      return
    }
    if (lastManusIndex >= 0) {
      const row = messages.value[lastManusIndex]
      if (row) row.content += (row.content ? '\n' : '') + errorText
    } else {
      messages.value.push({ role: 'assistant', content: errorText, streaming: true, responseId })
    }
  } finally {
    for (const row of messages.value) {
      if (row.responseId === responseId) row.streaming = false
    }
    sending.value = false
    abort = null
  }
}

function appendManusStepChunk(chunk, responseId, lastIndex) {
  const parts = chunk.split(/(?=Step\s+\d+\s*:)/g).filter((p) => p.trim())
  if (!parts.length) return lastIndex

  let currentIndex = lastIndex
  for (const part of parts) {
    const text = part.trim()
    const step = text.match(/^Step\s+(\d+)\s*:\s*/i)
    if (step) {
      const content = text.replace(/^Step\s+\d+\s*:\s*/i, '').trim()
      messages.value.push({
        role: 'assistant',
        stepTitle: `Step ${step[1]}`,
        content: content || '...',
        collapsed: true,
        streaming: true,
        responseId
      })
      currentIndex = messages.value.length - 1
    } else if (currentIndex >= 0) {
      const row = messages.value[currentIndex]
      if (row) row.content += (row.content ? '\n\n' : '') + text
    } else {
      messages.value.push({ role: 'assistant', content: text, streaming: true, responseId })
      currentIndex = messages.value.length - 1
    }
  }
  return currentIndex
}
</script>

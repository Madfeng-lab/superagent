<template>
  <div class="h-full">
    <ChatRoom v-if="chatId" title="AI Gym 大师" mode="gym" :chat-id="chatId" />
    <p v-else class="m-0 p-8 text-center text-slate-300">正在创建会话…</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ChatRoom from '../components/ChatRoom.vue'

const chatId = ref('')

onMounted(() => {
  chatId.value = crypto.randomUUID()
})
</script>

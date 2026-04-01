import { http } from './http'

/** 同步对话（可选）；流式场景请使用 utils/sseStream + fetch */
export function gymChatSync(message, chatId) {
  return http.get('/ai/gym_app/chat/sync', {
    params: { message, chatId }
  })
}

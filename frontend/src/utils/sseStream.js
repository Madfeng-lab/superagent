/**
 * 使用 fetch 读取 Spring 返回的 text/event-stream，解析 SSE 事件并回调 data 片段。
 * @param {string} url 完整 URL（已含 query）
 * @param {AbortSignal} signal
 * @param {(chunk: string) => void} onData 每条 data 内容（可能多行合并）
 * @returns {Promise<void>}
 */
export async function readSseStream(url, signal, onData) {
  const res = await fetch(url, {
    method: 'GET',
    signal,
    headers: { Accept: 'text/event-stream' }
  })

  if (!res.ok) {
    const t = await res.text().catch(() => '')
    throw new Error(t || `HTTP ${res.status}`)
  }

  const reader = res.body?.getReader()
  if (!reader) {
    throw new Error('响应不支持流式读取')
  }

  const decoder = new TextDecoder()
  let buffer = ''

  while (true) {
    const { value, done } = await reader.read()
    if (done) break
    buffer += decoder.decode(value, { stream: true })
    buffer = consumeSseBuffer(buffer, onData)
  }

  buffer += decoder.decode()
  if (buffer.trim()) {
    consumeSseBuffer(buffer + '\n\n', onData)
  }
}

/**
 * 从 buffer 中取出完整 SSE 事件（以 \n\n 分隔），返回未处理完的尾部
 */
function consumeSseBuffer(buffer, onData) {
  const parts = buffer.split('\n\n')
  const rest = parts.pop() ?? ''

  for (const raw of parts) {
    const block = raw.trim()
    if (!block) continue

    const lines = block.split('\n')
    let data = ''
    for (const line of lines) {
      if (line.startsWith('data:')) {
        data += (data ? '\n' : '') + line.slice(5).trimStart()
      }
    }
    if (data) {
      onData(data)
    } else if (!lines.some((l) => l.startsWith(':'))) {
      onData(block)
    }
  }

  return rest
}

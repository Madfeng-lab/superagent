# SuperAgent

## 中文介绍

SuperAgent 是一个基于 Spring AI 的智能体应用项目，聚焦于健身与通用任务场景，包含：

- AI 健身对话应用（支持流式返回）
- 超级智能体（多步推理 + 工具调用）
- RAG 检索增强（本地文档/云知识库扩展）
- MCP 客户端集成
- 前后端分离（Vue3 + TailwindCSS）与 Docker 部署方案
- 图像搜索 MCP 子模块：`superagent-imgsearch-mcp-server`

### 快速启动（后端）

```bash
mvn spring-boot:run
```

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

### 测试

```bash
mvn test
```

### Docker 与发布

- 分离部署与打包说明：`docs/Docker部署说明.md`
- 一键生成上传包：
  - Windows: `.\scripts\package-upload.ps1`
  - Linux/macOS: `./scripts/package-upload.sh`

---

## English Introduction

SuperAgent is a Spring AI based agent application project focused on fitness and general task automation, featuring:

- AI gym chat application (with streaming responses)
- Super agent (multi-step reasoning + tool calling)
- RAG enhancement (local docs / cloud knowledge base extension)
- MCP client integration
- Frontend/backend split architecture (Vue3 + TailwindCSS) with Docker deployment
- Image-search MCP submodule: `superagent-imgsearch-mcp-server`

### Quick Start (Backend)

```bash
mvn spring-boot:run
```

### Frontend Development

```bash
cd frontend
npm install
npm run dev
```

### Tests

```bash
mvn test
```

### Docker and Deployment

- Deployment guide: `docs/Docker部署说明.md`
- One-click upload package scripts:
  - Windows: `.\scripts\package-upload.ps1`
  - Linux/macOS: `./scripts/package-upload.sh`

---

## Open Source Notes

- License: MIT (see `LICENSE`)
- Do not commit secrets:
  - API keys
  - database passwords
  - local-only config files

Use environment variables for sensitive values in production.


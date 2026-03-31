# superagent

Spring AI based fitness assistant project, including:

- main application with RAG and tool-calling
- MCP client integration
- MCP image-search server submodule (`superagent-imgsearch-mcp-server`)

## Open Source Notes

- License: MIT (see `LICENSE`)
- Do not commit secrets:
  - API keys
  - database passwords
  - local-only config files

This repository uses environment variables for sensitive values.

## Required Environment Variables

- `DASHSCOPE_API_KEY`
- `SEARCH_API_KEY` (optional if web search tool is used)
- `POSTGRES_PASSWORD` (if using local postgres)
- `PEXELS_API_KEY` (for image search MCP server)

## Run Main App

```bash
mvn spring-boot:run
```

## Run MCP Image Search Server

```bash
cd superagent-imgsearch-mcp-server
mvn spring-boot:run -Dspring-boot.run.profiles=stdio
```

## Test

```bash
mvn test
```

